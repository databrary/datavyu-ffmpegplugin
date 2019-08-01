#include "FfmpegJniUtils.h"
#include "FfmpegSdlAvPlaybackPipeline.h"
#include "JavaPlayerEventDispatcher.h"
#include "JniUtils.h"
#include "Media.h"
#include "MediaPlayerErrors.h"
#include "Pipeline.h"

using namespace std;

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegInitPlayer
 * Signature: ([JLjava/lang/String;)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegInitPlayer(
    JNIEnv *env, jobject obj, jlongArray jlMediaHandle, jstring sourcePath) {

  CPipelineOptions *pOptions = new (nothrow) CPipelineOptions();
  if (NULL == pOptions) {
    return ERROR_SYSTEM_ENOMEM;
  }

  CPipeline *pPipeline =
      new (std::nothrow) FfmpegSdlAvPlaybackPipeline(pOptions);

  if (NULL == pPipeline) {
    return ERROR_PIPELINE_NULL;
  }

  CMedia *pMedia = NULL;
  CMedia **ppMedia = &pMedia;

  *ppMedia = new (nothrow) CMedia(pPipeline);

  if (NULL == *ppMedia) {
    delete pOptions;
    delete pPipeline;
    return ERROR_MEDIA_CREATION;
  }

  if (CMedia::IsValid(pMedia)) {
    jlong lMediaHandle = (jlong)ptr_to_jlong(pMedia);
    env->SetLongArrayRegion(jlMediaHandle, 0, 1, &lMediaHandle);
  } else {
    delete pOptions;
    delete pPipeline;
    return ERROR_MEDIA_INVALID;
  }

  CJavaPlayerEventDispatcher *pEventDispatcher =
      new (nothrow) CJavaPlayerEventDispatcher();
  if (NULL == pEventDispatcher)
    return ERROR_SYSTEM_ENOMEM;

  pEventDispatcher->Init(env, obj, pMedia);
  pPipeline->SetEventDispatcher(pEventDispatcher);

  const char *filename = env->GetStringUTFChars(sourcePath, 0);

  jint iRet = (jint)pPipeline->Init(filename);

  env->ReleaseStringUTFChars(sourcePath, filename);

  return iRet;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegDisposePlayer
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegDisposePlayer(
    JNIEnv *env, jobject object, jlong ref_media) {

  CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
  if (NULL == pMedia)
    return ERROR_MEDIA_NULL;

  CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
  if (NULL == pPipeline)
    return ERROR_PIPELINE_NULL;

  // deletes the pipeline by first calling dispose, also deletes pipeline
  // options, and java dispatcher
  delete pMedia;

  return 0;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegGetAudioSyncDelay
 * Signature: (J[J)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegGetAudioSyncDelay(
    JNIEnv *env, jobject obj, jlong ref_media, jlongArray jrglAudioSyncDelay) {

  CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
  if (NULL == pMedia)
    return ERROR_MEDIA_NULL;

  CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
  if (NULL == pPipeline)
    return ERROR_PIPELINE_NULL;

  long lAudioSyncDelay;
  uint32_t uErrCode = pPipeline->GetAudioSyncDelay(&lAudioSyncDelay);
  if (ERROR_NONE != uErrCode)
    return (jint)uErrCode;
  jlong jlAudioSyncDelay = (jlong)lAudioSyncDelay;
  env->SetLongArrayRegion(jrglAudioSyncDelay, 0, 1, &jlAudioSyncDelay);

  return ERROR_NONE;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegSetAudioSyncDelay
 * Signature: (JJ)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegSetAudioSyncDelay(
    JNIEnv *env, jobject obj, jlong ref_media, jlong audio_sync_delay) {

  CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
  if (NULL == pMedia)
    return ERROR_MEDIA_NULL;

  CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
  if (NULL == pPipeline)
    return ERROR_PIPELINE_NULL;

  jint iRet = (jint)pPipeline->SetAudioSyncDelay((long)audio_sync_delay);

  return iRet;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegPlay
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegPlay(
    JNIEnv *env, jobject obj, jlong ref_media) {

  CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
  if (NULL == pMedia)
    return ERROR_MEDIA_NULL;

  CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
  if (NULL == pPipeline)
    return ERROR_PIPELINE_NULL;

  jint iRet = (jint)pPipeline->Play();

  return iRet;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegPause
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegPause(
    JNIEnv *env, jobject obj, jlong ref_media) {

  CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
  if (NULL == pMedia)
    return ERROR_MEDIA_NULL;

  CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
  if (NULL == pPipeline)
    return ERROR_PIPELINE_NULL;

  jint iRet = (jint)pPipeline->Pause();

  return iRet;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegStop
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegStop(
    JNIEnv *env, jobject obj, jlong ref_media) {

  CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
  if (NULL == pMedia)
    return ERROR_MEDIA_NULL;

  CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
  if (NULL == pPipeline)
    return ERROR_PIPELINE_NULL;

  jint iRet = (jint)pPipeline->Stop();

  return iRet;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegStepForward
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegStepForward(
    JNIEnv *env, jobject obj, jlong ref_media) {
  CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
  if (NULL == pMedia)
    return ERROR_MEDIA_NULL;

  CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
  if (NULL == pPipeline)
    return ERROR_PIPELINE_NULL;

  jint iRet = (jint)pPipeline->StepForward();

  return iRet;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegStepBackward
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegStepBackward(
	JNIEnv *env, jobject obj, jlong ref_media) {
  CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
  if (NULL == pMedia) return ERROR_MEDIA_NULL;

  CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
  if (NULL == pPipeline) return ERROR_PIPELINE_NULL;

  jint iRet = (jint)pPipeline->StepBackward();

  return iRet;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegFinish
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegFinish(
    JNIEnv *env, jobject obj, jlong ref_media) {

  CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
  if (NULL == pMedia)
    return ERROR_MEDIA_NULL;

  CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
  if (NULL == pPipeline)
    return ERROR_PIPELINE_NULL;

  jint iRet = (jint)pPipeline->Finish();

  return iRet;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegGetRate
 * Signature: (J[F)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegGetRate(
    JNIEnv *env, jobject obj, jlong ref_media, jfloatArray jrgfRate) {

  CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
  if (NULL == pMedia)
    return ERROR_MEDIA_NULL;

  CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
  if (NULL == pPipeline)
    return ERROR_PIPELINE_NULL;

  float fRate;
  uint32_t uRetCode = pPipeline->GetRate(&fRate);
  if (ERROR_NONE != uRetCode)
    return uRetCode;
  jfloat jfRate = (jfloat)fRate;
  env->SetFloatArrayRegion(jrgfRate, 0, 1, &jfRate);

  return ERROR_NONE;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegSetRate
 * Signature: (JF)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegSetRate(
    JNIEnv *env, jobject obj, jlong ref_media, jfloat rate) {

  CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
  if (NULL == pMedia)
    return ERROR_MEDIA_NULL;

  CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
  if (NULL == pPipeline)
    return ERROR_PIPELINE_NULL;

  jint iRet = (jint)pPipeline->SetRate(rate);

  return iRet;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegGetPresentationTime
 * Signature: (J[D)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegGetPresentationTime(
    JNIEnv *env, jobject obj, jlong ref_media,
    jdoubleArray jrgdPresentationTime) {

  CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
  if (NULL == pMedia)
    return ERROR_MEDIA_NULL;

  CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
  if (NULL == pPipeline)
    return ERROR_PIPELINE_NULL;

  double dPresentationTime;
  uint32_t uRetCode = pPipeline->GetStreamTime(&dPresentationTime);
  if (ERROR_NONE != uRetCode)
    return uRetCode;
  jdouble jdPresentationTime = (double)dPresentationTime;
  env->SetDoubleArrayRegion(jrgdPresentationTime, 0, 1, &jdPresentationTime);

  return ERROR_NONE;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegGetFps
 * Signature: (J[D)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegGetFps(
    JNIEnv *env, jobject obj, jlong ref_media, jdoubleArray jdFps) {
  CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
  if (NULL == pMedia)
    return ERROR_MEDIA_NULL;

  CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
  if (NULL == pPipeline)
    return ERROR_PIPELINE_NULL;

  double dFps;
  uint32_t uRetCode = pPipeline->GetFps(&dFps);
  if (ERROR_NONE != uRetCode)
    return uRetCode;

  env->SetDoubleArrayRegion(jdFps, 0, 1, &dFps);

  return ERROR_NONE;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegGetBalance
 * Signature: (J[F)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegGetBalance(
    JNIEnv *env, jobject obj, jlong ref_media, jfloatArray jrgfBalance) {

  CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
  if (NULL == pMedia)
    return ERROR_MEDIA_NULL;

  CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
  if (NULL == pPipeline)
    return ERROR_PIPELINE_NULL;

  float fBalance;
  uint32_t uErrCode = pPipeline->GetBalance(&fBalance);
  if (ERROR_NONE != uErrCode)
    return uErrCode;
  jfloat jfBalance = (jfloat)fBalance;
  env->SetFloatArrayRegion(jrgfBalance, 0, 1, &jfBalance);

  return ERROR_NONE;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegSetBalance
 * Signature: (JF)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegSetBalance(
    JNIEnv *env, jobject obj, jlong ref_media, jfloat balance) {

  CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
  if (NULL == pMedia)
    return ERROR_MEDIA_NULL;

  CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
  if (NULL == pPipeline)
    return ERROR_PIPELINE_NULL;

  jint iRet = (jint)pPipeline->SetBalance((float)balance);

  return iRet;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegGetDuration
 * Signature: (J[D)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegGetDuration(
    JNIEnv *env, jobject obj, jlong ref_media, jdoubleArray jrgdDuration) {

  CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
  if (NULL == pMedia)
    return ERROR_MEDIA_NULL;

  CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
  if (NULL == pPipeline)
    return ERROR_PIPELINE_NULL;

  double dDuration;
  uint32_t uErrCode = pPipeline->GetDuration(&dDuration);
  if (ERROR_NONE != uErrCode)
    return uErrCode;
  jdouble jdDuration = (jdouble)dDuration;
  env->SetDoubleArrayRegion(jrgdDuration, 0, 1, &jdDuration);

  return ERROR_NONE;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegSeek
 * Signature: (JD)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegSeek(
	JNIEnv *env, jobject obj, jlong ref_media, jdouble stream_time,
	jint seek_flags) {

  CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
  if (NULL == pMedia) return ERROR_MEDIA_NULL;

  CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
  if (NULL == pPipeline) return ERROR_PIPELINE_NULL;

  jint iRet = (jint)pPipeline->Seek(stream_time, seek_flags);

  return iRet;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegSeekToFrame
 * Signature: (JD)I
*/
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegSeekToFrame(
	JNIEnv *env, jobject obj, jlong ref_media, jint frame_nb) {
	CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
	if (NULL == pMedia) return ERROR_MEDIA_NULL;

	CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
	if (NULL == pPipeline) return ERROR_PIPELINE_NULL;

	jint iRet = (jint)pPipeline->SeekToFrame(frame_nb);

	return iRet;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegGetImageWidth
 * Signature: (J[I)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegGetImageWidth(
	JNIEnv *env, jobject obj, jlong ref_media, jintArray jriImageWidth) {
	CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
	if (NULL == pMedia)
		return ERROR_MEDIA_NULL;

	CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
	if (NULL == pPipeline)
		return ERROR_PIPELINE_NULL;

	int iImageWidth;
	uint32_t uErrCode = pPipeline->GetImageWidth(&iImageWidth);
	if (ERROR_NONE != uErrCode)
		return uErrCode;
	jint jiImageWidth = (jint)iImageWidth;
	env->SetIntArrayRegion(jriImageWidth, 0, 1, &jiImageWidth);

	return ERROR_NONE;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegGetImageHeight
 * Signature: (J[I)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegGetImageHeight(
	JNIEnv *env, jobject obj, jlong ref_media, jintArray jriImageWidth) {
	CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
	if (NULL == pMedia)
		return ERROR_MEDIA_NULL;

	CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
	if (NULL == pPipeline)
		return ERROR_PIPELINE_NULL;

	int iImageHeight;
	uint32_t uErrCode = pPipeline->GetImageHeight(&iImageHeight);
	if (ERROR_NONE != uErrCode)
		return uErrCode;
	jint jiImageHeight = (jint)iImageHeight;
	env->SetIntArrayRegion(jriImageWidth, 0, 1, &jiImageHeight);

	return ERROR_NONE;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegGetVolume
 * Signature: (J[F)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegGetVolume(
    JNIEnv *env, jobject obj, jlong ref_media, jfloatArray jrgfVolume) {

  CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
  if (NULL == pMedia)
    return ERROR_MEDIA_NULL;

  CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
  if (NULL == pPipeline)
    return ERROR_PIPELINE_NULL;

  float fVolume;
  uint32_t uRetCode = pPipeline->GetVolume(&fVolume);
  if (ERROR_NONE != uRetCode)
    return uRetCode;
  jfloat jfVolume = (jfloat)fVolume;
  env->SetFloatArrayRegion(jrgfVolume, 0, 1, &jfVolume);

  return ERROR_NONE;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegSetVolume
 * Signature: (JF)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegSetVolume(
    JNIEnv *env, jobject obj, jlong ref_media, jfloat volume) {

  CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
  if (NULL == pMedia)
    return ERROR_MEDIA_NULL;

  CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
  if (NULL == pPipeline)
    return ERROR_PIPELINE_NULL;

  jint iRet = (jint)pPipeline->SetVolume((float)volume);

  return iRet;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegGetWindowWidth
 * Signature: (J[I)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegGetWindowSize
(JNIEnv *env, jobject obj, jlong ref_media, jintArray jriWindowWidth, jintArray jriWindowHeight) {
	CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
	if (NULL == pMedia)
		return ERROR_MEDIA_NULL;

	CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
	if (NULL == pPipeline)
		return ERROR_PIPELINE_NULL;
#ifdef SDL_ENABLED
	int iWindowWidth;
	int iWindowHeight;
	uint32_t uErrCode = pPipeline->GetWindowSize(&iWindowWidth, &iWindowHeight);
	if (ERROR_NONE != uErrCode)
		return uErrCode;
	jint jiWindowWidth = (jint)iWindowWidth;
	jint jiWindowHeight = (jint)iWindowHeight;
	env->SetIntArrayRegion(jriWindowWidth, 0, 1, &jiWindowWidth);
	env->SetIntArrayRegion(jriWindowHeight, 0, 1, &jiWindowHeight);
#endif
	return ERROR_NONE;
}

/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegSetWindowSize
 * Signature: (JII)I
 */
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegSetWindowSize
(JNIEnv *env, jobject obj, jlong ref_media, jint jiWidth, jint jiHeight) {
	CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
	if (NULL == pMedia)
		return ERROR_MEDIA_NULL;

	CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
	if (NULL == pPipeline)
		return ERROR_PIPELINE_NULL;
#ifdef SDL_ENABLED
	jint iRet = (jint)pPipeline->SetWindowSize(jiWidth, jiHeight);
#endif
	return iRet;
}
    
/*
* Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
* Method:    ffmpegShowWindow
* Signature: (J)I
*/
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegShowWindow(
    JNIEnv *env, jobject object, jlong ref_media) {
    CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
    if (NULL == pMedia)
        return ERROR_MEDIA_NULL;
    
    CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
    if (NULL == pPipeline)
        return ERROR_PIPELINE_NULL;
    
    jint iRet = 0;
#ifdef SDL_ENABLED
    iRet = (jint)pPipeline->ShowWindow();
#endif
    return iRet;
}
    
/*
 * Class:     org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer
 * Method:    ffmpegHideWindow
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL
Java_org_datavyu_plugins_ffmpeg_FfmpegSdlMediaPlayer_ffmpegHideWindow(
    JNIEnv *env, jobject object, jlong ref_media) {
    CMedia *pMedia = (CMedia *)jlong_to_ptr(ref_media);
    if (NULL == pMedia)
        return ERROR_MEDIA_NULL;
    
    CPipeline *pPipeline = (CPipeline *)pMedia->GetPipeline();
    if (NULL == pPipeline)
        return ERROR_PIPELINE_NULL;
    
    jint iRet = 0;
#ifdef SDL_ENABLED
    iRet = (jint)pPipeline->HideWindow();
#endif
    return iRet;
}

#ifdef __cplusplus
}
#endif
