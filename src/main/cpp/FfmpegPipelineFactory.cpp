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
		//TODO(Reda): rename FfmpegAVPlaybackPipeline suggestion: FfmpegSDLAVPipeline
		*ppPipeline = new (nothrow) FfmpegAVPlaybackPipeline(pOptions);
	else
		//TODO(Reda) call FfmpegJAVAAVPipeline 
		return ERROR_PIPELINE_NULL;

	if (NULL == ppPipeline)
		return ERROR_PIPELINE_NULL;

	return ERROR_NONE;
}
