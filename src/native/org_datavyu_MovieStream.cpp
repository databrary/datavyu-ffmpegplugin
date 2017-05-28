#include "org_datavyu_MovieStream.h"
#include "ImageBuffer.h"
#include "AudioBuffer.h"
#include "Logger.h"
#include "AVLogger.h"
#include <jni.h>
#include <cstdio>
#include <cmath>
#include <cassert>
#include <vector>
#include <thread>
#include <chrono>
#include <algorithm>
#include <cstdlib>
#include <sstream>

// TODO: Implement when only audio/image stream is present. At the moment we 
// assume both streams exist.
extern "C" {
	#include <libavcodec/avcodec.h> // codecs
	#include <libavformat/avformat.h> // formats
	#include <libswscale/swscale.h> // sampling of image
	#include <libswresample/swresample.h> // resampling of audio
	#include <libavutil/error.h> // error codes
}

// Florian Raudies, Mountain View, CA.
// vcvarsall.bat x64
// cl org_datavyu_MovieStream.cpp /Fe"..\..\lib\MovieStream" /I"C:\Users\Florian\FFmpeg-release-3.2" /I"C:\Program Files\Java\jdk1.8.0_91\include" /I"C:\Program Files\Java\jdk1.8.0_91\include\win32" /showIncludes /MD /LD /link "C:\Program Files\Java\jdk1.8.0_91\lib\jawt.lib" "C:\Users\Florian\FFmpeg-release-3.2\libavcodec\avcodec.lib" "C:\Users\Florian\FFmpeg-release-3.2\libavformat\avformat.lib" "C:\Users\Florian\FFmpeg-release-3.2\libavutil\avutil.lib" "C:\Users\Florian\FFmpeg-release-3.2\libswscale\swscale.lib" "C:\Users\Florian\FFmpeg-release-3.2\libswresample\swresample.lib"

#define AV_SYNC_THRESHOLD 0.01
#define AV_NOSYNC_THRESHOLD 10.0
#define PTS_DELTA_THRESHOLD 3
#define MAX_AUDIO_FRAME_SIZE 192000

/*******************************************************************************
 * Basic information about the movie.
 ******************************************************************************/

/** Width of the image in pixels. */
int width = 0; 

/** Height of the image in pixels. */
int height = 0;

/** Number of color channels. */
int	nChannel = 3;

/** Duration of the video in seconds. */
double duration = 0;


/*******************************************************************************
 * Variables to playback the image stream within the video file.
 ******************************************************************************/

/** Audio and video format context. */
AVFormatContext	*pFormatCtx = nullptr;

/** Index of the 1st video stream. */
int	iVideoStream = -1;

/** Index of the 1st audio stream. */
int	iAudioStream = -1;

/** Done with decoding. Set to 0 on reset. */
int	doneDecoding = 0;

/** Video stream. */
AVStream *pVideoStream = nullptr;

/** Video codec context. */
AVCodecContext *pVideoCodecCtx = nullptr;

/** Video codec. */
AVCodec *pVideoCodec = nullptr;

/** Video frame read from stream. */
AVFrame *pVideoFrame = nullptr;

/** Video frame displayed (wrapped by buffer). */
AVFrame *pVideoFrameShow = nullptr;				

/** Dictionary is a key-value store. */
AVDictionary *pOptsDict = nullptr;

/** Scaling context from source to target image format and size. */
struct SwsContext *pSwsImageCtx	= nullptr;

/** Decoding thread that decodes frames and is started when opening a video. */
std::thread	*decodeFrame = nullptr;

/** Quit the decoding thread. */
bool quit = false;

/** Flag indicates that we loaded a movie. */
bool loadedMovie = false;

/** Pointer to the image buffer. */
ImageBuffer *pImageBuffer = nullptr;

/** The stream reached the end of the file. */
bool eof = false;


/*******************************************************************************
 * Variables to playback the audio stream within the movie file.
 ******************************************************************************/

/** The audio buffer data. This pointer is shared with the java buffer. */
uint8_t	*pAudioBufferData = nullptr;

/** Length of the audio buffer data in bytes. Set to 0 on reset. */
int lenAudioBufferData = 0;

/** The audio input codec context (before transcoding). */
AVCodecContext *pAudioInCodecCtx = nullptr;

/** The audio output codec context (after transcoding). */
AVCodecContext *pAudioOutCodecCtx = nullptr;

/** Context for resampling of the audio signal. */
SwrContext *pResampleCtx = nullptr;

/** Buffer for the audio data. */
AudioBuffer	*pAudioBuffer = nullptr;


/*******************************************************************************
 * Variable that control the playback speed and reverse playback.
 ******************************************************************************/
/** Toggles the direction of playback. */
bool toggle	= false;

/** Controls the speed of playback as factor of the original playback speed. */
double speed = 1;

/** Last present time stamp. Set to 0 on reset. */
int64_t lastPts = 0;

/** Difference between time stamps. Set to 0 on reset. */
int64_t	deltaPts = 0;

/** Average of time stamp intervals. Set to 1 on reset. Used for reverse seek.*/
int64_t	avgDeltaPts = 1;

/** Last written time stamp. Set to 0 on reset. */
int64_t	lastWritePts = 0;

/** Accumulated difference. Set to 0 on reset. */
double diff = 0;

/** The system clock's last time. */
std::chrono::high_resolution_clock::time_point lastTime;


/*******************************************************************************
 * Variables that control random seeking.
 ******************************************************************************/
/** Flag to initiate a random seek request. */
bool seekReq = false;

/** The time stamp that should be present after a random seek. */
int64_t seekPts = 0;

/** Flag that indicates the direction of seeking etc. */
int seekFlags = 0;

/** Logger. */
Logger *pLogger = nullptr;


/*******************************************************************************
 * Parameters for the viewing window.
 ******************************************************************************/

/** Width of the viewing window. Set to 0 on reset. */
int	widthView = 0;

/** Height of the viewing window. Set to 0 on reset. */
int	heightView = 0;

/** Horizontal starting position of the viewing window. Set to 0 on reset. */
int	x0View = 0;

/** Vertical starting position of the viewing window. Set to 0 on reset. */
int	y0View = 0;

/** True if a viewing window is set, otherwise false. Set to false on reset. */
bool doView	= false;


/*******************************************************************************
 * Structure that holds the parameters for the audio format.
 ******************************************************************************/
struct AudioFormat {
	/** The name of the encoding. */
	std::string encodingName;

	/** Bytes are encoded in big endian. */
	bool bigEndian;

	/** The sample rate of the audio signal. */
	float sampleRate;

	/** The number of bits per sample. Often 8 bit/sample or 16 bit/sample. */
	int sampleSizeInBits;

	/** The number of channels. */
	int channels;

	/** The size of an audio frame. */
	float frameSize;

	/** The audio frame rate for the replay of the audio signal.*/
	float frameRate;
};


