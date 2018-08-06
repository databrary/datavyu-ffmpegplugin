#include "PipelineFactory.h"
#include "FfmpegPipelineFactory.h"

PipelineFactory::PFSingleton PipelineFactory::s_Singleton;

uint32_t PipelineFactory::GetInstance(PipelineFactory ** ppPipelineFactory) {
	return s_Singleton.GetInstance(ppPipelineFactory);
}

PipelineFactory::~PipelineFactory()
{}

PipelineFactory::PipelineFactory()
{}

uint32_t PipelineFactory::CreateInstance(PipelineFactory ** ppPipelineFactory) {
	*ppPipelineFactory = new(nothrow) FfmpegPipelineFactory();
	return NULL == *ppPipelineFactory ? ERROR_MEMORY_ALLOCATION : ERROR_NONE;
}
