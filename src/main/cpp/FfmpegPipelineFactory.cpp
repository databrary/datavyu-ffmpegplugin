#include "FfmpegPipelineFactory.h"

FfmpegPipelineFactory::FfmpegPipelineFactory()
{}

FfmpegPipelineFactory::~FfmpegPipelineFactory()
{}

uint32_t FfmpegPipelineFactory::CreatePlayerPipeline(CPipelineOptions *pOptions, CPipeline **ppPipeline) {
	
	*ppPipeline = NULL;
	if (pOptions->GetStreamData())
		*ppPipeline = new (nothrow) FfmpegJavaAvPlaybackPipline(pOptions);
	else
		*ppPipeline = new (nothrow) FfmpegSdlAvPlaybackPipeline(pOptions);

	if (NULL == ppPipeline)
		return ERROR_PIPELINE_NULL;

	return ERROR_NONE;
}
