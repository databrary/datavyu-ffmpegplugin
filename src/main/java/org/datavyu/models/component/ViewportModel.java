/*
 * Copyright (c) 2011 OpenSHAPA Foundation, http://openshapa.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.datavyu.models.component;

public interface ViewportModel extends MixerComponentModel {
    /**
     * @return A snapshot of the viewport's current state.
     */
    ViewportState getViewport();

    /**
     * @param maxEnd
     * @param resetViewportWindow 
     */
    void setViewportMaxEnd(long maxEnd, boolean resetViewportWindow);

    /**
     *
     * @param start
     * @param end
     */
    void setViewportWindow(long start, long end);

    /**
     * Resizes the viewport by adding or removing pixels while maintaining the
     * viewport's existing resolution.
     *
     * @param start Time in milliseconds.
     * @param width
     */
    void resizeViewport(long start, double width);

    /**
     *
     * @param start
     * @param end
     * @param maxEnd
     * @param width
     */
    void setViewport(long start, long end, long maxEnd, double width);

    /**
     *
     * @param zoomLevel Between 0 and 1.0 inclusive.
     * @param centerTime Time to center around, or -1 if centering is not
     * required.
     */
    void setViewportZoom(double zoomLevel, long centerTime);
    
    void resetViewport();
}
