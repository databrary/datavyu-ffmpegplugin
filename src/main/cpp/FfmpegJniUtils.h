#ifndef _FFMPEG_JNI_UTILS_H_
#define _FFMPEG_JNI_UTILS_H_

#include <jni.h>
#include "AudioVideoFormats.h"

// This mapping is taken from java.awt.color.ColorSpace.java
#define TYPE_RGB 5

uint32_t SetJAudioFormat(JNIEnv *env, jobject jAudioFormat, const AudioFormat& audioFormat);

uint32_t SetJPixelFormat(JNIEnv *env, jobject jPixelFormat, const PixelFormat& pixelFormat);

uint32_t GetAudioFormat(JNIEnv *env, jobject jAudioFormat, AudioFormat* audioFormat);

uint32_t GetPixelFormat(JNIEnv *env, jobject jPixelFormat, PixelFormat* pixelFormat);

#endif  //_FFMPEG_JNI_UTILS_H_
