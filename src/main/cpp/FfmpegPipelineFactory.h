#ifndef FFMPEGPIPELINEFACTORY_H_
#define FFMPEGPIPELINEFACTORY_H_

#include "PipelineFactory.h"
#include "PipelineOptions.h"
#include "FfmpegSdlAvPlaybackPipeline.h"
#include "FfmpegJavaAvPlaybackPipline.h"
#include "FfmpegMediaErrors.h"

class FfmpegPipelineFactory : public PipelineFactory {
public:
	FfmpegPipelineFactory();
	virtual ~FfmpegPipelineFactory();

	uint32_t CreatePlayerPipeline(CPipelineOptions *pOptions, CPipeline** ppPipeline);

private:
};
#endif // FFMPEGPIPELINEFACTORY_H_


