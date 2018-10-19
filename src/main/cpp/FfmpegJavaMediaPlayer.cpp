#include "org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer.h"

#include "JniUtils.h"
#include "JavaPlayerEventDispatcher.h"
#include "Media.h"
#include "Pipeline.h"
#include "MediaPlayerErrors.h"
#include "FfmpegJniUtils.h"
#include "FfmpegJavaAvPlaybackPipline.h"

using namespace std;

#ifdef __cplusplus
extern "C" {
#endif

	/*
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegInitPlayer
	* Signature: ([JLjava/lang/String;Ljavax/sound/sampled/AudioFormat;Ljava/awt/color/ColorSpace;I)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegInitPlayer
	(JNIEnv *env, jobject obj, jlongArray jlMediaHandle, jstring sourcePath,
		jobject jAudioFormat, jobject jColorSpace, jint jAudioBufferSizeInBy) {

		uint32_t uRetCode;
		AudioFormat audioFormat;
		uRetCode = GetAudioFormat(env, jAudioFormat, &audioFormat);
		if (ERROR_NONE != uRetCode) {
			return uRetCode;
		}

		PixelFormat pixelFormat;
		uRetCode = GetPixelFormat(env, jColorSpace, &pixelFormat);
		if (ERROR_NONE != uRetCode) {
			return uRetCode;
		}

		CPipelineOptions* pOptions = new (nothrow) CPipelineOptions(
			audioFormat, pixelFormat, jAudioBufferSizeInBy);
		if (NULL == pOptions) {
			return ERROR_SYSTEM_ENOMEM;
		}

		CPipelineData* pPipelineData = new (nothrow) FfmpegJavaAvPlaybackPipline(pOptions);

		if (NULL == pPipelineData)
			return ERROR_PIPELINE_NULL;

		CMedia* pMedia = NULL;
		CMedia** ppMedia = &pMedia;

		*ppMedia = new(nothrow) CMedia(pPipelineData);

		if (NULL == *ppMedia) {
			delete pOptions;
			delete pPipelineData;
			return ERROR_MEDIA_CREATION;
		}

		if (CMedia::IsValid(pMedia)) {
			jlong lMediaHandle = (jlong)ptr_to_jlong(pMedia);
			env->SetLongArrayRegion(jlMediaHandle, 0, 1, &lMediaHandle);
		}
		else {
			delete pOptions;
			delete pPipelineData;
			return ERROR_MEDIA_INVALID;
		}

		CJavaPlayerEventDispatcher* pEventDispatcher = new(nothrow) CJavaPlayerEventDispatcher();
		if (NULL == pEventDispatcher)
			return ERROR_SYSTEM_ENOMEM;

		pEventDispatcher->Init(env, obj, pMedia);
		pPipelineData->SetEventDispatcher(pEventDispatcher);

		const char* filename = env->GetStringUTFChars(sourcePath, 0);

		jint iRet = (jint)pPipelineData->Init(filename);

		env->ReleaseStringUTFChars(sourcePath, filename);

		return iRet;
	}

	/*
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegDisposePlayer
	* Signature: (J)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegDisposePlayer
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
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegGetAudioSyncDelay
	* Signature: (J[J)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegGetAudioSyncDelay
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
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegSetAudioSyncDelay
	* Signature: (JJ)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegSetAudioSyncDelay
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
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegPlay
	* Signature: (J)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegPlay
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
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegPause
	* Signature: (J)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegPause
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
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegStop
	* Signature: (J)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegStop
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
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegStepForward
	* Signature: (J)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegStepForward
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
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegFinish
	* Signature: (J)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegFinish
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
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegGetRate
	* Signature: (J[F)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegGetRate
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
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegSetRate
	* Signature: (JF)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegSetRate
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
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegGetPresentationTime
	* Signature: (J[D)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegGetPresentationTime
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
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegGetFps
	* Signature: (J[D)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegGetFps
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
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegGetDuration
	* Signature: (J[D)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegGetDuration
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
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegSeek
	* Signature: (JD)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegSeek
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
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegHasAudioData
	* Signature: (J[Z)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegHasAudioData
	(JNIEnv *env, jobject obj, jlong ref_media, jbooleanArray jrbAudioData) {
		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia)
			return ERROR_MEDIA_NULL;

		CPipelineData* pPipeline = (CPipelineData*)pMedia->GetPipeline();
		if (NULL == pPipeline)
			return ERROR_PIPELINE_NULL;

		bool bAudioData;
		uint32_t uErrCode = pPipeline->HasAudioData(&bAudioData);
		if (ERROR_NONE != uErrCode)
			return uErrCode;
		jboolean jbAudioData = (jboolean)bAudioData;
		env->SetBooleanArrayRegion(jrbAudioData, 0, 1, &jbAudioData);

		return ERROR_NONE;
	}

	/*
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegHasImageData
	* Signature: (J[Z)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegHasImageData
	(JNIEnv *env, jobject obj, jlong ref_media, jbooleanArray jrbImageData) {
		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia)
			return ERROR_MEDIA_NULL;

		CPipelineData* pPipeline = (CPipelineData*)pMedia->GetPipeline();
		if (NULL == pPipeline)
			return ERROR_PIPELINE_NULL;

		bool bImageData;
		uint32_t uErrCode = pPipeline->HasImageData(&bImageData);
		if (ERROR_NONE != uErrCode)
			return uErrCode;
		jboolean jbImageData = (jboolean)bImageData;
		env->SetBooleanArrayRegion(jrbImageData, 0, 1, &jbImageData);

		return ERROR_NONE;
	}

	/*
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegGetImageWidth
	* Signature: (J[I)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegGetImageWidth
	(JNIEnv *env, jobject obj, jlong ref_media, jintArray jriImageWidth) {
		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia)
			return ERROR_MEDIA_NULL;

		CPipelineData* pPipeline = (CPipelineData*)pMedia->GetPipeline();
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
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegGetImageHeight
	* Signature: (J[I)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegGetImageHeight
	(JNIEnv *env, jobject obj, jlong ref_media, jintArray jriImageHeight) {
		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia)
			return ERROR_MEDIA_NULL;

		CPipelineData* pPipeline = (CPipelineData*)pMedia->GetPipeline();
		if (NULL == pPipeline)
			return ERROR_PIPELINE_NULL;

		int iImageHeight;
		uint32_t uErrCode = pPipeline->GetImageHeight(&iImageHeight);
		if (ERROR_NONE != uErrCode)
			return uErrCode;
		jint jiImageHeight = (jint)iImageHeight;
		env->SetIntArrayRegion(jriImageHeight, 0, 1, &jiImageHeight);

		return ERROR_NONE;
	}

	/*
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegGetAudioFormat
	* Signature: (J[Ljavax/sound/sampled/AudioFormat;)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegGetAudioFormat
	(JNIEnv *env, jobject obj, jlong ref_media, jobjectArray jrAudioFormat) {
		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia)
			return ERROR_MEDIA_NULL;

		CPipelineData* pPipeline = (CPipelineData*)pMedia->GetPipeline();
		if (NULL == pPipeline)
			return ERROR_PIPELINE_NULL;

		AudioFormat audioParams;
		uint32_t uErrCode = pPipeline->GetAudioFormat(&audioParams);
		if (ERROR_NONE != uErrCode)
			return uErrCode;

		jobject jAudioFormat = env->GetObjectArrayElement(jrAudioFormat, 0);
		if (jAudioFormat == nullptr) {
			return ERROR_FFMPEG_AUDIO_FORMAT_NULL;
		}

		uErrCode = SetJAudioFormat(env, jAudioFormat, audioParams);
		if (ERROR_NONE != uErrCode)
			return uErrCode;

		return ERROR_NONE;
	}

	/*
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegGetColorSpace
	* Signature: (J[Ljava/awt/color/ColorSpace;)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegGetColorSpace
	(JNIEnv *env, jobject obj, jlong ref_media, jobjectArray jrColorSpace) {
		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia)
			return ERROR_MEDIA_NULL;

		CPipelineData* pPipeline = (CPipelineData*)pMedia->GetPipeline();
		if (NULL == pPipeline)
			return ERROR_PIPELINE_NULL;

		PixelFormat pixelFormat;
		uint32_t uErrCode = pPipeline->GetPixelFormat(&pixelFormat);
		if (ERROR_NONE != uErrCode)
			return uErrCode;

		jobject jColorSpace = env->GetObjectArrayElement(jrColorSpace, 0);
		if (jColorSpace == nullptr) {
			return ERROR_FFMPEG_COLOR_SPACE_NULL;
		}

		uErrCode = SetJPixelFormat(env, jColorSpace, pixelFormat);
		if (ERROR_NONE != uErrCode)
			return uErrCode;

		return ERROR_NONE;
	}

	/*
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegUpdateImageData
	* Signature: (J[B)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegUpdateImageData
	(JNIEnv *env, jobject obj, jlong ref_media, jbyteArray data) {
		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia) {
			return ERROR_MEDIA_NULL;
		}
		CPipelineData* pPipeline = (CPipelineData*)pMedia->GetPipeline();
		if (NULL == pPipeline) {
			return ERROR_PIPELINE_NULL;
		}

		jbyte* pData = env->GetByteArrayElements(data, 0);
		jlong len = env->GetArrayLength(data);
		uint32_t uErrCode = pPipeline->UpdateImageBuffer((uint8_t*)pData, len);
		env->ReleaseByteArrayElements(data, pData, 0);
		return uErrCode;
	}

	/*
	* Class:     org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer
	* Method:    ffmpegUpdateAudioData
	* Signature: (J[B)I
	*/
	JNIEXPORT jint JNICALL Java_org_datavyu_plugins_ffmpeg_FfmpegJavaMediaPlayer_ffmpegUpdateAudioData
	(JNIEnv *env, jobject obj, jlong ref_media, jbyteArray data) {
		CMedia* pMedia = (CMedia*)jlong_to_ptr(ref_media);
		if (NULL == pMedia) {
			return ERROR_MEDIA_NULL;
		}
		CPipelineData* pPipeline = (CPipelineData*)pMedia->GetPipeline();
		if (NULL == pPipeline) {
			return ERROR_PIPELINE_NULL;
		}
		// TODO(fraudies): Check if we need to make an extra copy (it may be that the audio data is still read while this is writing)
		jbyte* pData = env->GetByteArrayElements(data, 0);
		jlong len = env->GetArrayLength(data);
		uint32_t uErrCode = pPipeline->UpdateAudioBuffer((uint8_t*)pData, len);
		env->ReleaseByteArrayElements(data, pData, 0);
		return uErrCode;
	}

#ifdef __cplusplus
}
#endif