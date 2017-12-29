#include <jni.h>

#ifndef AUDIO_FORMAT_HELPER_H_
#define AUDIO_FORMAT_HELPER_H_

/*******************************************************************************
 * Structure that holds the parameters for the audio format.
 ******************************************************************************/
struct AudioFormat {

	/** The name of the encoding */
	std::string encodingName;

	/** Bytes are encoded in big endian */
	bool bigEndian;

	/** The sample rate of the audio signal */
	float sampleRate;

	/** The number of bits per sample. Often 8 bit/sample or 16 bit/sample */
	int sampleSizeInBits;

	/** The number of channels */
	int channels;

	/** The size of an audio frame */
	float frameSize;

	/** The audio frame rate for the replay of the audio signal */
	float frameRate;
};

/**
 * Uses the audio format struct to set the corresponding values for this audio
 * format in the java object.
 */
int setAudioFormat(JNIEnv *env, jobject jAudioFormat, const AudioFormat& audioFormat, Logger *pLogger) {

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
	encodingId = env->GetFieldID(audioFormatClass, "encoding", "Ljavax/sound/sampled/AudioFormat$Encoding;");
	if (encodingId == nullptr) {
		pLogger->error("Could not find 'encoding' attribute in 'AudioFormat'.");
		return (jint) AVERROR_INVALIDDATA;
	}

	encoding = env->GetObjectField(jAudioFormat, encodingId);
	if (encoding == nullptr) {
		pLogger->error("Could not find value for 'encoding' attribute in 'AudioEncoding'.");
		return (jint) AVERROR_INVALIDDATA;
	}
	encodingClass = env->GetObjectClass(encoding);

	// Set the encoding name
	encodingNameId = env->GetFieldID(encodingClass, "name", "Ljava/lang/String;");
	if (encodingNameId == nullptr) {
		pLogger->error("Could not find 'name' in 'AudioFormat$Encoding'.");
		return (jint) AVERROR_INVALIDDATA;
	}
	jEncodingName = env->NewStringUTF(audioFormat.encodingName.c_str());
	env->SetObjectField(jAudioFormat, encodingNameId, jEncodingName);

	// Set endianess
	bigEndianId = env->GetFieldID(audioFormatClass, "bigEndian", "Z");
	if (bigEndianId == nullptr) {
		pLogger->error("Could not find attribute 'bigEndian' in 'AudioEncoding'.");
		return (jint) AVERROR_INVALIDDATA;
	}
	env->SetBooleanField(jAudioFormat, bigEndianId, (jboolean) audioFormat.bigEndian);

	// Set sample rate
	sampleRateId = env->GetFieldID(audioFormatClass, "sampleRate", "F");
	if (sampleRateId == nullptr) {
		pLogger->error("Could not find attribute 'sampleRate' in 'AudioEncoding'.");
		return (jint) AVERROR_INVALIDDATA;
	}
	env->SetFloatField(jAudioFormat, sampleRateId, (jfloat) audioFormat.sampleRate);

	// Set sample size in bits
	sampleSizeInBitsId = env->GetFieldID(audioFormatClass, "sampleSizeInBits", "I");
	if (sampleSizeInBitsId == nullptr) {
		pLogger->error("Could not find attribute 'sampleSizeInBits' in 'AudioEncoding'.");
		return (jint) AVERROR_INVALIDDATA;
	}
	env->SetIntField(jAudioFormat, sampleSizeInBitsId, (jint) audioFormat.sampleSizeInBits);

	// Set the number of channels
	channelsId = env->GetFieldID(audioFormatClass, "channels", "I");
	if (channelsId == nullptr) {
		pLogger->error("Could not find attribute 'channelsId' in 'AudioEncoding'.");
		return (jint) AVERROR_INVALIDDATA;
	}
	env->SetIntField(jAudioFormat, channelsId, (jint) audioFormat.channels);

	// Set the frame size in bytes
	frameSizeId = env->GetFieldID(audioFormatClass, "frameSize", "I");
	if (frameSizeId == nullptr) {
		pLogger->error("Could not find attribute 'frameSize' in 'AudioEncoding'.");
		return (jint) AVERROR_INVALIDDATA;
	}
	env->SetIntField(jAudioFormat, frameSizeId, (jint) audioFormat.frameSize);

	// Set the frame rate in Hertz
	frameRateId = env->GetFieldID(audioFormatClass, "frameRate", "F");
	if (frameRateId == nullptr) {
		pLogger->error("Could not find attribute 'frameRate' in 'AudioEncoding'.");
		return (jint) AVERROR_INVALIDDATA;
	}
	env->SetFloatField(jAudioFormat, frameRateId, (jfloat) audioFormat.frameRate);

	return 0; // Return no error
}

