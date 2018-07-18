/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer */

#ifndef _Included_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
#define _Included_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
#ifdef __cplusplus
extern "C" {
#endif
#undef org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_eventPlayerUnknown
#define org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_eventPlayerUnknown 100L
#undef org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_eventPlayerReady
#define org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_eventPlayerReady 101L
#undef org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_eventPlayerPlaying
#define org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_eventPlayerPlaying 102L
#undef org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_eventPlayerPaused
#define org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_eventPlayerPaused 103L
#undef org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_eventPlayerStopped
#define org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_eventPlayerStopped 104L
#undef org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_eventPlayerStalled
#define org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_eventPlayerStalled 105L
#undef org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_eventPlayerFinished
#define org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_eventPlayerFinished 106L
#undef org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_eventPlayerError
#define org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_eventPlayerError 107L
/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegInitPlayer
 * Signature: ([JLjava/net/URI;Ljavax/sound/sampled/AudioFormat;Ljava/awt/color/ColorSpace;)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegInitPlayer
  (JNIEnv *, jobject, jlongArray, jobject, jobject, jobject);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegDisposePlayer
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegDisposePlayer
  (JNIEnv *, jobject, jlong);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegGetAudioSyncDelay
 * Signature: (J[J)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegGetAudioSyncDelay
  (JNIEnv *, jobject, jlong, jlongArray);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegSetAudioSyncDelay
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegSetAudioSyncDelay
  (JNIEnv *, jobject, jlong, jlong);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegPlay
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegPlay
  (JNIEnv *, jobject, jlong);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegPause
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegPause
  (JNIEnv *, jobject, jlong);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegStop
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegStop
  (JNIEnv *, jobject, jlong);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegFinish
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegFinish
  (JNIEnv *, jobject, jlong);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegGetRate
 * Signature: (J[F)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegGetRate
  (JNIEnv *, jobject, jlong, jfloatArray);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegSetRate
 * Signature: (JF)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegSetRate
  (JNIEnv *, jobject, jlong, jfloat);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegGetPresentationTime
 * Signature: (J[D)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegGetPresentationTime
  (JNIEnv *, jobject, jlong, jdoubleArray);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegGetVolume
 * Signature: (J[F)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegGetVolume
  (JNIEnv *, jobject, jlong, jfloatArray);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegSetVolume
 * Signature: (JF)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegSetVolume
  (JNIEnv *, jobject, jlong, jfloat);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegGetBalance
 * Signature: (J[F)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegGetBalance
  (JNIEnv *, jobject, jlong, jfloatArray);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegSetBalance
 * Signature: (JF)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegSetBalance
  (JNIEnv *, jobject, jlong, jfloat);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegGetDuration
 * Signature: (J[D)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegGetDuration
  (JNIEnv *, jobject, jlong, jdoubleArray);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegSeek
 * Signature: (JD)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegSeek
  (JNIEnv *, jobject, jlong, jdouble);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegHasAudioData
 * Signature: (J[Z)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegHasAudioData
  (JNIEnv *, jobject, jlong, jbooleanArray);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegHasImageData
 * Signature: (J[Z)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegHasImageData
  (JNIEnv *, jobject, jlong, jbooleanArray);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegGetImageWidth
 * Signature: (J[I)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegGetImageWidth
  (JNIEnv *, jobject, jlong, jintArray);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegGetImageHeight
 * Signature: (J[I)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegGetImageHeight
  (JNIEnv *, jobject, jlong, jintArray);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegGetAudioFormat
 * Signature: (JLjavax/sound/sampled/AudioFormat;)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegGetAudioFormat
  (JNIEnv *, jobject, jlong, jobject);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegGetColorSpace
 * Signature: (JLjava/awt/color/ColorSpace;)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegGetColorSpace
  (JNIEnv *, jobject, jlong, jobject);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegGetImageBuffer
 * Signature: (J[B)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegGetImageBuffer
  (JNIEnv *, jobject, jlong, jbyteArray);

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer
 * Method:    ffmpegGetAudioBuffer
 * Signature: (J[B)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegGetAudioBuffer
  (JNIEnv *, jobject, jlong, jbyteArray);

#ifdef __cplusplus
}
#endif
#endif
