/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_datavyu_plugins_ffmpegplayer_MovieStream */

#ifndef _Included_org_datavyu_plugins_ffmpegplayer_MovieStream
#define _Included_org_datavyu_plugins_ffmpegplayer_MovieStream
#ifdef __cplusplus
extern "C" {
#endif
#undef org_datavyu_plugins_ffmpegplayer_MovieStream_AUDIO_BUFFER_SIZE
#define org_datavyu_plugins_ffmpegplayer_MovieStream_AUDIO_BUFFER_SIZE 65536L
/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    getAverageFrameRate0
 * Signature: (I)D
 */
JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getAverageFrameRate0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    hasVideoStream0
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_hasVideoStream0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    hasAudioStream0
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_hasAudioStream0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    getStartTime0
 * Signature: (I)D
 */
JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getStartTime0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    getEndTime0
 * Signature: (I)D
 */
JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getEndTime0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    getDuration0
 * Signature: (I)D
 */
JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getDuration0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    getCurrentTime0
 * Signature: (I)D
 */
JNIEXPORT jdouble JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getCurrentTime0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    start0
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_start0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    stop0
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_stop0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    setCurrentTime0
 * Signature: (ID)V
 */
JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_setCurrentTime0
  (JNIEnv *, jclass, jint, jdouble);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    setPlaybackSpeed0
 * Signature: (IF)V
 */
JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_setPlaybackSpeed0
  (JNIEnv *, jclass, jint, jfloat);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    reset0
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_reset0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    close0
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_close0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    loadNextAudioData0
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_loadNextAudioData0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    getAudioBuffer0
 * Signature: (II)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getAudioBuffer0
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    getSampleFormat0
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getSampleFormat0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    getCodecName0
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getCodecName0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    getSampleRate0
 * Signature: (I)F
 */
JNIEXPORT jfloat JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getSampleRate0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    getSampleSizeInBits0
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getSampleSizeInBits0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    getNumberOfSoundChannels0
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getNumberOfSoundChannels0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    getFrameSize0
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getFrameSize0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    getFrameRate0
 * Signature: (I)F
 */
JNIEXPORT jfloat JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getFrameRate0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    bigEndian0
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_bigEndian0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    open0
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljavax/sound/sampled/AudioFormat;)[I
 */
JNIEXPORT jintArray JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_open0
  (JNIEnv *, jclass, jstring, jstring, jobject);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    getNumberOfColorChannels0
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getNumberOfColorChannels0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    getHeight0
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getHeight0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    getWidth0
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getWidth0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    getFrameBuffer0
 * Signature: (I)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_getFrameBuffer0
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_datavyu_plugins_ffmpegplayer_MovieStream
 * Method:    loadNextImageFrame0
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpegplayer_MovieStream_loadNextImageFrame0
  (JNIEnv *, jclass, jint);

#ifdef __cplusplus
}
#endif
#endif
