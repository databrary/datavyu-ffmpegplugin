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
package org.datavyu.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Properties;

import org.datavyu.plugins.DataViewer;


/**
 * Utility methods for {@link DataViewer}s.
 */
public final class DataViewerUtils {

    /**
     * Store the DataViewer's default settings that need to be serialized in the
     * given OutputStream.
     *
     * @param dv
     * @param os
     * @throws IOException
     */
    public static void storeDefaults(final DataViewer dv, final OutputStream os)
        throws IOException {

        if ((dv == null) || (os == null)) {
            throw new NullPointerException();
        }

        Properties props = new Properties();
        props.setProperty("offset", Long.toString(dv.getOffset()));
        props.store(os, null);
    }

    /**
     * Load and restore the DataViewer's default serialized settings from the
     * given InputStream. Assumes that the offset was stored using the
     * {@link #storeDefaults(DataViewer, OutputStream)} method. This method's
     * behaviour is undefined otherwise.
     *
     * @param dv
     * @param is
     * @throws IOException
     */
    public static void loadDefaults(final DataViewer dv, final InputStream is)
        throws IOException {

        if ((dv == null) || (is == null)) {
            throw new NullPointerException();
        }

        Properties props = new Properties();
        props.load(is);

        String property = props.getProperty("offset");

        if ((property != null) && !"".equals(property)) {
            dv.setOffset(Long.parseLong(property));
        }
    }

}