static const char *getErrorText(const int error) {
    static char error_buffer[256];
    av_strerror(error, error_buffer, sizeof(error_buffer));
    return error_buffer;
}

static int initResampler(AVCodecContext *inCodecCtx, AVCodecContext *outCodecCtx,
                         SwrContext **pResampleCtx) {
    int errNo;

    /**
     * Create a resampler context for the conversion.
     * Set the conversion parameters.
     * Default channel layouts based on the number of channels
     * are assumed for simplicity (they are sometimes not detected
     * properly by the demuxer and/or decoder).
     */
    *pResampleCtx = swr_alloc_set_opts(NULL,
                                       av_get_default_channel_layout(outCodecCtx->channels),
                                       outCodecCtx->sample_fmt,
                                       outCodecCtx->sample_rate,
                                       av_get_default_channel_layout(inCodecCtx->channels),
                                       inCodecCtx->sample_fmt,
                                       inCodecCtx->sample_rate,
                                       0, NULL);
    if (!*pResampleCtx) {
        fprintf(stderr, "Could not allocate resample context\n");
        return AVERROR(ENOMEM);
    }
    /**
    * Perform a sanity check so that the number of converted samples is
    * not greater than the number of samples to be converted.
    * If the sample rates differ, this case has to be handled differently
    */
    assert(outCodecCtx->sample_rate == inCodecCtx->sample_rate);

    /** Open the resampler with the specified parameters. */
    if ((errNo = swr_init(*pResampleCtx)) < 0) {
		fprintf(stderr, "Unable to open resample context. Error: '%s'.\n",
				getErrorText(errNo));
        swr_free(pResampleCtx);
        return errNo;
    }
    return 0;
}

/**
 * Initialize a temporary storage for the specified number of audio samples.
 * The conversion requires temporary storage due to the different formats.
 * The number of audio samples to be allocated is specified in frameSize.
 */
static int initConvertedSamples(uint8_t ***convertedInSamples,
                                AVCodecContext *outCodecCtx,
                                int frameSize) {
    int errNo;

    /**
     * Allocate as many pointers as there are audio channels.
     * Each pointer will later point to the audio samples of the corresponding
     * channels (although it may be NULL for interleaved formats).
     */
    if (!(*convertedInSamples = (uint8_t**)calloc(outCodecCtx->channels,
                                            sizeof(**convertedInSamples)))) {
        fprintf(stderr, "Could not allocate converted input sample pointers.\n");
        return 1;
    }

    /**
     * Allocate memory for the samples of all channels in one consecutive
     * block for convenience.
     */
    if ((errNo = av_samples_alloc(*convertedInSamples, NULL,
                                  outCodecCtx->channels,
                                  frameSize,
                                  outCodecCtx->sample_fmt, 0)) < 0) {
		fprintf(stderr, "Could not allocate converted input samples. Error: '%s')\n",
						getErrorText(errNo));
        av_freep(&(*convertedInSamples)[0]);
        free(*convertedInSamples);
        return errNo;
    }
    return 0;
}

/**
 * Convert the input audio samples into the output sample format.
 * The conversion happens on a per-frame basis, the size of which is specified
 * by frameSize.
 */
static int convertSamples(const uint8_t **inData, uint8_t **convertedData, 
						   const int frameSize, SwrContext *pResampleCtx) {
    int errNo;
    /** Convert samples using the resampler context. */
    if ((errNo = swr_convert(pResampleCtx,
                             convertedData, frameSize,
                             inData, frameSize)) < 0) {
        fprintf(stderr, "Could not convert input samples. Error '%s')\n",
                getErrorText(errNo));
        return errNo;
    }
    return 0;
}

// decodes packet from queue into audioByteBuffer
int audioDecodeFrame(AVCodecContext *pAudioInCodecCtx, uint8_t *audioByteBuffer, 
					 int bufferSize) {
	static AVPacket pkt;
	static uint8_t *pAudioPktData = nullptr;
	static int audioPktSize = 0;
	static AVFrame frame;
	uint8_t **convertedInSamples = nullptr;
	int dataLen, dataSize = 0;
	int errNo;

	for(;;) {
		while (audioPktSize > 0) {
			int gotFrame = 0;
			dataLen = avcodec_decode_audio4(pAudioInCodecCtx, &frame, &gotFrame, &pkt);
			if (dataLen < 0) {
				/* if error, skip frame */
				audioPktSize = 0;
				break;
			}
			
			pAudioPktData += dataLen;
			audioPktSize -= dataLen;
			dataSize = 0;

			if (gotFrame) {
				dataSize = av_samples_get_buffer_size(NULL,
								pAudioOutCodecCtx->channels,
								frame.nb_samples,
								pAudioOutCodecCtx->sample_fmt,
								1);

				assert(dataSize <= bufferSize);

				if ((errNo = initConvertedSamples(&convertedInSamples, 
													pAudioOutCodecCtx, 
													frame.nb_samples)) < 0) {
					// clean-up
					if (convertedInSamples) {
						av_freep(&convertedInSamples[0]);
						free(convertedInSamples);					
					}
					return errNo;
				}

				if ((errNo = convertSamples((const uint8_t**)frame.extended_data, 
											 convertedInSamples, 
											 frame.nb_samples, pResampleCtx)) < 0) {
					// clean-up
					if (convertedInSamples) {
						av_freep(&convertedInSamples[0]);
						free(convertedInSamples);					
					}
					return errNo;
				}

				memcpy(audioByteBuffer, convertedInSamples[0], dataSize);

				av_freep(&convertedInSamples[0]);
				free(convertedInSamples);
			}

			if (dataSize <= 0) {
				/* No data yet, get more frames */
				continue;
			}
			/* We have data, return it and come back for more later */
			return dataSize;
		}
		if (pkt.data)
			av_free_packet(&pkt);

		if (quit) {
			return -1;
		}
		if (doneDecoding && pAudioBuffer->empty()) {
			return -1;
		}
		if (pAudioBuffer->get(&pkt) < 0) {
			return -1;
		}
		pAudioPktData = pkt.data;
		audioPktSize = pkt.size;
	}
}