/**
 * Get the audio format struct from the java object audio format.
 */
int getAudioFormat(JNIEnv *env, AudioFormat* audioFormat, const jobject& jAudioFormat, Logger *pLogger) {

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
	encodingId = env->GetFieldID(audioFormatClass, "encoding", "Ljavax/sound/sampled/AudioFormat$Encoding;");

	if (encodingId == nullptr) {
		pLogger->error("Could not find 'encoding' attribute in 'AudioFormat'.");
		return (jint) AVERROR_INVALIDDATA;
	}
	encoding = env->GetObjectField(jAudioFormat, encodingId);
	if (encoding == nullptr) {
		pLogger->error("Could not find value for 'encoding' attribute in 'AudioEncoding'.");
		return (jint) AVERROR_INVALIDDATA;
	}
	encodingClass = env->GetObjectClass(encoding);

	// Get the encoding name
	encodingNameId = env->GetFieldID(encodingClass, "name", "Ljava/lang/String;");
	if (encodingNameId == nullptr) {
		pLogger->error("Could not find 'name' in 'AudioFormat$Encoding'.");
		return (jint) AVERROR_INVALIDDATA;
	}
	jEncodingName = (jstring) env->GetObjectField(encoding, encodingNameId);
	encodingName = env->GetStringUTFChars(jEncodingName, 0);
	audioFormat->encodingName = std::string(encodingName, env->GetStringLength(jEncodingName));
	env->ReleaseStringUTFChars(jEncodingName, encodingName);

	// Get endianess
	bigEndianId = env->GetFieldID(audioFormatClass, "bigEndian", "Z");
	if (bigEndianId == nullptr) {
		pLogger->error("Could not find attribute 'bigEndian' in 'AudioEncoding'.");
		return (jint) AVERROR_INVALIDDATA;
	}
	audioFormat->bigEndian = (bool) env->GetBooleanField(jAudioFormat, bigEndianId);

	// Get sample rate
	sampleRateId = env->GetFieldID(audioFormatClass, "sampleRate", "F");
	if (sampleRateId == nullptr) {
		pLogger->error("Could not find attribute 'sampleRate' in 'AudioEncoding'.");
		return (jint) AVERROR_INVALIDDATA;
	}
	audioFormat->sampleRate = (float) env->GetFloatField(jAudioFormat, sampleRateId);

	// Get sample size in bits
	sampleSizeInBitsId = env->GetFieldID(audioFormatClass, "sampleSizeInBits", "I");
	if (sampleSizeInBitsId == nullptr) {
		pLogger->error("Could not find attribute 'sampleSizeInBits' in 'AudioEncoding'.");
		return (jint) AVERROR_INVALIDDATA;
	}
	audioFormat->sampleSizeInBits = (int) env->GetIntField(jAudioFormat, sampleSizeInBitsId);

	// Get the number of channels
	channelsId = env->GetFieldID(audioFormatClass, "channels", "I");
	if (channelsId == nullptr) {
		pLogger->error("Could not find attribute 'channelsId' in 'AudioEncoding'.");
		return (jint) AVERROR_INVALIDDATA;
	}
	audioFormat->channels = (int) env->GetIntField(jAudioFormat, channelsId);

	// Get the frame size in Bytes
	frameSizeId = env->GetFieldID(audioFormatClass, "frameSize", "I");
	if (frameSizeId == nullptr) {
		pLogger->error("Could not find attribute 'frameSize' in 'AudioEncoding'.");
		return (jint) AVERROR_INVALIDDATA;
	}
	audioFormat->frameSize = (int) env->GetIntField(jAudioFormat, frameSizeId);

	// Get the frame rate in Hertz
	frameRateId = env->GetFieldID(audioFormatClass, "frameRate", "F");
	if (frameRateId == nullptr) {
		pLogger->error("Could not find attribute 'frameRate' in 'AudioEncoding'.");
		return (jint) AVERROR_INVALIDDATA;
	}
	audioFormat->frameRate = env->GetFloatField(jAudioFormat, frameRateId);
	return 0; // No error
}

std::string getErrorText(const int error) {
    char error_buffer[256];
    av_strerror(error, error_buffer, sizeof(error_buffer));
    return std::string(error_buffer, sizeof(error_buffer));
}

#endif AUDIO_FORMAT_HELPER_H_
