#include "FfmpegJniUtils.h"
#include "MediaPlayerErrors.h"

uint32_t SetJAudioFormat(JNIEnv *env, jobject j_audio_format,
                         const AudioFormat &audio_format) {

  // Get the audio format class
  jclass audio_format_class = env->GetObjectClass(j_audio_format);
  if (audio_format_class == nullptr) {
    return ERROR_FFMPEG_AUDIO_FORMAT_NULL;
  }

  // Set the audio encoding (nested class)
  jfieldID encoding_id =
      env->GetFieldID(audio_format_class, "encoding",
                      "Ljavax/sound/sampled/AudioFormat$Encoding;");
  if (encoding_id == nullptr) {
    return ERROR_FFMPEG_AUDIO_FORMAT_ENCODING_ID_NULL;
  }
  jobject j_encoding = env->GetObjectField(j_audio_format, encoding_id);
  if (j_encoding == nullptr) {
    return ERROR_FFMPEG_AUDIO_FORMAT_ENCODING_NULL;
  }
  jclass encoding_class = env->GetObjectClass(j_encoding);
  if (encoding_class == nullptr) {
    return ERROR_FFMPEG_AUDIO_FORMAT_ENCODING_CLASS_NULL;
  }
  jfieldID encoding_name_id =
      env->GetFieldID(encoding_class, "name", "Ljava/lang/String;");
  if (encoding_name_id == nullptr) {
    return ERROR_FFMPEG_AUDIO_FORMAT_ENCODING_NAME_ID_NULL;
  }
  env->SetObjectField(j_encoding, encoding_name_id,
                      env->NewStringUTF(audio_format.encoding_name_.c_str()));

  // Set endianess
  jfieldID big_endian_id = env->GetFieldID(audio_format_class, "bigEndian", "Z");
  if (big_endian_id == nullptr) {
    return ERROR_FFMPEG_AUDIO_FORMAT_ENDIAN_ID_NULL;
  }
  env->SetBooleanField(j_audio_format, big_endian_id,
                       (jboolean)audio_format.is_big_endian_);

  // Set sample rate
  jfieldID sample_rate_id =
      env->GetFieldID(audio_format_class, "sampleRate", "F");
  if (sample_rate_id == nullptr) {
    return ERROR_FFMPEG_AUDIO_FORMAT_SAMPLE_RATE_ID_NULL;
  }
  env->SetFloatField(j_audio_format, sample_rate_id,
                     (jfloat)audio_format.sample_rate_);

  // Set sample size in bits
  jfieldID sample_size_in_bits_id =
      env->GetFieldID(audio_format_class, "sampleSizeInBits", "I");
  if (sample_size_in_bits_id == nullptr) {
    return ERROR_FFMPEG_AUDIO_FORMAT_SAMPLE_SIZE_IN_BITS_ID_NULL;
  }
  env->SetIntField(j_audio_format, sample_size_in_bits_id,
                   (jint)(audio_format.sample_size_in_bits_));

  // Set the number of channels
  jfieldID channels_id = env->GetFieldID(audio_format_class, "channels", "I");
  if (channels_id == nullptr) {
    return ERROR_FFMPEG_AUDIO_FORMAT_CHANNELS_ID_NULL;
  }
  env->SetIntField(j_audio_format, channels_id,
                   (jint)audio_format.num_channels_);

  // Set the frame size in bytes
  jfieldID frame_size_id = env->GetFieldID(audio_format_class, "frameSize", "I");
  if (frame_size_id == nullptr) {
    return ERROR_FFMPEG_AUDIO_FORMAT_FRAME_SIZE_ID_NULL;
  }
  env->SetIntField(j_audio_format, frame_size_id, (jint)audio_format.frame_size_);

  // Set the frame rate in Hertz
  jfieldID frame_rate_id = env->GetFieldID(audio_format_class, "frameRate", "F");
  if (frame_rate_id == nullptr) {
    return ERROR_FFMPEG_AUDIO_FORMAT_FRAME_RATE_ID_NULL;
  }
  env->SetFloatField(j_audio_format, frame_rate_id,
                     (jfloat)audio_format.frame_rate_);

  return ERROR_NONE;
}

uint32_t SetJPixelFormat(JNIEnv *env, jobject j_color_space,
                         const PixelFormat &pixelFormat) {

  // Get the audio format class
  jclass color_space_class = env->GetObjectClass(j_color_space);
  if (color_space_class == nullptr) {
    return ERROR_FFMPEG_COLOR_SPACE_NULL;
  }

  // Set int type
  jfieldID type_id = env->GetFieldID(color_space_class, "type", "I");
  if (type_id == nullptr) {
    return ERROR_FFMPEG_COLOR_SPACE_TYPE_NULL;
  }
  int type = 65535; // Unmapped id to be used as unknown here

  // TODO(fraudies): Add support for more pixel formats
  switch (pixelFormat.pixel_format_) {
  case AV_PIX_FMT_RGB24:
    type = TYPE_RGB;
    break;
  }
  env->SetIntField(j_color_space, type_id, type);

  // Set number of color channels
  jfieldID num_channel_id =
      env->GetFieldID(color_space_class, "numComponents", "I");
  if (type_id == nullptr) {
    return ERROR_FFMPEG_COLOR_SPACE_NUM_COMPONENT_NULL;
  }
  env->SetIntField(j_color_space, num_channel_id, pixelFormat.num_components_);

  return ERROR_NONE;
}

