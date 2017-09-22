#include "AudioPlayer.h"
#include "AudioBuffer.h"
#include <stdio.h>
#include <assert.h>
#include <thread> // std::thread
#include <string>

extern "C" {
	#include <libavcodec/avcodec.h>
	#include <libavformat/avformat.h>
	#include <libswresample/swresample.h>
}

// Florian Raudies, 04/09/2017, Mountain View, CA.
// vcvarsall.bat x64
// cl AudioPlayer.cpp /Fe"..\..\lib\AudioPlayer" /I"C:\Users\Florian\FFmpeg-release-3.2" /I"C:\Program Files\Java\jdk1.8.0_91\include" /I"C:\Program Files\Java\jdk1.8.0_91\include\win32" /showIncludes /MD /LD /link "C:\Program Files\Java\jdk1.8.0_91\lib\jawt.lib" "C:\Users\Florian\FFmpeg-release-3.2\libavcodec\avcodec.lib" "C:\Users\Florian\FFmpeg-release-3.2\libavformat\avformat.lib" "C:\Users\Florian\FFmpeg-release-3.2\libavutil\avutil.lib" "C:\Users\Florian\FFmpeg-release-3.2\libswresample\swresample.lib"
// To view the function handles in the dll use the command line argument:
//	dumpbin /exports ..\..\lib\AudioPlayer.dll
// To find out about the compiled class methods from java use the tool javap in 
// jdk1.8.0_91/jre/bin
// Examples:
//	javap javax.sound.sampled.AudioFormat
//  javap javax.sound.sampled.AudioFormat$Encoding

#define MAX_AUDIO_FRAME_SIZE 192000

/**
 * Convert an error code into a text message.
 * @param error Error code to be converted
 * @return Corresponding error text (not thread-safe)
 */
static const char *getErrorText(const int error) {
    static char error_buffer[256];
    av_strerror(error, error_buffer, sizeof(error_buffer));
    return error_buffer;
}

int				quit				= 0;
int				lenAudioBufferData	= 0;
int             iAudioStream		= -1;
int				doneDecoding		= 0;
bool			loadedMovie			= false;

uint8_t			*pAudioBufferData		= nullptr;
AVFormatContext *pFormatCtx				= nullptr;
AVCodecContext  *pAudioInCodecCtx		= nullptr;
AVCodecContext	*pAudioOutCodecCtx		= nullptr;
std::thread		*decodeAudio			= nullptr;
SwrContext		*pResampleCtx			= nullptr;
AudioBuffer		*pAudioBuffer			= nullptr;

struct AudioFormat {
	std::string encodingName;
	bool bigEndian;
	float sampleRate;
	int sampleSizeInBits;
	int channels;
	float frameSize;
	float frameRate;
};

