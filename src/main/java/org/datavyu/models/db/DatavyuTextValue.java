/*
 * Copyright (c) 2011 Datavyu Foundation, http://datavyu.org
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

import java.util.UUID;

public final class DatavyuTextValue extends DatavyuValue implements TextValue {

    public DatavyuTextValue() {
    }

    public DatavyuTextValue(UUID parent_id, Cell parent) {
        this.parent_id = parent_id;
        this.index = -1;
        this.parent = parent;
    }

    public DatavyuTextValue(UUID parent_id, Argument arg, Cell parent) {
        this(parent_id, parent);
        this.arg = arg;
        this.parent = parent;
    }

    public DatavyuTextValue(UUID parent_id, String name, int index, Argument arg, Cell parent) {
        this(parent_id, parent);
        this.name = name;
        this.index = index;
        this.arg = arg;
        this.parent = parent;
    }
}