int setAudioFormat(JNIEnv *env, jobject jAudioFormat, 
				   const AudioFormat& audioFormat) {
	// Variables to pull from the jobject
	jclass audioFormatClass = nullptr;
	jfieldID encodingId = nullptr;
	jobject encoding = nullptr;
	jclass encodingClass = nullptr;
	jfieldID encodingNameId = nullptr;
	jstring jEncodingName = nullptr;
	jfieldID bigEndianId = nullptr;
	jfieldID sampleRateId = nullptr;
	jfieldID sampleSizeInBitsId = nullptr;
	jfieldID channelsId = nullptr;
	jfieldID frameSizeId = nullptr;
	jfieldID frameRateId = nullptr;

	audioFormatClass = env->GetObjectClass(jAudioFormat);

	// Get the audio format
	encodingId = env->GetFieldID(audioFormatClass, 
					"encoding", "Ljavax/sound/sampled/AudioFormat$Encoding;");
	if (encodingId == nullptr) {
		fprintf(stderr, "Could not find 'encoding' attribute in AudioFormat.\n");
		return (jint) AVERROR_INVALIDDATA;
	}

	encoding = env->GetObjectField(jAudioFormat, encodingId);
	if (encoding == nullptr) {
		fprintf(stderr, "Could not find value for 'encoding' attribute in AudioEncoding.\n");
		return (jint) AVERROR_INVALIDDATA;
	}
	encodingClass = env->GetObjectClass(encoding);

	// Set the encoding name
	encodingNameId = env->GetFieldID(encodingClass, "name", "Ljava/lang/String;");
	if (encodingNameId == nullptr) {
		fprintf(stderr, "Could not find 'name' in AudioFormat$Encoding");
		return (jint) AVERROR_INVALIDDATA;
	}
	jEncodingName = env->NewStringUTF(audioFormat.encodingName.c_str());
	env->SetObjectField(jAudioFormat, encodingNameId, jEncodingName);

	// Set endianess
	bigEndianId = env->GetFieldID(audioFormatClass, "bigEndian", "Z");
	if (bigEndianId == nullptr) {
		fprintf(stderr, "Could not find attribute 'bigEndian' in AudioEncoding.\n");
		return (jint) AVERROR_INVALIDDATA;		
	}
	env->SetBooleanField(jAudioFormat, bigEndianId, (jboolean) audioFormat.bigEndian);

	// Set sample rate
	sampleRateId = env->GetFieldID(audioFormatClass, "sampleRate", "F");
	if (sampleRateId == nullptr) {
		fprintf(stderr, "Could not find attribute 'sampleRate' in AudioEncoding.\n");
		return (jint) AVERROR_INVALIDDATA;
	}
	env->SetFloatField(jAudioFormat, sampleRateId, (jfloat) audioFormat.sampleRate);

	// Set sample size in bits
	sampleSizeInBitsId = env->GetFieldID(audioFormatClass, "sampleSizeInBits", "I");
	if (sampleSizeInBitsId == nullptr) {
		fprintf(stderr, "Could not find attribute 'sampleSizeInBits' in AudioEncoding.\n");
		return (jint) AVERROR_INVALIDDATA;
	}
	env->SetIntField(jAudioFormat, sampleSizeInBitsId, (jint) audioFormat.sampleSizeInBits);
	
	// Set the number of channels
	channelsId = env->GetFieldID(audioFormatClass, "channels", "I");
	if (channelsId == nullptr) {
		fprintf(stderr, "Could not find attribute 'channelsId' in AudioEncoding.\n");
		return (jint) AVERROR_INVALIDDATA;
	}
	env->SetIntField(jAudioFormat, channelsId, (jint) audioFormat.channels);

	// Set the frame size in Bytes
	frameSizeId = env->GetFieldID(audioFormatClass, "frameSize", "I");
	if (frameSizeId == nullptr) {
		fprintf(stderr, "Could not find attribute 'frameSize' in AudioEncoding.\n");
		return (jint) AVERROR_INVALIDDATA;
	}
	env->SetIntField(jAudioFormat, frameSizeId, (jint) audioFormat.frameSize);

	// Set the frame rate in Hertz
	frameRateId = env->GetFieldID(audioFormatClass, "frameRate", "F");
	if (frameRateId == nullptr) {
		fprintf(stderr, "Could not find attribute 'frameRate' in AudioEncoding.\n");
		return (jint) AVERROR_INVALIDDATA;
	}
	env->SetFloatField(jAudioFormat, frameRateId, (jfloat) audioFormat.frameRate);

	return 0; // No error
}

int getAudioFormat(JNIEnv *env, AudioFormat* audioFormat, const jobject& jAudioFormat) {
	// Variables to pull from the jobject
	jclass audioFormatClass = nullptr;
	jfieldID encodingId = nullptr;
	jobject encoding = nullptr;
	jclass encodingClass = nullptr;
	jfieldID encodingNameId = nullptr;
	const char* encodingName = nullptr;
	jstring jEncodingName = nullptr;
	jfieldID bigEndianId = nullptr;
	jfieldID sampleRateId = nullptr;
	jfieldID sampleSizeInBitsId = nullptr;
	jfieldID channelsId = nullptr;
	jfieldID frameSizeId = nullptr;
	jfieldID frameRateId = nullptr;

	audioFormatClass = env->GetObjectClass(jAudioFormat);
	// Get the audio format
	encodingId = env->GetFieldID(audioFormatClass, 
					"encoding", "Ljavax/sound/sampled/AudioFormat$Encoding;");

	if (encodingId == nullptr) {
		fprintf(stderr, "Could not find 'encoding' attribute in AudioFormat.\n");
		return (jint) AVERROR_INVALIDDATA;
	}
	encoding = env->GetObjectField(jAudioFormat, encodingId);
	if (encoding == nullptr) {
		fprintf(stderr, "Could not find value for 'encoding' attribute in AudioEncoding.\n");
		return (jint) AVERROR_INVALIDDATA;
	}
	encodingClass = env->GetObjectClass(encoding);

	// Get the encoding name
	encodingNameId = env->GetFieldID(encodingClass, "name", "Ljava/lang/String;");
	if (encodingNameId == nullptr) {
		fprintf(stderr, "Could not find 'name' in AudioFormat$Encoding");
		return (jint) AVERROR_INVALIDDATA;
	}
	jEncodingName = (jstring) env->GetObjectField(encoding, encodingNameId);
	encodingName = env->GetStringUTFChars(jEncodingName, 0);
	audioFormat->encodingName = std::string(encodingName, env->GetStringLength(jEncodingName));
	env->ReleaseStringUTFChars(jEncodingName, encodingName);

	// Get endianess
	bigEndianId = env->GetFieldID(audioFormatClass, "bigEndian", "Z");
	if (bigEndianId == nullptr) {
		fprintf(stderr, "Could not find attribute 'bigEndian' in AudioEncoding.\n");
		return (jint) AVERROR_INVALIDDATA;		
	}
	audioFormat->bigEndian = (bool) env->GetBooleanField(jAudioFormat, bigEndianId);

	// Get sample rate
	sampleRateId = env->GetFieldID(audioFormatClass, "sampleRate", "F");
	if (sampleRateId == nullptr) {
		fprintf(stderr, "Could not find attribute 'sampleRate' in AudioEncoding.\n");
		return (jint) AVERROR_INVALIDDATA;
	}
	audioFormat->sampleRate = (float) env->GetFloatField(jAudioFormat, sampleRateId);

	// Get sample size in bits
	sampleSizeInBitsId = env->GetFieldID(audioFormatClass, "sampleSizeInBits", "I");
	if (sampleSizeInBitsId == nullptr) {
		fprintf(stderr, "Could not find attribute 'sampleSizeInBits' in AudioEncoding.\n");
		return (jint) AVERROR_INVALIDDATA;
	}
	audioFormat->sampleSizeInBits = (int) env->GetIntField(jAudioFormat, sampleSizeInBitsId);
	
	// Get the number of channels
	channelsId = env->GetFieldID(audioFormatClass, "channels", "I");
	if (channelsId == nullptr) {
		fprintf(stderr, "Could not find attribute 'channelsId' in AudioEncoding.\n");
		return (jint) AVERROR_INVALIDDATA;
	}
	audioFormat->channels = (int) env->GetIntField(jAudioFormat, channelsId);

	// Get the frame size in Bytes
	frameSizeId = env->GetFieldID(audioFormatClass, "frameSize", "I");
	if (frameSizeId == nullptr) {
		fprintf(stderr, "Could not find attribute 'frameSize' in AudioEncoding.\n");
		return (jint) AVERROR_INVALIDDATA;
	}
	audioFormat->frameSize = (int) env->GetIntField(jAudioFormat, frameSizeId);

	// Get the frame rate in Hertz
	frameRateId = env->GetFieldID(audioFormatClass, "frameRate", "F");
	if (frameRateId == nullptr) {
		fprintf(stderr, "Could not find attribute 'frameRate' in AudioEncoding.\n");
		return (jint) AVERROR_INVALIDDATA;
	}
	audioFormat->frameRate = env->GetFloatField(jAudioFormat, frameRateId);
	return 0; // No error
}

