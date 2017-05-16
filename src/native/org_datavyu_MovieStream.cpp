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
			}
		}
	}
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

}

JNIEXPORT void JNICALL Java_org_datavyu_MovieStream_setPlaybackSpeed0
(JNIEnv* env, jobject thisObject, jfloat jSpeed) {

}

JNIEXPORT void JNICALL Java_org_datavyu_MovieStream_reset
(JNIEnv* env, jobject thisObject) {

}

JNIEXPORT void JNICALL Java_org_datavyu_MovieStream_close0
(JNIEnv* env, jobject thisObject) {

	if (loadedMovie) {

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

		// Close the video file
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

	pLogger->info("Closed video and released resources.");

	if (pLogger) {
		delete pLogger;
		pLogger = nullptr;
	}
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_MovieStream_availableAudioFrame
(JNIEnv* env, jobject thisObject) {
	return (jboolean) false;
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_MovieStream_availableImageFrame
(JNIEnv* env, jobject thisObject) {
	return !(isForwardPlayback() && atEndForRead() 
		|| !isForwardPlayback() && atStartForRead());
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_MovieStream_loadNextAudioFrame
(JNIEnv* env, jobject thisObject) {
	return false;
}

JNIEXPORT jobject JNICALL Java_org_datavyu_MovieStream_getAudioBuffer
(JNIEnv *env, jobject thisObject, jint jSize) {
	return 0;
}

JNIEXPORT jstring JNICALL Java_org_datavyu_MovieStream_getSampleFormat
(JNIEnv* env, jobject thisObject) {
	return 0;
}

JNIEXPORT jstring JNICALL Java_org_datavyu_MovieStream_getCodecName
(JNIEnv* env, jobject thisObject) {
	return 0;
}

JNIEXPORT jfloat JNICALL Java_org_datavyu_MovieStream_getSampleRate
(JNIEnv* env, jobject thisObject) {
	return 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_MovieStream_getSampleSizeInBits
(JNIEnv* env, jobject thisObject) {
	return 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_MovieStream_getNumberOfSoundChannels
(JNIEnv* env, jobject thisObject) {
	return 0;
}

JNIEXPORT jint JNICALL Java_org_datavyu_MovieStream_getFrameSize
(JNIEnv* env, jobject thisObject) {
	return 0;
}

JNIEXPORT jfloat JNICALL Java_org_datavyu_MovieStream_getFrameRate
(JNIEnv* env, jobject thisObject) {
	return 0;
}

JNIEXPORT jboolean JNICALL Java_org_datavyu_MovieStream_bigEndian
(JNIEnv* env, jobject thisObject) {
	return false;
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
		return (jint) AVERROR_INVALIDDATA;
	}

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

	// Seek to the start of the file.
	lastWritePts = seekPts = pVideoStream->start_time;
	seekReq = true;

	// Start the decode thread.
	decodeFrame = new std::thread(readNextFrame);	
	pLogger->info("Started decoding thread!");

	// Set the value for loaded move true.
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
