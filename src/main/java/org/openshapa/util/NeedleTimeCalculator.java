/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openshapa.util;

import org.openshapa.models.component.ViewableModel;

/**
 *
 *
 */
public final class NeedleTimeCalculator {

    /** */
    private NeedleTimeCalculator() {

    }

    /**
     * Calculates the time that a needle position corresponds to.
     * @param xIn The x position of the needle.
     * @param maxX The maximum position the needle can be at.
     * @param vm The ViewableModel the needle belongs to.
     * @param paddingLeft The left padding of the needle or timescale region.
     * @return The timestamp (rounded to an int) that the needle is pointing to.
     */
    public static int getNeedleTime(final int xIn, final int maxX,
            final ViewableModel vm, final int paddingLeft) {

        // Bound the x value (0 - maxX)
        int x = Math.min(Math.max(0, xIn), maxX);

        // Calculate the time represented by the new location
        float ratio =
                vm.getIntervalWidth()
                        / vm.getIntervalTime();
        float newTime =
                (x - paddingLeft + (vm.getZoomWindowStart()) * ratio) / ratio;
        if (newTime < 0) {
            newTime = 0;
        }
        if (newTime > vm.getZoomWindowEnd()) {
            newTime = vm.getZoomWindowEnd();
        }

        return Math.round(newTime);

    }

}