/**
 * True if writing reached the start of the file. This happens in reverse mode.
 * We are not at the end yet if we are in reverse (last condition).
 */
bool atStartForWrite() {
	return pImageBuffer->isReverse() 
		&& lastWritePts <= pVideoStream->start_time+avgDeltaPts 
		&& !pImageBuffer->inReverse();
}

/**
 * True if reading reached the start of the file. This happens in reverse mode.
 */
bool atStartForRead() {
	return atStartForWrite() && pImageBuffer->empty();
}

/**
 * True if writing reached the end of the file. This happens in forward mode.
 */
bool atEndForWrite() {
	// return lastWritePts-pVideoStream->start_time >= pVideoStream->duration;
	// Duration is just an estimate and usually larger than the acutal number of frames.
	// I measured 8 - 14 additional frames that duration specifies.
	return !pImageBuffer->isReverse() && eof;
}

/**
 * True if reading reached the end of the file. This happens in forward mode.
 */
bool atEndForRead() {
	// The duration is not a reliable estimate for the end a video file.
	return !pImageBuffer->isReverse() && eof && pImageBuffer->empty();
}


bool isForwardPlayback() {
	return loadedMovie ? (jboolean) !pImageBuffer->isReverse() : true;
}

/**
 * Reads the next frame from the video stream and supports:
 * - Random seek through the variable seekReq.
 * - Toggeling the direction through the variable toggle.
 * - Automatically fills the buffer for backward play.
 *
 * For a seek this jumps to the next earlier keyframe than the current frame.
 * This method drops as many frames as there are between the keyframe and the
 * requested seek frame.
 * 
 * Decodes multiple AVPacket into an AVFrame and writes this one to the buffer.
 *
 */
