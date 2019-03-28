#ifndef _FFMPEG_JNI_UTILS_H_
#define _FFMPEG_JNI_UTILS_H_

#include "AudioVideoFormats.h"
#include <jni.h>

// This mapping is taken from java.awt.color.ColorSpace.java
#define TYPE_RGB 5

uint32_t SetJAudioFormat(JNIEnv *env, jobject j_audio_format,
                         const AudioFormat &audio_format);

uint32_t SetJPixelFormat(JNIEnv *env, jobject j_pixel_format,
                         const PixelFormat &pixel_format);

uint32_t GetAudioFormat(JNIEnv *env, jobject j_audio_format,
                        AudioFormat *p_audio_format);

uint32_t GetPixelFormat(JNIEnv *env, jobject j_pixel_format,
                        PixelFormat *p_pixel_format);

#endif //_FFMPEG_JNI_UTILS_H_
