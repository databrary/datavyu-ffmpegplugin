/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_datavyu_plugins_mpv_MpvMediaPlayer */

#ifndef _Included_org_datavyu_plugins_mpv_MpvMediaPlayer
#define _Included_org_datavyu_plugins_mpv_MpvMediaPlayer
#ifdef __cplusplus
extern "C" {
#endif
#undef org_datavyu_plugins_mpv_MpvMediaPlayer_eventPlayerUnknown
#define org_datavyu_plugins_mpv_MpvMediaPlayer_eventPlayerUnknown 100L
#undef org_datavyu_plugins_mpv_MpvMediaPlayer_eventPlayerReady
#define org_datavyu_plugins_mpv_MpvMediaPlayer_eventPlayerReady 101L
#undef org_datavyu_plugins_mpv_MpvMediaPlayer_eventPlayerPlaying
#define org_datavyu_plugins_mpv_MpvMediaPlayer_eventPlayerPlaying 102L
#undef org_datavyu_plugins_mpv_MpvMediaPlayer_eventPlayerPaused
#define org_datavyu_plugins_mpv_MpvMediaPlayer_eventPlayerPaused 103L
#undef org_datavyu_plugins_mpv_MpvMediaPlayer_eventPlayerStopped
#define org_datavyu_plugins_mpv_MpvMediaPlayer_eventPlayerStopped 104L
#undef org_datavyu_plugins_mpv_MpvMediaPlayer_eventPlayerStalled
#define org_datavyu_plugins_mpv_MpvMediaPlayer_eventPlayerStalled 105L
#undef org_datavyu_plugins_mpv_MpvMediaPlayer_eventPlayerFinished
#define org_datavyu_plugins_mpv_MpvMediaPlayer_eventPlayerFinished 106L
#undef org_datavyu_plugins_mpv_MpvMediaPlayer_eventPlayerError
#define org_datavyu_plugins_mpv_MpvMediaPlayer_eventPlayerError 107L
/*
 * Class:     org_datavyu_plugins_mpv_MpvMediaPlayer
 * Method:    mpvInitPlayer
 * Signature: ([JLjava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_mpv_MpvMediaPlayer_mpvInitPlayer
  (JNIEnv *, jobject, jlongArray, jstring, jlong);

/*
 * Class:     org_datavyu_plugins_mpv_MpvMediaPlayer
 * Method:    mpvDisposePlayer
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_mpv_MpvMediaPlayer_mpvDisposePlayer
  (JNIEnv *, jobject, jlong);

/*
 * Class:     org_datavyu_plugins_mpv_MpvMediaPlayer
 * Method:    mpvPlay
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_mpv_MpvMediaPlayer_mpvPlay
  (JNIEnv *, jobject, jlong);

/*
 * Class:     org_datavyu_plugins_mpv_MpvMediaPlayer
 * Method:    mpvPause
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_mpv_MpvMediaPlayer_mpvPause
  (JNIEnv *, jobject, jlong);

/*
 * Class:     org_datavyu_plugins_mpv_MpvMediaPlayer
 * Method:    mpvStop
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_mpv_MpvMediaPlayer_mpvStop
  (JNIEnv *, jobject, jlong);

/*
 * Class:     org_datavyu_plugins_mpv_MpvMediaPlayer
 * Method:    mpvStepForward
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_mpv_MpvMediaPlayer_mpvStepForward
  (JNIEnv *, jobject, jlong);

/*
 * Class:     org_datavyu_plugins_mpv_MpvMediaPlayer
 * Method:    mpvStepBackward
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_mpv_MpvMediaPlayer_mpvStepBackward
  (JNIEnv *, jobject, jlong);

/*
 * Class:     org_datavyu_plugins_mpv_MpvMediaPlayer
 * Method:    mpvFinish
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_mpv_MpvMediaPlayer_mpvFinish
  (JNIEnv *, jobject, jlong);

/*
 * Class:     org_datavyu_plugins_mpv_MpvMediaPlayer
 * Method:    mpvGetRate
 * Signature: (J[F)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_mpv_MpvMediaPlayer_mpvGetRate
  (JNIEnv *, jobject, jlong, jfloatArray);

/*
 * Class:     org_datavyu_plugins_mpv_MpvMediaPlayer
 * Method:    mpvSetRate
 * Signature: (JF)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_mpv_MpvMediaPlayer_mpvSetRate
  (JNIEnv *, jobject, jlong, jfloat);

/*
 * Class:     org_datavyu_plugins_mpv_MpvMediaPlayer
 * Method:    mpvGetPresentationTime
 * Signature: (J[D)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_mpv_MpvMediaPlayer_mpvGetPresentationTime
  (JNIEnv *, jobject, jlong, jdoubleArray);

/*
 * Class:     org_datavyu_plugins_mpv_MpvMediaPlayer
 * Method:    mpvGetFps
 * Signature: (J[D)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_mpv_MpvMediaPlayer_mpvGetFps
  (JNIEnv *, jobject, jlong, jdoubleArray);

/*
 * Class:     org_datavyu_plugins_mpv_MpvMediaPlayer
 * Method:    mpvGetDuration
 * Signature: (J[D)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_mpv_MpvMediaPlayer_mpvGetDuration
  (JNIEnv *, jobject, jlong, jdoubleArray);

/*
 * Class:     org_datavyu_plugins_mpv_MpvMediaPlayer
 * Method:    mpvSeek
 * Signature: (JD)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_mpv_MpvMediaPlayer_mpvSeek
  (JNIEnv *, jobject, jlong, jdouble);

/*
 * Class:     org_datavyu_plugins_mpv_MpvMediaPlayer
 * Method:    mpvGetImageWidth
 * Signature: (J[I)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_mpv_MpvMediaPlayer_mpvGetImageWidth
(JNIEnv *, jobject, jlong, jintArray);

/*
 * Class:     org_datavyu_plugins_mpv_MpvMediaPlayer
 * Method:    mpvGetImageHeight
 * Signature: (J[I)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_mpv_MpvMediaPlayer_mpvGetImageHeight
(JNIEnv *, jobject, jlong, jintArray);

/*
 * Class:     org_datavyu_plugins_mpv_MpvMediaPlayer
 * Method:    mpvGetVolume
 * Signature: (J[F)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_mpv_MpvMediaPlayer_mpvGetVolume
  (JNIEnv *, jobject, jlong, jfloatArray);

/*
 * Class:     org_datavyu_plugins_mpv_MpvMediaPlayer
 * Method:    mpvSetVolume
 * Signature: (JF)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_mpv_MpvMediaPlayer_mpvSetVolume
  (JNIEnv *, jobject, jlong, jfloat);

#ifdef __cplusplus
}
#endif
#endif
