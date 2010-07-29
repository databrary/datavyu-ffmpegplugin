package org.openshapa.plugins.spectrum.swing;

import java.awt.geom.Path2D;

import org.openshapa.plugins.spectrum.models.StereoAmplitudeData;


/**
 * Interface for providing amplitude data.
 */
public interface Amplitude {

    /**
     * Provide amplitude data for use.
     *
     * @param data
     *            Data to use.
     */
    void setData(StereoAmplitudeData data);

}
