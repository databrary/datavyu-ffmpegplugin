#ifndef PIPELINEFACTORY_H_
#define PIPELINEFACTORY_H_

#include "Pipeline.h"
#include "PipelineOptions.h"
#include "Singleton.h"
#include <stdint.h>

class PipelineFactory {
public:
	static uint32_t GetInstance(PipelineFactory **ppPipelineFactory);

	virtual ~PipelineFactory();

	virtual uint32_t CreatePlayerPipeline(CPipelineOptions *pOptions, CPipeline** ppPipeline) = 0;

protected:
	PipelineFactory();

private:
	typedef Singleton<PipelineFactory> PFSingleton;
	friend class Singleton<PipelineFactory>;

	static uint32_t CreateInstance(PipelineFactory **ppPipelineFactory);
	static PFSingleton       s_Singleton;
};
#endif
