#ifndef _FFMPEG_JNI_UTILS_H_
#define _FFMPEG_JNI_UTILS_H_

#include <jni.h>
#include "AudioVideoFormats.h"

uint32_t SetAudioFormat(JNIEnv *env, jobject jAudioFormat, const AudioFormat& audioFormat);

uint32_t SetPixelFormat(JNIEnv *env, jobject jPixelFormat, const PixelFormat& pixelFormat);

#endif  //_FFMPEG_JNI_UTILS_H_
