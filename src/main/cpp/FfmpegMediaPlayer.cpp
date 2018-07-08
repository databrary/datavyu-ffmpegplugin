#include "org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer.h"

#include "JniUtils.h"
#include "JavaPlayerEventDispatcher.h"
#include "Media.h"
#include "Pipeline.h"
#include "FfmpegMediaErrors.h"
#include "FfmpegAVPlaybackPipeline.h"

using namespace std;

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegInitPlayer
(JNIEnv *env, jobject obj, jlong ref_media, jobject source, jobject audioFormat, jobject imageFormat) {
	// TODO(fraudies): Intialize the ffmpeg player

	CPipelineOptions* pOptions = new (nothrow) CPipelineOptions();
	if (NULL == pOptions) {
		return ERROR_MEMORY_ALLOCATION;
	}

	CPipeline* pPipeline = new (nothrow) FfmpegAVPlaybackPipeline(pOptions);
	if (NULL == pPipeline) {
		return ERROR_PIPELINE_CREATION;
	}

	CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
	CMedia** ppMedia = &pMedia;

	*ppMedia = new(nothrow) CMedia(pPipeline);

	if (NULL == *ppMedia) {
		delete pOptions;
		delete pPipeline;
		return ERROR_MEDIA_CREATION;
	}

	CJavaPlayerEventDispatcher* pEventDispatcher = new(nothrow) CJavaPlayerEventDispatcher();
	if (NULL == pEventDispatcher)
		return ERROR_MEMORY_ALLOCATION;

	pEventDispatcher->Init(env, obj, pMedia);
	pPipeline->SetEventDispatcher(pEventDispatcher);

	jint iRet = (jint)pPipeline->Init();

	return iRet;
}

JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegDisposePlayer
(JNIEnv *env, jobject object, jlong ref_media) {

	CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
	if (NULL == pMedia)
		return ERROR_MEDIA_NULL;

	CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
	if (NULL == pPipeline)
		return ERROR_PIPELINE_NULL;

	// TODO(fraudies): When/where do we delete the event dispatcher?	
	pPipeline->Dispose();

	delete pPipeline->GetCPipelineOptions();
	delete pPipeline;
	delete pMedia;

	return 0;
}

