#include "FfmpegPipelineFactory.h"

FfmpegPipelineFactory::FfmpegPipelineFactory()
{}

FfmpegPipelineFactory::~FfmpegPipelineFactory()
{}

uint32_t FfmpegPipelineFactory::CreatePlayerPipeline(CPipelineOptions *pOptions, CPipeline **ppPipeline) {

	if (NULL == pOptions->GetStreamData())
		return ERROR_BASE_PIPELINE;
	
	*ppPipeline = NULL;
	if (pOptions->GetStreamData())
		*ppPipeline = new (nothrow) FfmpegSdlAvPlaybackPipeline(pOptions);
	else
		//TODO(Reda) create instance of FfmpegJavaAvPlaybackPipline here
		return ERROR_PIPELINE_NULL;

	if (NULL == ppPipeline)
		return ERROR_PIPELINE_NULL;

	return ERROR_NONE;
}