static int initResampler(AVCodecContext *inCodecCtx,
                         AVCodecContext *outCodecCtx,
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
        return 1;
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
        return 1;
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

// allocate buffer.
JNIEXPORT jobject JNICALL Java_AudioPlayer_getAudioBuffer
(JNIEnv *env, jobject thisObject, jint nByte) {
	lenAudioBufferData = nByte;
	pAudioBufferData = (uint8_t*) malloc(nByte);
	if (!pAudioBufferData) {
		fprintf(stderr, "Failed to allocate stream audio buffer.\n");
		return 0;
	}
	return env->NewDirectByteBuffer((void*) pAudioBufferData, nByte*sizeof(uint8_t));
}

JNIEXPORT jboolean JNICALL Java_AudioPlayer_loadNextFrame(JNIEnv *env, jobject thisObject) {

	int len = lenAudioBufferData; // get length of buffer
	uint8_t *data = pAudioBufferData; // get a write pointer
	int decodeLen, audioSize;  

	static uint8_t audioByteBuffer[(MAX_AUDIO_FRAME_SIZE * 3) / 2];
	static unsigned int audioBufferSize = 0;
	static unsigned int audioBufferIndex = 0;

	// If we are at the end of the end of the file then do not load another 
	// empty buffer
	if (doneDecoding || pAudioBuffer->empty()) {
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
	return quit == 0;
}

void decodeLoop() {
	AVPacket packet;
	while (!quit && av_read_frame(pFormatCtx, &packet) >= 0) {
		if (packet.stream_index == iAudioStream) {
			// Decode packet from audio stream
			pAudioBuffer->put(&packet);
		} else {
			av_free_packet(&packet);
		}
	}
	// We are doneDecoding
	doneDecoding = 1;
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


JNIEXPORT jint JNICALL Java_AudioPlayer_loadAudio
(JNIEnv *env, jobject thisObject, jstring jFileName, jobject jAudioFormat) {
	if (loadedMovie) {
		Java_AudioPlayer_release(env, thisObject);
	}
	const char *fileName = env->GetStringUTFChars(jFileName, 0);
	int iStream;
	AVCodec *aInCodec = nullptr;
	AVCodec *aOutCodec = nullptr;
	int errNo = 0;
	struct AudioFormat audioFormat;
	if ((errNo = getAudioFormat(env, &audioFormat, jAudioFormat)) < 0) {
		return errNo;
	}
	AVCodecID codecId;
	AVSampleFormat sampleFormat;
	if (strcmp("PCM_UNSIGNED", audioFormat.encodingName.c_str()) == 0) {
		sampleFormat = AV_SAMPLE_FMT_U8;
	} else if (strcmp("PCM_SIGNED", audioFormat.encodingName.c_str()) == 0) {
		sampleFormat = AV_SAMPLE_FMT_S16;
	} else {
		fprintf(stderr, "Encoding %s is not supported.\n", audioFormat.encodingName.c_str());
		return (jint) AVERROR_INVALIDDATA;
	}
	codecId = av_get_pcm_codec(sampleFormat, audioFormat.bigEndian); // AV_CODEC_ID_PCM_U8, AV_CODEC_ID_PCM_S16LE

	// Register all formats and codecs
	av_register_all();

	// Open video file
	if ((errNo = avformat_open_input(&pFormatCtx, fileName, NULL, NULL)) < 0) {
		fprintf(stderr, "Could not open input file '%s'. Error: '%s'.\n",
                fileName, getErrorText(errNo));
		return errNo;
	}

	// Retrieve stream information
	if ((errNo = avformat_find_stream_info(pFormatCtx, NULL)) < 0) {
		fprintf(stderr, "Could not find stream information. Error: '%s'.\n",
                fileName, getErrorText(errNo));
		avformat_close_input(&pFormatCtx);
		return errNo;
	}

	// Dump information about file onto standard error
	av_dump_format(pFormatCtx, 0, fileName, 0);

	// Find the first audio stream
	iAudioStream = -1;
	for (iStream = 0; iStream < pFormatCtx->nb_streams; iStream++) {
		if (pFormatCtx->streams[iStream]->codec->codec_type==AVMEDIA_TYPE_AUDIO) {
			iAudioStream = iStream;
			break;
		}
	}

	if (iAudioStream == -1) {
		fprintf(stderr, "Could not find an audio stream.\n");
		avformat_close_input(&pFormatCtx);
		return AVERROR_EXIT;
	}

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

    /** Open the encoder for the audio stream to use it later. */
    if ((errNo = avcodec_open2(pAudioOutCodecCtx, aOutCodec, NULL)) < 0) {
		fprintf(stderr, "Could not open output codec. Error: '%s'.\n",
                getErrorText(errNo));
		avformat_close_input(&pFormatCtx);
        return errNo;
    }

    /** Initialize the resampler to be able to convert audio sample formats. */
    if ((errNo = initResampler(pAudioInCodecCtx, pAudioOutCodecCtx, &pResampleCtx)) < 0) {
		avformat_close_input(&pFormatCtx);
		return errNo;
	}

	// bits_per_coded_sample is only set after opening the audio codec context
	audioFormat.sampleSizeInBits = pAudioOutCodecCtx->bits_per_coded_sample;

	if ((errNo = setAudioFormat(env, jAudioFormat, audioFormat)) < 0) {
		return errNo;
	}

	decodeAudio = new std::thread(decodeLoop);
	env->ReleaseStringUTFChars(jFileName, fileName);

	loadedMovie = true;

	return 0; // No error
}

JNIEXPORT jstring JNICALL Java_AudioPlayer_getSampleFormat
(JNIEnv *env, jobject thisObject) {
	// sample formats http://ffmpeg.org/doxygen/trunk/group__lavu__sampfmts.html#gaf9a51ca15301871723577c730b5865c5
	AVSampleFormat sampleFormat = pAudioOutCodecCtx->sample_fmt;
	const char* name = av_get_sample_fmt_name(sampleFormat);
	return env->NewStringUTF(name);
}

JNIEXPORT jstring JNICALL Java_AudioPlayer_getCodecName
(JNIEnv *env, jobject thisObject) {
	const char* name = pAudioOutCodecCtx->codec->name;
	return env->NewStringUTF(name);
}

JNIEXPORT jfloat JNICALL Java_AudioPlayer_getSampleRate
(JNIEnv *env, jobject thisObject) {
	return pAudioOutCodecCtx->sample_rate;
}

JNIEXPORT jint JNICALL Java_AudioPlayer_getSampleSizeInBits
(JNIEnv *env, jobject thisObject) {
	return pAudioOutCodecCtx->bits_per_coded_sample;
}

JNIEXPORT jint JNICALL Java_AudioPlayer_getNumberOfChannels
(JNIEnv *env, jobject thisObject) {
	return pAudioOutCodecCtx->channels;
}

JNIEXPORT jint JNICALL Java_AudioPlayer_getFrameSizeInBy
(JNIEnv *env, jobject thisObject) {
	AVSampleFormat sampleFormat = pAudioOutCodecCtx->sample_fmt;
	return av_get_bytes_per_sample(sampleFormat);
}

JNIEXPORT jfloat JNICALL Java_AudioPlayer_getFramesPerSecond
(JNIEnv *env, jobject thisObject) {
	return pAudioOutCodecCtx->sample_rate;
}

JNIEXPORT jboolean JNICALL Java_AudioPlayer_bigEndian
(JNIEnv *env, jobject thisObject) {
    short int number = 0x1;
    char *numPtr = (char*)&number;
    return (numPtr[0] != 1);
}

JNIEXPORT void JNICALL Java_AudioPlayer_release
(JNIEnv *env, jobject thisObject) {


	if (loadedMovie) {
		pAudioBuffer->stop();

		quit = 1;
		doneDecoding = 0;

		decodeAudio->join();
		delete decodeAudio;

		// If the audio queue has been initialized the free it up
		delete pAudioBuffer;
		//packet_queue_destroy(&audioq);

		// Close the codec
		avcodec_close(pAudioInCodecCtx);
		avcodec_close(pAudioOutCodecCtx);		
		// Cleanup conversion context
		swr_free(&pResampleCtx);
		// Close the video file
		avformat_close_input(&pFormatCtx);
		free(pAudioBufferData);

		pAudioInCodecCtx = nullptr;
		pAudioOutCodecCtx = nullptr;
		pResampleCtx = nullptr;
		pFormatCtx = nullptr;
		pAudioBufferData = nullptr;
		decodeAudio = nullptr;
	}
	loadedMovie = false;
}