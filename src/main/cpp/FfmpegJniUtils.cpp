#include "FfmpegJniUtils.h"
#include "FfmpegMediaErrors.h"

uint32_t SetJAudioFormat(JNIEnv *env, jobject jAudioFormat, const AudioFormat& audioFormat) {

	// Get the audio format class
	jclass audioFormatClass = env->GetObjectClass(jAudioFormat);
	if (audioFormatClass == nullptr) {
		return ERROR_FFMPEG_AUDIO_FORMAT_NULL;
	}

	// Set the audio encoding (nested class)
	jfieldID encodingId = env->GetFieldID(audioFormatClass, "encoding", "Ljavax/sound/sampled/AudioFormat$Encoding;");
	if (encodingId == nullptr) {
		return ERROR_FFMPEG_AUDIO_FORMAT_ENCODING_ID_NULL;
	}
	jobject jEncoding = env->GetObjectField(jAudioFormat, encodingId);
	if (jEncoding == nullptr) {
		return ERROR_FFMPEG_AUDIO_FORMAT_ENCODING_NULL;
	}
	jclass encodingClass = env->GetObjectClass(jEncoding);
	if (encodingClass == nullptr) {
		return ERROR_FFMPEG_AUDIO_FORMAT_ENCODING_CLASS_NULL;
	}
	jfieldID encodingNameId = env->GetFieldID(encodingClass, "name", "Ljava/lang/String;");
	if (encodingNameId == nullptr) {
		return ERROR_FFMPEG_AUDIO_FORMAT_ENCODING_NAME_ID_NULL;
	}
	env->SetObjectField(jEncoding, encodingNameId, env->NewStringUTF(audioFormat.encoding.c_str()));

	// Set endianess
	jfieldID bigEndianId = env->GetFieldID(audioFormatClass, "bigEndian", "Z");
	if (bigEndianId == nullptr) {
		return ERROR_FFMPEG_AUDIO_FORMAT_ENDIAN_ID_NULL;
	}
	env->SetBooleanField(jAudioFormat, bigEndianId, (jboolean)audioFormat.bigEndian);

	// Set sample rate
	jfieldID sampleRateId = env->GetFieldID(audioFormatClass, "sampleRate", "F");
	if (sampleRateId == nullptr) {
		return ERROR_FFMPEG_AUDIO_FORMAT_SAMPLE_RATE_ID_NULL;
	}
	env->SetFloatField(jAudioFormat, sampleRateId, (jfloat)audioFormat.sampleRate);

	// Set sample size in bits
	jfieldID sampleSizeInBitsId = env->GetFieldID(audioFormatClass, "sampleSizeInBits", "I");
	if (sampleSizeInBitsId == nullptr) {
		return ERROR_FFMPEG_AUDIO_FORMAT_SAMPLE_SIZE_IN_BITS_ID_NULL;
	}
	env->SetIntField(jAudioFormat, sampleSizeInBitsId, (jint)(audioFormat.sampleSizeInBits));

	// Set the number of channels
	jfieldID channelsId = env->GetFieldID(audioFormatClass, "channels", "I");
	if (channelsId == nullptr) {
		return ERROR_FFMPEG_AUDIO_FORMAT_CHANNELS_ID_NULL;
	}
	env->SetIntField(jAudioFormat, channelsId, (jint)audioFormat.channels);

	// Set the frame size in bytes
	jfieldID frameSizeId = env->GetFieldID(audioFormatClass, "frameSize", "I");
	if (frameSizeId == nullptr) {
		return ERROR_FFMPEG_AUDIO_FORMAT_FRAME_SIZE_ID_NULL;
	}
	env->SetIntField(jAudioFormat, frameSizeId, (jint)audioFormat.frameSize);

	// Set the frame rate in Hertz
	jfieldID frameRateId = env->GetFieldID(audioFormatClass, "frameRate", "F");
	if (frameRateId == nullptr) {
		return ERROR_FFMPEG_AUDIO_FORMAT_FRAME_RATE_ID_NULL;
	}
	env->SetFloatField(jAudioFormat, frameRateId, (jfloat)audioFormat.frameRate);

	return ERROR_NONE;
}


uint32_t SetJPixelFormat(JNIEnv *env, jobject jColorSpace, const PixelFormat& pixelFormat) {

	// Get the audio format class
	jclass colorSpaceClass = env->GetObjectClass(jColorSpace);
	if (colorSpaceClass == nullptr) {
		return ERROR_FFMPEG_COLOR_SPACE_NULL;
	}

	// Set int type
	jfieldID typeId = env->GetFieldID(colorSpaceClass, "type", "I");
	if (typeId == nullptr) {
		return ERROR_FFMPEG_COLOR_SPACE_TYPE_NULL;
	}
	int type = 65535; // Unmapped id to be used as unknown here

	// TODO(fraudies): Add support for more pixel formats
	switch (pixelFormat.type) {
	case AV_PIX_FMT_RGB24:
		type = TYPE_RGB;
		break;
	}
	env->SetIntField(jColorSpace, typeId, type);

	// Set number of color channels
	jfieldID numChannelId = env->GetFieldID(colorSpaceClass, "numComponents", "I");
	if (typeId == nullptr) {
		return ERROR_FFMPEG_COLOR_SPACE_NUM_COMPONENT_NULL;
	}
	env->SetIntField(jColorSpace, numChannelId, pixelFormat.numComponents);

	return ERROR_NONE;
}

