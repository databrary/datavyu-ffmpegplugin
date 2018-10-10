#include "JniUtils.h"
#include "JavaPlayerEventDispatcher.h"
#include "Media.h"
#include "Pipeline.h"
#include "FfmpegMediaErrors.h"
#include "FfmpegJniUtils.h"
#include "MpvAvPlaybackPipeline.h"
#include "jawt_md.h"

using namespace std;

#ifdef __cplusplus
extern "C" {
#endif

	/*
	* Class:     org_datavyu_plugins_ffmpeg_MpvMediaPlayer
	* Method:    mpvInitPlayer
	* Signature: ([JLjava/lang/String;)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_MpvMediaPlayer_mpvInitPlayer
	(JNIEnv *env, jobject obj, jlongArray jlMediaHandle, jstring sourcePath, jlong windowID) {

		CPipelineOptions* pOptions = new (nothrow) CPipelineOptions();
		if (NULL == pOptions) {
			return ERROR_SYSTEM_ENOMEM;
		}

		CPipeline* pPipeline = new (std::nothrow) MpvAvPlaybackPipeline(pOptions, (intptr_t)windowID);

		if (NULL == pPipeline) {
			return ERROR_PIPELINE_NULL;
		}

		CMedia* pMedia = NULL;
		CMedia** ppMedia = &pMedia;

		*ppMedia = new(nothrow) CMedia(pPipeline);

		if (NULL == *ppMedia) {
			delete pOptions;
			delete pPipeline;
			return ERROR_MEDIA_CREATION;
		}

		if (CMedia::IsValid(pMedia)) {
			jlong lMediaHandle = (jlong)ptr_to_jlong(pMedia);
			env->SetLongArrayRegion(jlMediaHandle, 0, 1, &lMediaHandle);
		}
		else {
			delete pOptions;
			delete pPipeline;
			return ERROR_MEDIA_INVALID;
		}

		CJavaPlayerEventDispatcher* pEventDispatcher = new(nothrow) CJavaPlayerEventDispatcher();
		if (NULL == pEventDispatcher)
			return ERROR_SYSTEM_ENOMEM;

		pEventDispatcher->Init(env, obj, pMedia);
		pPipeline->SetEventDispatcher(pEventDispatcher);

		const char* filename = env->GetStringUTFChars(sourcePath, 0);

		jint iRet = (jint)pPipeline->Init(filename);

		env->ReleaseStringUTFChars(sourcePath, filename);

		return iRet;
	}

	/*
	* Class:     org_datavyu_plugins_ffmpeg_MpvMediaPlayer
	* Method:    mpvDisposePlayer
	* Signature: (J)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_MpvMediaPlayer_mpvDisposePlayer
	(JNIEnv *env, jobject object, jlong ref_media) {

		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia)
			return ERROR_MEDIA_NULL;

		CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
		if (NULL == pPipeline)
			return ERROR_PIPELINE_NULL;

		// deletes the pipeline by first calling dispose, also deletes pipeline options, and java dispatcher
		delete pMedia;

		return 0;
	}

	/*
	* Class:     org_datavyu_plugins_ffmpeg_MpvMediaPlayer
	* Method:    mpvGetAudioSyncDelay
	* Signature: (J[J)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_MpvMediaPlayer_mpvGetAudioSyncDelay
	(JNIEnv *env, jobject obj, jlong ref_media, jlongArray jrglAudioSyncDelay) {

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

	/*
	* Class:     org_datavyu_plugins_ffmpeg_MpvMediaPlayer
	* Method:    mpvSetAudioSyncDelay
	* Signature: (JJ)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_MpvMediaPlayer_mpvSetAudioSyncDelay
	(JNIEnv *env, jobject obj, jlong ref_media, jlong audio_sync_delay) {

		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia)
			return ERROR_MEDIA_NULL;

		CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
		if (NULL == pPipeline)
			return ERROR_PIPELINE_NULL;

		jint iRet = (jint)pPipeline->SetAudioSyncDelay((long)audio_sync_delay);

		return iRet;
	}

	/*
	* Class:     org_datavyu_plugins_ffmpeg_MpvMediaPlayer
	* Method:    mpvPlay
	* Signature: (J)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_MpvMediaPlayer_mpvPlay
	(JNIEnv *env, jobject obj, jlong ref_media) {

		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia)
			return ERROR_MEDIA_NULL;

		CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
		if (NULL == pPipeline)
			return ERROR_PIPELINE_NULL;

		jint iRet = (jint)pPipeline->Play();

		return iRet;
	}

	/*
	* Class:     org_datavyu_plugins_ffmpeg_MpvMediaPlayer
	* Method:    mpvPause
	* Signature: (J)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_MpvMediaPlayer_mpvPause
	(JNIEnv *env, jobject obj, jlong ref_media) {

		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia)
			return ERROR_MEDIA_NULL;

		CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
		if (NULL == pPipeline)
			return ERROR_PIPELINE_NULL;

		jint iRet = (jint)pPipeline->Pause();

		return iRet;
	}

	/*
	* Class:     org_datavyu_plugins_ffmpeg_MpvMediaPlayer
	* Method:    mpvStop
	* Signature: (J)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_MpvMediaPlayer_mpvStop
	(JNIEnv *env, jobject obj, jlong ref_media) {

		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia)
			return ERROR_MEDIA_NULL;

		CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
		if (NULL == pPipeline)
			return ERROR_PIPELINE_NULL;

		jint iRet = (jint)pPipeline->Stop();

		return iRet;
	}

	/*
	* Class:     org_datavyu_plugins_ffmpeg_MpvMediaPlayer
	* Method:    mpvStepForward
	* Signature: (J)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_MpvMediaPlayer_mpvStepForward
	(JNIEnv *env, jobject obj, jlong ref_media) {
		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia)
			return ERROR_MEDIA_NULL;

		CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
		if (NULL == pPipeline)
			return ERROR_PIPELINE_NULL;

		jint iRet = (jint)pPipeline->StepForward();

		return iRet;
	}

	/*
	* Class:     org_datavyu_plugins_ffmpeg_MpvMediaPlayer
	* Method:    mpvStepBackward
	* Signature: (J)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_MpvMediaPlayer_mpvStepBackward
	(JNIEnv *env, jobject obj, jlong ref_media) {
		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia)
			return ERROR_MEDIA_NULL;

		CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
		if (NULL == pPipeline)
			return ERROR_PIPELINE_NULL;

		jint iRet = (jint)pPipeline->StepBackward();

		return iRet;
	}

	/*
	* Class:     org_datavyu_plugins_ffmpeg_MpvMediaPlayer
	* Method:    mpvFinish
	* Signature: (J)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_MpvMediaPlayer_mpvFinish
	(JNIEnv *env, jobject obj, jlong ref_media) {

		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia)
			return ERROR_MEDIA_NULL;

		CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
		if (NULL == pPipeline)
			return ERROR_PIPELINE_NULL;

		jint iRet = (jint)pPipeline->Finish();

		return iRet;
	}

	/*
	* Class:     org_datavyu_plugins_ffmpeg_MpvMediaPlayer
	* Method:    mpvGetRate
	* Signature: (J[F)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_MpvMediaPlayer_mpvGetRate
	(JNIEnv *env, jobject obj, jlong ref_media, jfloatArray jrgfRate) {

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

	/*
	* Class:     org_datavyu_plugins_ffmpeg_MpvMediaPlayer
	* Method:    mpvSetRate
	* Signature: (JF)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_MpvMediaPlayer_mpvSetRate
	(JNIEnv *env, jobject obj, jlong ref_media, jfloat rate) {

		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia)
			return ERROR_MEDIA_NULL;

		CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
		if (NULL == pPipeline)
			return ERROR_PIPELINE_NULL;

		jint iRet = (jint)pPipeline->SetRate(rate);

		return iRet;
	}

	/*
	* Class:     org_datavyu_plugins_ffmpeg_MpvMediaPlayer
	* Method:    mpvGetPresentationTime
	* Signature: (J[D)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_MpvMediaPlayer_mpvGetPresentationTime
	(JNIEnv *env, jobject obj, jlong ref_media, jdoubleArray jrgdPresentationTime) {

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

	/*
	* Class:     org_datavyu_plugins_ffmpeg_MpvMediaPlayer
	* Method:    mpvGetFps
	* Signature: (J[D)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_MpvMediaPlayer_mpvGetFps
	(JNIEnv *env, jobject obj, jlong ref_media, jdoubleArray jdFps) {
		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia)
			return ERROR_MEDIA_NULL;

		CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
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
	* Class:     org_datavyu_plugins_ffmpeg_MpvMediaPlayer
	* Method:    mpvGetBalance
	* Signature: (J[F)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_MpvMediaPlayer_mpvGetBalance
	(JNIEnv *env, jobject obj, jlong ref_media, jfloatArray jrgfBalance) {

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

	/*
	* Class:     org_datavyu_plugins_ffmpeg_MpvMediaPlayer
	* Method:    mpvSetBalance
	* Signature: (JF)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_MpvMediaPlayer_mpvSetBalance
	(JNIEnv *env, jobject obj, jlong ref_media, jfloat balance) {

		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia)
			return ERROR_MEDIA_NULL;

		CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
		if (NULL == pPipeline)
			return ERROR_PIPELINE_NULL;

		jint iRet = (jint)pPipeline->SetBalance((float)balance);

		return iRet;
	}

	/*
	* Class:     org_datavyu_plugins_ffmpeg_MpvMediaPlayer
	* Method:    mpvGetDuration
	* Signature: (J[D)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_MpvMediaPlayer_mpvGetDuration
	(JNIEnv *env, jobject obj, jlong ref_media, jdoubleArray jrgdDuration) {

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

	/*
	* Class:     org_datavyu_plugins_ffmpeg_MpvMediaPlayer
	* Method:    mpvSeek
	* Signature: (JD)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_MpvMediaPlayer_mpvSeek
	(JNIEnv *env, jobject obj, jlong ref_media, jdouble stream_time) {

		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia)
			return ERROR_MEDIA_NULL;

		CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
		if (NULL == pPipeline)
			return ERROR_PIPELINE_NULL;

		jint iRet = (jint)pPipeline->Seek(stream_time);

		return iRet;
	}

	/*
	 * Class:     org_datavyu_plugins_ffmpeg_MpvMediaPlayer
	 * Method:    mpvGetImageWidth
	 * Signature: (J[I)I
	 */
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_MpvMediaPlayer_mpvGetImageWidth
	(JNIEnv *env, jobject obj, jlong ref_media, jintArray jriImageWidth) {
		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia)
			return ERROR_MEDIA_NULL;

		CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
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
	 * Class:     org_datavyu_plugins_ffmpeg_MpvMediaPlayer
	 * Method:    mpvGetImageHeight
	 * Signature: (J[I)I
	 */
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_MpvMediaPlayer_mpvGetImageHeight
	(JNIEnv *env, jobject obj, jlong ref_media, jintArray jriImageWidth) {
		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia)
			return ERROR_MEDIA_NULL;

		CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
		if (NULL == pPipeline)
			return ERROR_PIPELINE_NULL;

		int iImageWidth;
		uint32_t uErrCode = pPipeline->GetImageHeight(&iImageWidth);
		if (ERROR_NONE != uErrCode)
			return uErrCode;
		jint jiImageWidth = (jint)iImageWidth;
		env->SetIntArrayRegion(jriImageWidth, 0, 1, &jiImageWidth);

		return ERROR_NONE;
	}

	/*
	* Class:     org_datavyu_plugins_ffmpeg_MpvMediaPlayer
	* Method:    mpvGetVolume
	* Signature: (J[F)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_MpvMediaPlayer_mpvGetVolume
	(JNIEnv *env, jobject obj, jlong ref_media, jfloatArray jrgfVolume) {

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

	/*
	* Class:     org_datavyu_plugins_ffmpeg_MpvMediaPlayer
	* Method:    mpvSetVolume
	* Signature: (JF)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_MpvMediaPlayer_mpvSetVolume
	(JNIEnv *env, jobject obj, jlong ref_media, jfloat volume) {

		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia)
			return ERROR_MEDIA_NULL;

		CPipeline* pPipeline = (CPipeline*)pMedia->GetPipeline();
		if (NULL == pPipeline)
			return ERROR_PIPELINE_NULL;

		jint iRet = (jint)pPipeline->SetVolume((float)volume);

		return iRet;
	}

#ifdef __cplusplus
}
#endif