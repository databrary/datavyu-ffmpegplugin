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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The arguments held within the matrix.
 */
public final class Argument implements Serializable {

    public enum Type {
        MATRIX,
        TEXT,
        NOMINAL
    }

    ;

    // The name of argument.
    public String name;

    // Type type of argument.
    public Type type;

    // The child arguments - this is empty if no child arguments.
    public List<Argument> childArguments;

    // The ID of this variable
    public long id;

    private int lastCodeNumber = 0;

    /**
     * Constructor.
     *
     * @param newName  The new name to use for the argument.
     * @param newType  The new type to use for the argument.
     * @param newValue The new value to use for the argument.
     */
    public Argument(final String newName,
                    final Type newType) {

        id = this.hashCode();
        name = newName;
        type = newType;
        childArguments = new ArrayList<Argument>();

        // Matrix arguments default with a single child nominal.
        if (type == Type.MATRIX) {
            addChildArgument(Type.NOMINAL);
        }
    }

    public Argument(final String newName,
                    final Type newType,
                    final long id) {
        this(newName, newType);
        this.id = id;
    }

    public Argument addChildArgument(final Type newType) {
        Argument child = new Argument(String.format("code%02d", lastCodeNumber + 1), newType);
        lastCodeNumber++;
        childArguments.add(child);
        return child;
    }

    public void clearChildArguments() {
        childArguments.clear();
    }

    public boolean equals(Argument other) {
        if (other.id == this.id) {
            return true;
        } else {
            return false;
        }
    }
}