void readNextFrame() {
	int frameFinished;
	bool reverseRefresh = true;
	AVPacket packet;
	while (!quit) {

		// Random seek.
		if (seekReq) {
			seekFlags |= AVSEEK_FLAG_BACKWARD;
			if (av_seek_frame(pFormatCtx, iVideoStream, seekPts, seekFlags) < 0) {
				pLogger->error("Random seek of %I64d pts, %I64d frames unsuccessful.", 
					seekPts, seekPts/avgDeltaPts);
			} else {
				pLogger->info("Random seek of %I64d pts, %I64d frames successful.", 
					seekPts, seekPts/avgDeltaPts);
				pImageBuffer->doFlush();
				// TODO: Flush the audio buffer if audio is present
				avcodec_flush_buffers(pVideoCodecCtx);
				lastWritePts = seekPts;
			}
			seekReq = false;
		}

		// Switch direction of playback.
		if (toggle) {
			std::pair<int,int> offsetDelta = pImageBuffer->toggle();
			int offset = offsetDelta.first;
			int delta = offsetDelta.second;
			int nShift = 0;

			pLogger->info("Toggle with offset %d and delta %d.", offset, delta);

			// Even if we may not seek backward it is safest to get the prior keyframe.
			seekFlags |= AVSEEK_FLAG_BACKWARD;
			if (pImageBuffer->isReverse()) {
				int maxDelta = (-pVideoStream->start_time+lastWritePts)/avgDeltaPts;
				delta = std::min(offset+delta, maxDelta) - offset;
				pImageBuffer->setBackwardAfterToggle(delta);
				nShift = -(offset + delta) + 1;
			} else {
				nShift = offset;
			}

			lastWritePts = seekPts = lastWritePts + nShift*avgDeltaPts;

			if (av_seek_frame(pFormatCtx, iVideoStream, seekPts, seekFlags) < 0) {
				pLogger->error("Toggle seek of %I64d pts, %I64d frames unsuccessful.", 
								seekPts, seekPts/avgDeltaPts);
			} else {
				pLogger->info("Toggle seek of %I64d pts, %I64d frames successful.", 
								seekPts, seekPts/avgDeltaPts);
				avcodec_flush_buffers(pVideoCodecCtx);
			}			
			toggle = false;			
		}

		// Check start or end before issuing seek request!
		if (atStartForWrite() || atEndForWrite()) {
			pLogger->info("Reached the start or end with seek %I64d pts, ",
				"%I64d frames and last write %I64d pts, %I64d frames.", 
				seekPts, seekPts/avgDeltaPts, lastWritePts, 
				lastWritePts/avgDeltaPts);
			std::this_thread::sleep_for(std::chrono::milliseconds(500));
			continue;
		}

		// Find next frame in reverse playback.
		if (pImageBuffer->seekReq()) {

			// Find the number of frames that can still be read
			int maxDelta = (-pVideoStream->start_time+lastWritePts)/avgDeltaPts;

			std::pair<int, int> offsetDelta = pImageBuffer->seekBackward();
			
			int offset = offsetDelta.first;
			int delta = offsetDelta.second;

			delta = std::min(offset+delta, maxDelta) - offset;

			pLogger->info("Seek frame for reverse playback with offset %d and "
							"min delta %d.", offset, delta);

			pImageBuffer->setBackwardAfterSeek(delta);

			seekFlags |= AVSEEK_FLAG_BACKWARD;
			int nShift = -(offset + delta) + 1;

			lastWritePts = seekPts = lastWritePts + nShift*avgDeltaPts;

			if (av_seek_frame(pFormatCtx, iVideoStream, seekPts, seekFlags) < 0) {
				pLogger->error("Reverse seek of %I64d pts, %I64d frames unsuccessful.", 
								seekPts, seekPts/avgDeltaPts);
			} else {
				pLogger->info("Reverse seek of %I64d pts, %I64d frames successful.", 
								seekPts, seekPts/avgDeltaPts);
				avcodec_flush_buffers(pVideoCodecCtx);
			}
		}

		// Read frame.
		int ret = av_read_frame(pFormatCtx, &packet);

		// Set eof for end of file.
		eof = ret == AVERROR_EOF;

		// Any error that is not eof.
		if (ret < 0 && !eof) {
			pLogger->error("Error:  %c, %c, %c, %c.\n",
				static_cast<char>((-ret >> 0) & 0xFF),
				static_cast<char>((-ret >> 8) & 0xFF),
				static_cast<char>((-ret >> 16) & 0xFF),
				static_cast<char>((-ret >> 24) & 0xFF));
			std::this_thread::sleep_for(std::chrono::milliseconds(500));

		// We got a frame! Let's decode it.
		} else {

			// Is this a packet from the video stream?
			if (packet.stream_index == iVideoStream) {
				
				// Decode the video frame.
				avcodec_decode_video2(pVideoCodecCtx, pVideoFrame, &frameFinished, &packet);
				
				// Did we get a full video frame?
				if(frameFinished) {

					// Set the presentation time stamp.
					int64_t readPts = pVideoFrame->pkt_pts;

					// Skip frames until we are at or beyond of the seekPts time stamp.
					if (readPts >= seekPts) {
					
						// Get the next writeable buffer. 
						// This may block and can be unblocked with a flush.
						AVFrame* pFrameBuffer = pImageBuffer->reqWritePtr();

						// Did we get a frame buffer?
						if (pFrameBuffer) {

							// Convert the image from its native format into RGB.
							sws_scale
							(
								pSwsImageCtx,
								(uint8_t const * const *)pVideoFrame->data,
								pVideoFrame->linesize,
								0,
								pVideoCodecCtx->height,
								pFrameBuffer->data,
								pFrameBuffer->linesize
							);
							pFrameBuffer->repeat_pict = pVideoFrame->repeat_pict;
							pFrameBuffer->pts = lastWritePts = readPts;
							pImageBuffer->cmplWritePtr();

							pLogger->info("Wrote %I64d pts, %I64d frames.", 
											lastWritePts, 
											lastWritePts/avgDeltaPts);
							pImageBuffer->printLog();
						}
					}

					// Reset frame container to initial state.
					av_frame_unref(pVideoFrame);
				}

				// Free the packet that was allocated by av_read_frame.
				av_free_packet(&packet);

			} else if (packet.stream_index == iAudioStream) {
				// Decode packet from audio stream
				pAudioBuffer->put(&packet); // packet is freed when consumed
			} else {
				av_free_packet(&packet);
			}
		}
	}
	doneDecoding = 1;
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_MovieStream_getStartTime0
(JNIEnv* env, jobject thisObject) {
	return (jdouble) loadedMovie ? pVideoStream->start_time 
		* av_q2d(pVideoStream->time_base) : 0;
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_MovieStream_getEndTime0
(JNIEnv* env, jobject thisObject) {
	return (jdouble) loadedMovie ? (pVideoStream->duration 
		+ pVideoStream->start_time) * av_q2d(pVideoStream->time_base) : 0;
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_MovieStream_getDuration0
(JNIEnv* env, jobject thisObject) {
	return (jdouble) duration;
}

JNIEXPORT jdouble JNICALL Java_org_datavyu_MovieStream_getCurrentTime
(JNIEnv* env, jobject thisObject) {
	return loadedMovie ? pVideoFrameShow->pts*av_q2d(pVideoStream->time_base) : 0;
}

JNIEXPORT void JNICALL Java_org_datavyu_MovieStream_setTime0
(JNIEnv* env, jobject thisObject, jdouble jTime) {
	if (loadedMovie) {
		lastWritePts = seekPts = ((int64_t)(jTime/(avgDeltaPts
												*av_q2d(pVideoStream->time_base))))
													*avgDeltaPts;
		seekReq = true;	
	}
}

JNIEXPORT void JNICALL Java_org_datavyu_MovieStream_setPlaybackSpeed0
(JNIEnv* env, jobject thisObject, jfloat jSpeed) {
	// TODO: May have to adjust differently for audio
	if (loadedMovie) {
		pImageBuffer->setNMinImages(1);
		toggle = pImageBuffer->isReverse() != (jSpeed < 0);
		speed = fabs(jSpeed);
	}
}

JNIEXPORT void JNICALL Java_org_datavyu_MovieStream_reset
(JNIEnv* env, jobject thisObject) {
	if (loadedMovie) {
		if (pImageBuffer->isReverse()) {
			// Seek to the end of the file.
			lastWritePts = seekPts = pVideoStream->duration - 2*avgDeltaPts;
			seekReq = true;
			pLogger->info("Rewind to end %I64d pts, %I64d frames.", seekPts, 
				seekPts/avgDeltaPts);
		} else {
			// Seek to the start of the file.
			lastWritePts = seekPts = pVideoStream->start_time;
			seekReq = true;
			pLogger->info("Rewind to start %I64d pts, %I64d frames.", seekPts,
				seekPts/avgDeltaPts);
		}
	}
}

JNIEXPORT void JNICALL Java_org_datavyu_MovieStream_close0
(JNIEnv* env, jobject thisObject) {

	if (loadedMovie) {

		pAudioBuffer->stop();

		doneDecoding = 0;

		// Set the quit flag for the decoding thread.
		quit = true;

		// Flush the image buffer buffer (which unblocks all readers/writers).
		pImageBuffer->doFlush();

		// Join the decoding thread with this one.
		decodeFrame->join();

		// Free the decoding thread.
		delete decodeFrame;
		
		// Free the image buffer buffer.
		delete pImageBuffer;
		pImageBuffer = nullptr;

		// Free the dictionary.
		av_dict_free(&pOptsDict);
		pOptsDict = nullptr;

		// Flush the buffers
		avcodec_flush_buffers(pVideoCodecCtx);

		// Close codec context
		avcodec_close(pVideoCodecCtx);
		pVideoCodecCtx = nullptr;

		// Free scaling context.
		sws_freeContext(pSwsImageCtx);
		pSwsImageCtx = nullptr;

		// Free the YUV frame
		av_free(pVideoFrame);
		pVideoFrame = nullptr;
		pVideoFrameShow = nullptr;

		delete pAudioBuffer;
		avcodec_close(pAudioInCodecCtx);
		avcodec_close(pAudioOutCodecCtx);
		swr_free(&pResampleCtx);
		free(pAudioBufferData);

		pAudioInCodecCtx = nullptr;
		pAudioOutCodecCtx = nullptr;
		pResampleCtx = nullptr;
		pAudioBufferData = nullptr;

		// Close the video file AFTER closing all codecs!!!
		avformat_close_input(&pFormatCtx);
		pFormatCtx = nullptr;

		// Set default values for movie information.
		loadedMovie = false;
		width = 0;
		height = 0;
		nChannel = 3;
		duration = 0;
		lastWritePts = 0;

		// Set default values for playback speed.
		toggle = false;
		speed = 1;
		lastPts = 0;
		deltaPts = 0;
		avgDeltaPts = 1;
		lastWritePts = 0;
		diff = 0;
		eof = false;

		// Reset value for seek request.
		seekReq = false;
		seekPts = 0;
		seekFlags = 0;

		// Reset variables from viewing window.
		widthView = 0;
		heightView = 0;
		x0View = 0;
		y0View = 0;
		doView = false;

		quit = false;
	}

	if (pLogger) {
		pLogger->info("Closed video and released resources.");
		delete pLogger;
		pLogger = nullptr;
	}
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_MovieStream_availableAudioFrame
(JNIEnv* env, jobject thisObject) {
	return loadedMovie ? !pAudioBuffer->empty() : false;
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_MovieStream_availableImageFrame
(JNIEnv* env, jobject thisObject) {
	return loadedMovie ? !(isForwardPlayback() && atEndForRead() 
					  || !isForwardPlayback() && atStartForRead()) : false;
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_MovieStream_loadNextAudioFrame
(JNIEnv* env, jobject thisObject) {
	int len = lenAudioBufferData; // get length of buffer
	uint8_t *data = pAudioBufferData; // get a write pointer.
	int decodeLen, audioSize;

	static uint8_t audioByteBuffer[(MAX_AUDIO_FRAME_SIZE * 3) / 2];
	static unsigned int audioBufferSize = 0;
	static unsigned int audioBufferIndex = 0;

	// If we are at the end of the end of the file then do not load another 
	// empty buffer
	if (doneDecoding && pAudioBuffer->empty()) {
		return false;
	}

	while (len > 0) {
		// We still need to read len bytes
		if (audioBufferIndex >= audioBufferSize) {
			/* We already sent all our data; get more */
			audioSize = audioDecodeFrame(pAudioInCodecCtx, audioByteBuffer, sizeof(audioByteBuffer));

			if (audioSize < 0) {
				/* If error, output silence */
				audioBufferSize = 1024; // arbitrary?
				memset(audioByteBuffer, 0, audioBufferSize); // set silience for the rest
			} else {
				audioBufferSize = audioSize;
			}
			audioBufferIndex = 0;
		}
		decodeLen = audioBufferSize - audioBufferIndex;
		
		if (decodeLen > len) {
			decodeLen = len;
		}

		memcpy(data, (uint8_t *)audioByteBuffer + audioBufferIndex, decodeLen);
		len -= decodeLen;
		data += decodeLen;
		audioBufferIndex += decodeLen;
	}
	return !quit;
}

JNIEXPORT jobject JNICALL Java_org_datavyu_MovieStream_getAudioBuffer
(JNIEnv *env, jobject thisObject, jint jSize) {
	lenAudioBufferData = jSize;
	pAudioBufferData = (uint8_t*) malloc(jSize);
	if (!pAudioBufferData) {
		fprintf(stderr, "Failed to allocate stream audio buffer.\n");
		return 0;
	}
	return env->NewDirectByteBuffer((void*) pAudioBufferData, jSize*sizeof(uint8_t));
}

JNIEXPORT jstring JNICALL Java_org_datavyu_MovieStream_getSampleFormat
(JNIEnv* env, jobject thisObject) {
	// sample formats http://ffmpeg.org/doxygen/trunk/group__lavu__sampfmts.html#gaf9a51ca15301871723577c730b5865c5
	AVSampleFormat sampleFormat = pAudioOutCodecCtx->sample_fmt;
	const char* name = av_get_sample_fmt_name(sampleFormat);
	return env->NewStringUTF(name);
}

JNIEXPORT jstring JNICALL Java_org_datavyu_MovieStream_getCodecName
(JNIEnv* env, jobject thisObject) {
	const char* name = pAudioOutCodecCtx->codec->name;
	return env->NewStringUTF(name);
}

JNIEXPORT jfloat JNICALL Java_org_datavyu_MovieStream_getSampleRate
(JNIEnv* env, jobject thisObject) {
	return pAudioOutCodecCtx->sample_rate;
}

JNIEXPORT jint JNICALL Java_org_datavyu_MovieStream_getSampleSizeInBits
(JNIEnv* env, jobject thisObject) {
	return pAudioOutCodecCtx->bits_per_coded_sample;
}

JNIEXPORT jint JNICALL Java_org_datavyu_MovieStream_getNumberOfSoundChannels
(JNIEnv* env, jobject thisObject) {
	return pAudioOutCodecCtx->channels;
}

JNIEXPORT jint JNICALL Java_org_datavyu_MovieStream_getFrameSize
(JNIEnv* env, jobject thisObject) {
	AVSampleFormat sampleFormat = pAudioOutCodecCtx->sample_fmt;
	return av_get_bytes_per_sample(sampleFormat);
}

JNIEXPORT jfloat JNICALL Java_org_datavyu_MovieStream_getFrameRate
(JNIEnv* env, jobject thisObject) {
	return pAudioOutCodecCtx->sample_rate;
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_MovieStream_bigEndian
(JNIEnv* env, jobject thisObject) {
    short int number = 0x1;
    char *numPtr = (char*)&number;
    return (numPtr[0] != 1);
}

JNIEXPORT jint JNICALL Java_org_datavyu_MovieStream_open0
(JNIEnv* env, jobject thisObject, jstring jFileName, jstring jVersion, 
 jobject jAudioFormat) {
	int errNo = 0;

	// Release resources first before loading another movie.
	if (loadedMovie) {
		Java_org_datavyu_MovieStream_close0(env, thisObject);
	}

	const char *fileName = env->GetStringUTFChars(jFileName, 0);
	const char *version = env->GetStringUTFChars(jVersion, 0);

	pLogger = new FileLogger("logger.txt");
	pLogger->info("Version: %s", version);
	//pLogger = new StreamLogger(&std::cerr);

	// Register all formats and codecs.
	av_register_all();

	// Open the video file.
	if ((errNo = avformat_open_input(&pFormatCtx, fileName, nullptr, nullptr)) != 0) {
		pLogger->error("Could not open file %s.", fileName);
		return (jint) errNo;
	}

	// Retrieve the stream information.
	if ((errNo = avformat_find_stream_info(pFormatCtx, nullptr)) < 0) {
		pLogger->error("Unable to find stream information for file %s.", 
						fileName);
		return (jint) errNo;
	}
  
	// Find the first video stream.
	iVideoStream = -1;
	for (int iStream = 0; iStream < pFormatCtx->nb_streams; ++iStream) {
		if (pFormatCtx->streams[iStream]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
			iVideoStream = iStream;
			break;
		}
	}

	if (iVideoStream == -1) {
		pLogger->error("Unable to find a video stream in file %s.", fileName);
		avformat_close_input(&pFormatCtx);
		return (jint) AVERROR_INVALIDDATA;
	}
	pLogger->info("Found video stream with id %d.", iVideoStream);

	// Find the first audio stream
	iAudioStream = -1;
	for (int iStream = 0; iStream < pFormatCtx->nb_streams; iStream++) {
		if (pFormatCtx->streams[iStream]->codec->codec_type == AVMEDIA_TYPE_AUDIO) {
			iAudioStream = iStream;
			break;
		}
	}

	if (iAudioStream == -1) {
		fprintf(stderr, "Could not find an audio stream.\n");
		avformat_close_input(&pFormatCtx);
		return AVERROR_INVALIDDATA;
	}
	pLogger->info("Found audio stream with id %d.", iAudioStream);

	// Get a poitner to the video stream.
	pVideoStream = pFormatCtx->streams[iVideoStream];

	// Get a pointer to the codec context for the video stream.
	pVideoCodecCtx = pVideoStream->codec;

	// Find the decoder for the video stream.
	pVideoCodec = avcodec_find_decoder(pVideoCodecCtx->codec_id);
	if (pVideoCodec == nullptr) {
		pLogger->error("Unsupported codec for file %s.", fileName);
		return (jint) AVERROR_DECODER_NOT_FOUND;
	}

	// Open codec.
	if ((errNo = avcodec_open2(pVideoCodecCtx, pVideoCodec, &pOptsDict)) < 0) {
		pLogger->error("Unable to open codec for file %s.", fileName);
		return (jint) errNo;
	}
	
	// Log that opened a file.
	pLogger->info("Opened file %s.", fileName);

	// Dump information about file onto standard error.
	std::string info = log_av_format(pFormatCtx, 0, fileName, 0);
	std::istringstream lines(info);
    std::string line;
    while (std::getline(lines, line)) {
		pLogger->info("%s", line.c_str());
    }

	// Allocate video frame.
	pVideoFrame = av_frame_alloc();

	// Initialize the color model conversion/rescaling context.
	pSwsImageCtx = sws_getContext
		(
			pVideoCodecCtx->width,
			pVideoCodecCtx->height,
			pVideoCodecCtx->pix_fmt,
			pVideoCodecCtx->width,
			pVideoCodecCtx->height,
			AV_PIX_FMT_RGB24,
			SWS_BILINEAR,
			nullptr,
			nullptr,
			nullptr
		);

	// Initialize the widht, height, and duration.
	width = pVideoCodecCtx->width;
	height = pVideoCodecCtx->height;
	duration = pVideoStream->duration*av_q2d(pVideoStream->time_base);
	pLogger->info("Duration of movie %d x %d pixels is %2.3f seconds, %I64d pts.", 
				  width, height, duration, pVideoStream->duration);
	pLogger->info("Time base %2.5f.", av_q2d(pVideoStream->time_base));

	// Initialize the delta pts using the average frame rate and the average pts.
	avgDeltaPts = deltaPts = (int64_t)(1.0/(av_q2d(pVideoStream->time_base)
										*av_q2d(pVideoStream->avg_frame_rate)));
	pLogger->info("Average delta %I64d pts.", avgDeltaPts);

	// Initialize the image buffer.
	pImageBuffer = new ImageBuffer(width, height, avgDeltaPts, pLogger);

	// *************************************************************************
	// Work on audio
	// *************************************************************************
	AVCodec *aInCodec = nullptr;
	AVCodec *aOutCodec = nullptr;

	struct AudioFormat audioFormat;
	if ((errNo = getAudioFormat(env, &audioFormat, jAudioFormat)) < 0) {
		return errNo;
	}

	AVSampleFormat sampleFormat;
	if (strcmp("PCM_UNSIGNED", audioFormat.encodingName.c_str()) == 0) {
		sampleFormat = AV_SAMPLE_FMT_U8;
	} else if (strcmp("PCM_SIGNED", audioFormat.encodingName.c_str()) == 0) {
		sampleFormat = AV_SAMPLE_FMT_S16;
	} else {
		fprintf(stderr, "Encoding %s is not supported.\n", audioFormat.encodingName.c_str());
		return (jint) AVERROR_INVALIDDATA;
	}
	AVCodecID codecId = av_get_pcm_codec(sampleFormat, audioFormat.bigEndian); // AV_CODEC_ID_PCM_U8, AV_CODEC_ID_PCM_S16LE

	pAudioInCodecCtx = pFormatCtx->streams[iAudioStream]->codec;
	if (!(aInCodec = avcodec_find_decoder(pAudioInCodecCtx->codec_id))) {
		fprintf(stderr, "Could not find input codec!\n");
		avformat_close_input(&pFormatCtx);
		return AVERROR_EXIT;
	}

	if ((errNo = avcodec_open2(pAudioInCodecCtx, aInCodec, NULL)) < 0) {
		fprintf(stderr, "Could not open audio codec. Error: '%s'.\n",
                getErrorText(errNo));
		avformat_close_input(&pFormatCtx);
		return errNo;
	}
	pAudioBuffer = new AudioBuffer();

	// create the output codec (alternative is stero: AV_CODEC_ID_PCM_U8)
	if (!(aOutCodec = avcodec_find_encoder(codecId))) {
		fprintf(stderr, "Could not create output codec.");
		avformat_close_input(&pFormatCtx);
		return AVERROR_EXIT;
	}

	// Allocate the output codec context
    if (!(pAudioOutCodecCtx = avcodec_alloc_context3(aOutCodec))) {
        fprintf(stderr, "Could not allocate an encoding output context\n");
		avformat_close_input(&pFormatCtx);
		return AVERROR_EXIT;
    }

    // Set the sample format
	pAudioOutCodecCtx->sample_fmt = sampleFormat;

	// Set channels, either from input jAudioFormat or from input codec
	if (audioFormat.channels != 0) {
		pAudioOutCodecCtx->channels = audioFormat.channels;
		pAudioOutCodecCtx->channel_layout = av_get_default_channel_layout(audioFormat.channels);
	} else {
		pAudioOutCodecCtx->channels = pAudioInCodecCtx->channels;
		pAudioOutCodecCtx->channel_layout = pAudioInCodecCtx->channel_layout;
		audioFormat.channels = pAudioInCodecCtx->channels;
	}

	// Set sample rate, either from input jAudioFormat or from input codec
	if (audioFormat.sampleRate != 0) {
		pAudioOutCodecCtx->sample_rate = (int) audioFormat.sampleRate;
	} else {
	    pAudioOutCodecCtx->sample_rate = pAudioInCodecCtx->sample_rate;
		audioFormat.sampleRate = pAudioInCodecCtx->sample_rate;
	}

	// Set bit rate
	if (audioFormat.frameRate != 0) {
		pAudioOutCodecCtx->bit_rate = (int) audioFormat.frameRate;
	} else {
	    pAudioOutCodecCtx->bit_rate = pAudioInCodecCtx->bit_rate;
		audioFormat.sampleRate = pAudioInCodecCtx->bit_rate;
	}
	
	// Set the frame size
	audioFormat.frameSize = av_get_bytes_per_sample(sampleFormat);

    // Open the encoder for the audio stream to use it later.
    if ((errNo = avcodec_open2(pAudioOutCodecCtx, aOutCodec, NULL)) < 0) {
		fprintf(stderr, "Could not open output codec. Error: '%s'.\n",
                getErrorText(errNo));
		avformat_close_input(&pFormatCtx);
        return errNo;
    }

    // Initialize the resampler to be able to convert audio sample formats. 
    if ((errNo = initResampler(pAudioInCodecCtx, pAudioOutCodecCtx, &pResampleCtx)) < 0) {
		avformat_close_input(&pFormatCtx);
		return errNo;
	}

	// bits_per_coded_sample is only set after opening the audio codec context
	audioFormat.sampleSizeInBits = pAudioOutCodecCtx->bits_per_coded_sample;

	if ((errNo = setAudioFormat(env, jAudioFormat, audioFormat)) < 0) {
		return errNo;
	}

	// Seek to the start of the file.
	lastWritePts = seekPts = pVideoStream->start_time;
	seekReq = true;

	// Start the decode thread.
	decodeFrame = new std::thread(readNextFrame);	
	pLogger->info("Started decoding thread!");

	// Set the value for loaded movie true.
	loadedMovie = true;

	// Free strings.
	env->ReleaseStringUTFChars(jFileName, fileName);
	env->ReleaseStringUTFChars(jVersion, version);

	return (jint) 0; // No error.
}

JNIEXPORT jint JNICALL Java_org_datavyu_MovieStream_getNumberOfColorChannels0
(JNIEnv* env, jobject thisObject) {
	return (jint) nChannel;
}

JNIEXPORT jint JNICALL Java_org_datavyu_MovieStream_getHeight0
(JNIEnv* env, jobject thisObject) {
	return (jint) height;
}

JNIEXPORT jint JNICALL Java_org_datavyu_MovieStream_getWidth0
(JNIEnv* env, jobject thisObject) {
	return (jint) width;
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_MovieStream_view
(JNIEnv* env, jobject thisObject, jint jx0, jint jy0, jint jwidth, jint jheight) {
	return (jboolean) false;
}

JNIEXPORT jobject JNICALL Java_org_datavyu_MovieStream_getFrameBuffer
(JNIEnv* env, jobject thisObject) {
	// No movie was loaded return nullptr.
	if (!loadedMovie) return 0;

	// We have a viewing window that needs to be supported.
	if (doView) {
		for (int iRow = 0; iRow < heightView; ++iRow) {
			for (int iCol = 0; iCol < widthView; ++iCol) {
				for (int iChannel = 0; iChannel < nChannel; ++iChannel) {
					int iSrc = ((y0View+iRow)*width + x0View+iCol)*nChannel + iChannel;
					int iDst = (iRow*widthView + iCol)*nChannel + iChannel;
					pVideoFrameShow->data[0][iDst] = pVideoFrameShow->data[0][iSrc];
				}
			}
		}
		return env->NewDirectByteBuffer((void*) pVideoFrameShow->data[0], 
									widthView*heightView*nChannel*sizeof(uint8_t));
	}

	// Construct a new direct byte buffer pointing to data from pVideoFrameShow.
	return env->NewDirectByteBuffer((void*) pVideoFrameShow->data[0], 
									width*height*nChannel*sizeof(uint8_t));
}

JNIEXPORT jint JNICALL Java_org_datavyu_MovieStream_loadNextImageFrame
(JNIEnv* env, jobject thisObject) {
	// No movie was loaded return -1.
	if (!loadedMovie) return -1;

	// Counts the number of frames that this method requested (could be 0, 1, 2).
	int nFrame = 0;

	// Get the next read pointer.
	AVFrame *pVideoFrameTmp = pImageBuffer->getReadPtr();

	// We received a frame (no flushing).
	if (pVideoFrameTmp) {

		// Retrieve the presentation time for this first frame.
		uint64_t firstPts = pVideoFrameTmp->pts;

		// Increase the number of read frames by one.
		nFrame++;

		// Initialize if the pts difference is above threshold as a result of a seek.
		bool init = std::labs(firstPts - lastPts) > PTS_DELTA_THRESHOLD*deltaPts;

		// Compute the difference for the presentation time stamps.
		double diffPts = init ? 0 : std::labs(firstPts - lastPts)
										/speed*av_q2d(pVideoStream->time_base);

		// Get the current time.
		auto time = std::chrono::high_resolution_clock::now();

		// Compute the time difference.
		double timeDiff = init ? 0 
			: std::chrono::duration_cast<std::chrono::microseconds>(time-lastTime).count()/1000000.0;

		// Compute the difference between times and pts.
		diff = init ? 0 : (diff + diffPts - timeDiff);

		// Calculate the delay that this display thread is required to wait.
		double delay = diffPts;

		// If the frame is repeated split this delay in half.
		if (pVideoFrameTmp->repeat_pict) {
			delay += delay/2;
		}

		// Compute the synchronization threshold (see ffplay.c)
		double syncThreshold = (delay > AV_SYNC_THRESHOLD) ? delay : AV_SYNC_THRESHOLD;

		// The time difference is within the no sync threshold.
		if(fabs(diff) < AV_NOSYNC_THRESHOLD) {

			// If our time difference is lower than the sync threshold, then skip a frame.
			if (diff <= -syncThreshold) {
				AVFrame *pVideoFrameTmp2 = pImageBuffer->getReadPtr();
				if (pVideoFrameTmp2) {
					pVideoFrameTmp = pVideoFrameTmp2;
					nFrame++;
				}

			// If the time difference is within -syncThreshold ... +0 then show frame instantly.
			} else if (diff < 0) {
				delay = 0;

			// If the time difference is greater than the syncThreshold increase the delay.
			} else if (diff >= syncThreshold) {
				delay *= 2;
			}
		}

		// Save values for next call.
		deltaPts = init ? (int64_t)(1.0/(av_q2d(pVideoStream->time_base)
								*av_q2d(pVideoStream->avg_frame_rate))) 
								: std::labs(firstPts - lastPts);
		lastPts = firstPts; // Need to use the first pts.
		lastTime = time;

		// Delay read to keep the desired frame rate.
		if (delay > 0) {
			std::this_thread::sleep_for(std::chrono::milliseconds((int)(delay*1000+0.5)));
		}

		// Update the pointer for the show frame.
		pVideoFrameShow = pVideoFrameTmp;

		// Log that we displayed a frame.
		pLogger->info("Display pts %I64d.", pVideoFrameShow->pts/avgDeltaPts);
	}

	// Return the number of read frames (not neccesarily all are displayed).
	return (jint) nFrame;
}
