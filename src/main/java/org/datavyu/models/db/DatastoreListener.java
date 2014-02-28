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
 * Implement this interface to be notified of changes to the datastore.
 */
public interface DatastoreListener {
    /**
     * Called when a variable is added to the datastore.
     *
     * @param newVariable The variable that was just added to the datastore.
     */
    void variableAdded(final Variable newVariable);

    /**
     * Called when a variable is removed from the datastore.
     *
     * @param deletedVariable The variable that was removed from the datastore.
     */
    void variableRemoved(final Variable deletedVariable);

    /**
     * Called when the order of variables in the datastore change.
     */
    void variableOrderChanged();
    
    /**
     * Called when a variable is hidden.
     *
     * @param hiddenVariable The variable that was hidden.
     */
    void variableHidden(final Variable hiddenVariable);

    /**
     * Called when a variable is made visible (opposite of hidden).
     *
     * @param visibleVariable  The variable that was made visible.
     */
    void variableVisible(final Variable visibleVariable);

    /**
     * Called when the name of a variable has changed.
     *
     * @param editedVariable The variable that has had its name changed.
     */
    void variableNameChange(final Variable editedVariable);
}
