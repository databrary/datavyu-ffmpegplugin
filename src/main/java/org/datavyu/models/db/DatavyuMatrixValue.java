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

import java.util.*;


public final class DatavyuMatrixValue extends DatavyuValue implements MatrixValue {

    private UUID parentId;
    private String value;
    private List<Value> values;


    public DatavyuMatrixValue() {
    }

    public DatavyuMatrixValue(UUID parent_id, Argument type, Cell parent) {
        this.parentId = parent_id;
        this.parent = parent;
        values = new ArrayList<Value>();
        for (Argument arg : type.childArguments) {
            createArgument(arg);
        }
        this.arg = type;
        value = "MATRIX";
    }

    // Method to order the values coming out of the DB.
    private static void order(List<Value> values) {

        Collections.sort(values, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {

                int x1 = ((DatavyuValue) o1).getIndex();
                int x2 = ((DatavyuValue) o2).getIndex();

                if (x1 != x2) {
                    return x1 - x2;
                } else {
                    return 0;
                }
            }
        });
    }
    
    @Override
    public String toString() {
        String result = "";
        List<Value> values = getArguments();

        result += "(";
        for (int i = 0; i < values.size(); i++) {
            Value v = values.get(i);
            if (v.toString() == null) {
                result += "<code" + String.valueOf(i) + ">";
            } else {
                result += v.toString();
            }
            if (i < values.size() - 1) {
                result += ",";
            }
        }
        result += ")";

        return result;
    }

    public String serialize() {
        List<Value> values = getArguments();

        StringBuilder result = new StringBuilder("(");
        for (Iterator<Value> i = values.iterator(); i.hasNext(); ) {
            Value v = i.next();
            result.append(v.serialize());
            if (i.hasNext())
                result.append(',');
        }
        result.append(')');

        return result.toString();
    }

    @Override
    public List<Value> getArguments() {
        order(values);
        return values;
    }

    @Override
    public Value createArgument(Argument arg) {
        Value val = null;
        String name = String.format("code%02d", getArguments().size() + 1);
        if (arg.type == Argument.Type.NOMINAL) {
            val = new DatavyuNominalValue(this.id, name, getArguments().size(), arg, parent);
        } else if (arg.type == Argument.Type.TEXT) {
            val = new DatavyuTextValue(this.id, name, getArguments().size(), arg, parent);
        }
        this.getArguments().add(val);
        return val;
    }

    @Override
    public void removeArgument(final int index) {
        List<Value> args = getArguments();
        args.remove(index);
        Value v;
        for (int i = 0; i < args.size(); i++) {
            v = args.get(i);
            ((DatavyuNominalValue) v).setIndex(i);
        }
    }

    @Override
    public void set(String value) {
        if (value.startsWith("(") && value.endsWith(")")) {
            value = value.substring(1, value.length() - 1);
        }
        String[] args = value.split(",");
        List<Value> values = getArguments();
        if(args.length != values.size()) {
            System.err.println("Error: Arg list and value list are different sizes, cannot undo.");
        }
        for(int i = 0; i < args.length; i++) {
            values.get(i).set(args[i]);
        }
    }
}
