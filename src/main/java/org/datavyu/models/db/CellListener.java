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
package org.datavyu.models.db;

/**
 * Implement this interface to be notified of changes to a cell. 
 */
public interface CellListener {

    /**
     * Called when the offset of a cell changes.
     *
     * @param newOffset The new offset value for a cell.
     */
    void offsetChanged(final long newOffset);

    /**
     * Called when the onset of a cell changes.
     *
     * @param newOnset The new onset value for a cell.
     */
    void onsetChanged(final long newOnset);

    /**
     * Called when a cell is highlighted or deselected.
     *
     * @param isHighlighted True if the cell is highlighted, false otherwise.
     */
    void highlightingChange(final boolean isHighlighted);

    /**
     * Called when a cell is selected of deselected.
     * 
     * @param isSelected True if the cell is selected, false otherwise.
     */
    void selectionChange(final boolean isSelected);

    /**
     * Called when the value for a cell changes.
     *
     * @param newValue The new value being used for a cell.
     */
    void valueChange(final Value newValue);
}