// TODO: Might want to load field id's at init and cache thereafter (because of performance bottleneck in JNI)
uint32_t GetAudioFormat(JNIEnv *env, jobject jAudioFormat, AudioFormat* audioFormat) {
	// Get the audio format class
	jclass audioFormatClass = env->GetObjectClass(jAudioFormat);
	if (audioFormatClass == nullptr) {
		return ERROR_FFMPEG_AUDIO_FORMAT_NULL;
	}

	// Get the audio encoding (nested class)
	jfieldID encodingId = env->GetFieldID(audioFormatClass, "encoding", "Ljavax/sound/sampled/AudioFormat$Encoding;");
	if (encodingId == nullptr) {
		return ERROR_FFMPEG_AUDIO_FORMAT_ENCODING_ID_NULL;
	}
	jobject jEncoding = env->GetObjectField(jAudioFormat, encodingId);
	if (jEncoding == nullptr) {
		return ERROR_FFMPEG_AUDIO_FORMAT_ENCODING_NULL;
	}
	jclass encodingClass = env->GetObjectClass(jEncoding);
	if (encodingClass == nullptr) {
		return ERROR_FFMPEG_AUDIO_FORMAT_ENCODING_CLASS_NULL;
	}
	jfieldID encodingNameId = env->GetFieldID(encodingClass, "name", "Ljava/lang/String;");
	if (encodingNameId == nullptr) {
		return ERROR_FFMPEG_AUDIO_FORMAT_ENCODING_NAME_ID_NULL;
	}
	jstring jEncodingName = (jstring) env->GetObjectField(jEncoding, encodingNameId);
	int len = env->GetStringUTFLength(jEncodingName);
	const char* encodingName = env->GetStringUTFChars(jEncodingName, 0);
	audioFormat->encoding = std::string(encodingName, env->GetStringUTFLength(jEncodingName));
	env->ReleaseStringUTFChars(jEncodingName, encodingName);

	// Set endianess
	jfieldID bigEndianId = env->GetFieldID(audioFormatClass, "bigEndian", "Z");
	if (bigEndianId == nullptr) {
		return ERROR_FFMPEG_AUDIO_FORMAT_ENDIAN_ID_NULL;
	}
	audioFormat->bigEndian = env->GetBooleanField(jAudioFormat, bigEndianId);

	// Set sample rate
	jfieldID sampleRateId = env->GetFieldID(audioFormatClass, "sampleRate", "F");
	if (sampleRateId == nullptr) {
		return ERROR_FFMPEG_AUDIO_FORMAT_SAMPLE_RATE_ID_NULL;
	}
	audioFormat->sampleRate = env->GetFloatField(jAudioFormat, sampleRateId);

	// Set sample size in bits
	jfieldID sampleSizeInBitsId = env->GetFieldID(audioFormatClass, "sampleSizeInBits", "I");
	if (sampleSizeInBitsId == nullptr) {
		return ERROR_FFMPEG_AUDIO_FORMAT_SAMPLE_SIZE_IN_BITS_ID_NULL;
	}
	audioFormat->sampleSizeInBits = env->GetIntField(jAudioFormat, sampleSizeInBitsId);

	// Set the number of channels
	jfieldID channelsId = env->GetFieldID(audioFormatClass, "channels", "I");
	if (channelsId == nullptr) {
		return ERROR_FFMPEG_AUDIO_FORMAT_CHANNELS_ID_NULL;
	}
	audioFormat->channels = env->GetIntField(jAudioFormat, channelsId);

	// Set the frame size in bytes
	jfieldID frameSizeId = env->GetFieldID(audioFormatClass, "frameSize", "I");
	if (frameSizeId == nullptr) {
		return ERROR_FFMPEG_AUDIO_FORMAT_FRAME_SIZE_ID_NULL;
	}
	audioFormat->frameSize = env->GetIntField(jAudioFormat, frameSizeId);

	// Set the frame rate in Hertz
	jfieldID frameRateId = env->GetFieldID(audioFormatClass, "frameRate", "F");
	if (frameRateId == nullptr) {
		return ERROR_FFMPEG_AUDIO_FORMAT_FRAME_RATE_ID_NULL;
	}
	audioFormat->frameRate = env->GetFloatField(jAudioFormat, frameRateId);

	return ERROR_NONE;
}

uint32_t GetPixelFormat(JNIEnv *env, jobject jColorSpace, PixelFormat* pixelFormat) {

	// Get the audio format class
	jclass colorSpaceClass = env->GetObjectClass(jColorSpace);
	if (colorSpaceClass == nullptr) {
		return ERROR_FFMPEG_COLOR_SPACE_NULL;
	}

	// Set int type
	jfieldID typeId = env->GetFieldID(colorSpaceClass, "type", "I");
	if (typeId == nullptr) {
		return ERROR_FFMPEG_COLOR_SPACE_TYPE_NULL;
	}
	int type = env->GetIntField(jColorSpace, typeId);
	
	// TODO(fraudies): Add support for more pixel formats
	pixelFormat->type = AV_PIX_FMT_NONE;
	switch (type) {
	case TYPE_RGB:
		pixelFormat->type = AV_PIX_FMT_RGB24; // CS_sRGB = 1000
		break;
	}

	// Set number of color channels
	jfieldID numChannelId = env->GetFieldID(colorSpaceClass, "numComponents", "I");
	if (typeId == nullptr) {
		return ERROR_FFMPEG_COLOR_SPACE_NUM_COMPONENT_NULL;
	}
	pixelFormat->numComponents = env->GetIntField(jColorSpace, numChannelId);

	return ERROR_NONE;
}