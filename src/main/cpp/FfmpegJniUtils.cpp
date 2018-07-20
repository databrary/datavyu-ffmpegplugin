#include "FfmpegJniUtils.h"
#include "FfmpegMediaErrors.h"

uint32_t SetAudioFormat(JNIEnv *env, jobject jAudioFormat, const AudioFormat& audioFormat) {

	// Get the audio format class
	jclass audioFormatClass = env->GetObjectClass(jAudioFormat);
	if (audioFormatClass == nullptr) {
		return ERROR_AUDIO_FORMAT_NULL;
	}

	// Set the audio encoding (nested class)
	jfieldID encodingId = env->GetFieldID(audioFormatClass, "encoding", "Ljavax/sound/sampled/AudioFormat$Encoding;");
	if (encodingId == nullptr) {
		return ERROR_AUDIO_FORMAT_ENCODING_ID_NULL;
	}
	jobject encoding = env->GetObjectField(jAudioFormat, encodingId);
	if (encoding == nullptr) {
		return ERROR_AUDIO_FORMAT_ENCODING_NULL;
	}
	jclass encodingClass = env->GetObjectClass(encoding);
	if (encodingClass == nullptr) {
		return ERROR_AUDIO_FORMAT_ENCODING_CLASS_NULL;
	}
	jfieldID encodingNameId = env->GetFieldID(encodingClass, "name", "Ljava/lang/String;");
	if (encodingNameId == nullptr) {
		return ERROR_AUDIO_FORMAT_ENCODING_NAME_ID_NULL;
	}
	env->SetObjectField(jAudioFormat, encodingNameId, env->NewStringUTF(audioFormat.encoding.c_str()));

	// Set endianess
	jfieldID bigEndianId = env->GetFieldID(audioFormatClass, "bigEndian", "Z");
	if (bigEndianId == nullptr) {
		return ERROR_AUDIO_FORMAT_ENDIAN_ID_NULL;
	}
	env->SetBooleanField(jAudioFormat, bigEndianId, (jboolean)audioFormat.bigEndian);

	// Set sample rate
	jfieldID sampleRateId = env->GetFieldID(audioFormatClass, "sampleRate", "F");
	if (sampleRateId == nullptr) {
		return ERROR_AUDIO_FORMAT_SAMPLE_RATE_ID_NULL;
	}
	env->SetFloatField(jAudioFormat, sampleRateId, (jfloat)audioFormat.sampleRate);

	// Set sample size in bits
	jfieldID sampleSizeInBitsId = env->GetFieldID(audioFormatClass, "sampleSizeInBits", "I");
	if (sampleSizeInBitsId == nullptr) {
		return ERROR_AUDIO_FORMAT_SAMPLE_SIZE_IN_BITS_ID_NULL;
	}
	env->SetIntField(jAudioFormat, sampleSizeInBitsId, (jint)(audioFormat.sampleSizeInBits));

	// Set the number of channels
	jfieldID channelsId = env->GetFieldID(audioFormatClass, "channels", "I");
	if (channelsId == nullptr) {
		return ERROR_AUDIO_FORMAT_CHANNELS_ID_NULL;
	}
	env->SetIntField(jAudioFormat, channelsId, (jint)audioFormat.channels);

	// Set the frame size in bytes
	jfieldID frameSizeId = env->GetFieldID(audioFormatClass, "frameSize", "I");
	if (frameSizeId == nullptr) {
		return ERROR_AUDIO_FORMAT_FRAME_SIZE_ID_NULL;
	}
	env->SetIntField(jAudioFormat, frameSizeId, (jint)audioFormat.frameSize);

	// Set the frame rate in Hertz
	jfieldID frameRateId = env->GetFieldID(audioFormatClass, "frameRate", "F");
	if (frameRateId == nullptr) {
		return ERROR_AUDIO_FORMAT_FRAME_RATE_ID_NULL;
	}
	env->SetFloatField(jAudioFormat, frameRateId, (jfloat)audioFormat.frameRate);

	return ERROR_NONE;
}


uint32_t SetPixelFormat(JNIEnv *env, jobject jColorSpace, const PixelFormat& pixelFormat) {
	// TODO(fraudies): Implement the mapping from the pixel format into the color space
	// Given that the color space is an abstract class it might be better to return a string/id identifier
	return ERROR_NONE;
}