// TODO: Might want to load field id's at init and cache thereafter (because of
// performance bottleneck in JNI)
uint32_t GetAudioFormat(JNIEnv *env, jobject j_audio_format,
                        AudioFormat *p_audio_format) {
  // Get the audio format class
  jclass audio_format_class = env->GetObjectClass(j_audio_format);
  if (audio_format_class == nullptr) {
    return ERROR_FFMPEG_AUDIO_FORMAT_NULL;
  }

  // Get the audio encoding (nested class)
  jfieldID encoding_id =
      env->GetFieldID(audio_format_class, "encoding",
                      "Ljavax/sound/sampled/AudioFormat$Encoding;");
  if (encoding_id == nullptr) {
    return ERROR_FFMPEG_AUDIO_FORMAT_ENCODING_ID_NULL;
  }
  jobject j_encoding = env->GetObjectField(j_audio_format, encoding_id);
  if (j_encoding == nullptr) {
    return ERROR_FFMPEG_AUDIO_FORMAT_ENCODING_NULL;
  }
  jclass encoding_class = env->GetObjectClass(j_encoding);
  if (encoding_class == nullptr) {
    return ERROR_FFMPEG_AUDIO_FORMAT_ENCODING_CLASS_NULL;
  }
  jfieldID encoding_name_id =
      env->GetFieldID(encoding_class, "name", "Ljava/lang/String;");
  if (encoding_name_id == nullptr) {
    return ERROR_FFMPEG_AUDIO_FORMAT_ENCODING_NAME_ID_NULL;
  }
  jstring j_encoding_name =
      (jstring)env->GetObjectField(j_encoding, encoding_name_id);
  int len = env->GetStringUTFLength(j_encoding_name);
  const char *encodingName = env->GetStringUTFChars(j_encoding_name, 0);
  p_audio_format->encoding_name_ =
      std::string(encodingName, env->GetStringUTFLength(j_encoding_name));
  env->ReleaseStringUTFChars(j_encoding_name, encodingName);

  // Set endianess
  jfieldID big_endian_id = env->GetFieldID(audio_format_class, "bigEndian", "Z");
  if (big_endian_id == nullptr) {
    return ERROR_FFMPEG_AUDIO_FORMAT_ENDIAN_ID_NULL;
  }
  p_audio_format->is_big_endian_ = env->GetBooleanField(j_audio_format, big_endian_id);

  // Set sample rate
  jfieldID sample_rate_id = env->GetFieldID(audio_format_class, "sampleRate", "F");
  if (sample_rate_id == nullptr) {
    return ERROR_FFMPEG_AUDIO_FORMAT_SAMPLE_RATE_ID_NULL;
  }
  p_audio_format->sample_rate_ = env->GetFloatField(j_audio_format, sample_rate_id);

  // Set sample size in bits
  jfieldID sample_size_in_bits_id =
      env->GetFieldID(audio_format_class, "sampleSizeInBits", "I");
  if (sample_size_in_bits_id == nullptr) {
    return ERROR_FFMPEG_AUDIO_FORMAT_SAMPLE_SIZE_IN_BITS_ID_NULL;
  }
  p_audio_format->sample_size_in_bits_ =
      env->GetIntField(j_audio_format, sample_size_in_bits_id);

  // Set the number of channels
  jfieldID channels_id = env->GetFieldID(audio_format_class, "channels", "I");
  if (channels_id == nullptr) {
    return ERROR_FFMPEG_AUDIO_FORMAT_CHANNELS_ID_NULL;
  }
  p_audio_format->num_channels_ = env->GetIntField(j_audio_format, channels_id);

  // Set the frame size in bytes
  jfieldID frame_size_id = env->GetFieldID(audio_format_class, "frameSize", "I");
  if (frame_size_id == nullptr) {
    return ERROR_FFMPEG_AUDIO_FORMAT_FRAME_SIZE_ID_NULL;
  }
  p_audio_format->frame_size_ = env->GetIntField(j_audio_format, frame_size_id);

  // Set the frame rate in Hertz
  jfieldID frame_rate_id = env->GetFieldID(audio_format_class, "frameRate", "F");
  if (frame_rate_id == nullptr) {
    return ERROR_FFMPEG_AUDIO_FORMAT_FRAME_RATE_ID_NULL;
  }
  p_audio_format->frame_rate_ = env->GetFloatField(j_audio_format, frame_rate_id);

  return ERROR_NONE;
}

uint32_t GetPixelFormat(JNIEnv *env, jobject j_color_space,
                        PixelFormat *p_pixel_format) {

  // Get the audio format class
  jclass color_space_class = env->GetObjectClass(j_color_space);
  if (color_space_class == nullptr) {
    return ERROR_FFMPEG_COLOR_SPACE_NULL;
  }

  // Set int type
  jfieldID type_id = env->GetFieldID(color_space_class, "type", "I");
  if (type_id == nullptr) {
    return ERROR_FFMPEG_COLOR_SPACE_TYPE_NULL;
  }
  int type = env->GetIntField(j_color_space, type_id);

  // TODO(fraudies): Add support for more pixel formats
  p_pixel_format->pixel_format_ = AV_PIX_FMT_NONE;
  switch (type) {
  case TYPE_RGB:
    p_pixel_format->pixel_format_ = AV_PIX_FMT_RGB24; // CS_sRGB = 1000
    break;
  }

  // Set number of color channels
  jfieldID num_channel_id =
      env->GetFieldID(color_space_class, "numComponents", "I");
  if (type_id == nullptr) {
    return ERROR_FFMPEG_COLOR_SPACE_NUM_COMPONENT_NULL;
  }
  p_pixel_format->num_components_ = env->GetIntField(j_color_space, num_channel_id);

  return ERROR_NONE;
}
