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
package org.datavyu.views;

import org.datavyu.plugins.DataViewer;

/**
 * Creators of plugins DO NOT NEED TO IMPLEMENT this class - the method
 * 'setParentController' of DataViewer is used to notify each viewer of its
 * parent controller. When an implemeting viewer is closed, it should notify
 * the parent controller that it no longer requires commands by calling
 * shutdown and supplying itself as the argument. i.e.
 *
 * parentController.shutdown(this);
 *
 * It would be nice if we could use a weak reference instead (see bug:755)
 */
public interface DataController {

    /**
     * Remove the specifed viewer form the controller.
     *
     * @param viewer The viewer to shutdown.
     * @return True if the controller contained this viewer.
     */
    boolean shutdown(final DataViewer viewer);
}
