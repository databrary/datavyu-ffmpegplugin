package org.openshapa.plugins.spectrum.engine;

import org.openshapa.plugins.spectrum.models.AmplitudeBlock;
import org.openshapa.plugins.spectrum.models.StereoData;


/**
 * Interface for notifying about background processing progress.
 */
public interface Progress {

    /**
     * The given block has finished processing. Invoked on the EDT.
     *
     * @param block
     *            Block to process.
     */
    void blockDone(AmplitudeBlock block);

    /**
     * The overall progress so far. Invoked on the EDT.
     *
     * @param percentage
     *            [0,1]. 0 = just started, 1 = done.
     */
    void overallProgress(double percentage);

    /**
     * The entire file has been processed. Invoked on the EDT.
     *
     * @param allData
     *            All of the processed data.
     */
    void allDone(StereoData allData);

}