/**
* ffmpegGetAudioSyncDelay()
*
* Gets the audio sync delay for the media.
*/
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegGetAudioSyncDelay
(JNIEnv *env, jobject obj, jlong ref_media, jlongArray jrglAudioSyncDelay)
{

	CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
	if (NULL == pMedia)
		return ERROR_MEDIA_NULL;

	CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
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

/**
* ffmpegSetAudioSyncDelay()
*
* Sets the audio sync delay for the media.
*/
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegSetAudioSyncDelay
(JNIEnv *env, jobject obj, jlong ref_media, jlong audio_sync_delay)
{
	CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
	if (NULL == pMedia)
		return ERROR_MEDIA_NULL;

	CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
	if (NULL == pPipeline)
		return ERROR_PIPELINE_NULL;

	jint iRet = (jint)pPipeline->SetAudioSyncDelay((long)audio_sync_delay);

	return iRet;
}

/**
* ffmpegPlay()
*
* Makes an asynchronous call to play the media.
* 
* The state is updated by the pipeline.
*/
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegPlay
(JNIEnv *env, jobject obj, jlong ref_media)
{
	CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
	if (NULL == pMedia)
		return ERROR_MEDIA_NULL;

	CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
	if (NULL == pPipeline)
		return ERROR_PIPELINE_NULL;

	jint iRet = (jint)pPipeline->Play();

	return iRet;
}

/**
* ffmpegPause()
*
* Makes an asynchronous call to pause the media playback.
*/
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegPause
(JNIEnv *env, jobject obj, jlong ref_media)
{

	CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
	if (NULL == pMedia)
		return ERROR_MEDIA_NULL;

	CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
	if (NULL == pPipeline)
		return ERROR_PIPELINE_NULL;

	jint iRet = (jint)pPipeline->Pause();

	return iRet;
}

/**
* ffmpegStop()
*
* Makes an asynchronous call to sotp the media playback.
*/
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegStop
(JNIEnv *env, jobject obj, jlong ref_media)
{

	CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
	if (NULL == pMedia)
		return ERROR_MEDIA_NULL;

	CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
	if (NULL == pPipeline)
		return ERROR_PIPELINE_NULL;

	jint iRet = (jint)pPipeline->Stop();

	return iRet;
}

/**
* ffmpegFinish()
*
* Makes an asynchronous call to finish the media playback.
*/
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegFinish
(JNIEnv *env, jobject obj, jlong ref_media)
{

	CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
	if (NULL == pMedia)
		return ERROR_MEDIA_NULL;

	CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
	if (NULL == pPipeline)
		return ERROR_PIPELINE_NULL;

	jint iRet = (jint)pPipeline->Finish();

	return iRet;
}

/**
* ffmpegGetRate()
*
* Makes a synchronous call to get the media playback rate.
*/
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegGetRate
(JNIEnv *env, jobject obj, jlong ref_media, jfloatArray jrgfRate)
{

	CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
	if (NULL == pMedia)
		return ERROR_MEDIA_NULL;

	CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
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

/**
* ffmpegSetRate()
*
* Makes an asynchronous call to set the media playback rate.
*/
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegSetRate
(JNIEnv *env, jobject obj, jlong ref_media, jfloat rate)
{

	CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
	if (NULL == pMedia)
		return ERROR_MEDIA_NULL;

	CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
	if (NULL == pPipeline)
		return ERROR_PIPELINE_NULL;

	jint iRet = (jint)pPipeline->SetRate(rate);

	return iRet;
}

/**
* ffmpegGetPresentationTime()
*
* Makes a synchronous call to get the media presentation/stream time.
*/
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegGetPresentationTime
(JNIEnv *env, jobject obj, jlong ref_media, jdoubleArray jrgdPresentationTime)
{

	CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
	if (NULL == pMedia)
		return ERROR_MEDIA_NULL;

	CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
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

/**
* ffmpegGetVolume()
*
* Makes a synchronous call to get the audio volume.
*/
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegGetVolume
(JNIEnv *env, jobject obj, jlong ref_media, jfloatArray jrgfVolume)
{

	CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
	if (NULL == pMedia)
		return ERROR_MEDIA_NULL;

	CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
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

/**
* ffmpegSetVolume()
*
* Makes an asynchronous call to set the audio volume.
*/
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegSetVolume
(JNIEnv *env, jobject obj, jlong ref_media, jfloat volume)
{

	CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
	if (NULL == pMedia)
		return ERROR_MEDIA_NULL;

	CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
	if (NULL == pPipeline)
		return ERROR_PIPELINE_NULL;

	jint iRet = (jint)pPipeline->SetVolume((float)volume);

	return iRet;
}

/**
* ffmpegGetBalance()
*
* Makes a synchronous call to get the audio balance.
*/
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegGetBalance
(JNIEnv *env, jobject obj, jlong ref_media, jfloatArray jrgfBalance)
{

	CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
	if (NULL == pMedia)
		return ERROR_MEDIA_NULL;

	CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
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

/**
* ffmpegSetBalance()
*
* Makes an asynchronous call to set the audio balance.
*/
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegSetBalance
(JNIEnv *env, jobject obj, jlong ref_media, jfloat balance)
{

	CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
	if (NULL == pMedia)
		return ERROR_MEDIA_NULL;

	CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
	if (NULL == pPipeline)
		return ERROR_PIPELINE_NULL;

	jint iRet = (jint)pPipeline->SetBalance((float)balance);

	return iRet;
}

/**
* ffmpegGetDuration()
*
* Makes a synchronous call to get the duration of the media.
*/
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegGetDuration
(JNIEnv *env, jobject obj, jlong ref_media, jdoubleArray jrgdDuration)
{

	CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
	if (NULL == pMedia)
		return ERROR_MEDIA_NULL;

	CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
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

/**
* ffmpegSeek()
*
* Makes an asynchronous call to seek to a presentation time in the media.
*/
JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegMediaPlayer_ffmpegSeek
(JNIEnv *env, jobject obj, jlong ref_media, jdouble stream_time)
{

	CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
	if (NULL == pMedia)
		return ERROR_MEDIA_NULL;

	CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
	if (NULL == pPipeline)
		return ERROR_PIPELINE_NULL;

	jint iRet = (jint)pPipeline->Seek(stream_time);

	return iRet;
}

#ifdef __cplusplus
}
#endif