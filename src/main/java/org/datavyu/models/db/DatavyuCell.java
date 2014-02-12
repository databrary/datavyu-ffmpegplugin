/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.datavyu.models.db;

import java.util.*;


public class DatavyuCell implements Cell {

    static Map<UUID, List<CellListener>> allListeners = new HashMap<UUID, List<CellListener>>();
    private long onset = 0L;
    private long offset = 0L;
    private Argument type;
    private boolean selected;
    private boolean highlighted;
    private Variable parent;
    private Map<String, Value> arguments = new HashMap<String, Value>();
    private Value value;
    final private UUID id = UUID.randomUUID();

    /**
     * @param cellId The ID of the variable we want the listeners for.
     * @return The list of listeners for the specified cellId.
     */
    private static List<CellListener> getListeners(UUID cellId) {
        List<CellListener> result = allListeners.get(cellId);

        if (result == null) {
            result = new ArrayList<CellListener>();
            allListeners.put(cellId, result);
        }

        return result;
    }

    public DatavyuCell() {
    }

    public DatavyuCell(Variable parent, Argument type) {
        this.parent = parent;
        this.type = type;

        this.onset = 0L;
        this.offset = 0L;
        this.selected = true;
        this.highlighted = true;

        // Build argument list from the argument given

        if (type.type == Argument.Type.NOMINAL) {
            this.value = new DatavyuNominalValue(getID(), type);
        } else if (type.type == Argument.Type.TEXT) {
            this.value = new DatavyuTextValue(getID(), type);
        } else {
            this.value = new DatavyuMatrixValue(getID(), type);
        }
    }

    public Variable getVariable() {
        return parent;
    }

    private String convertMStoTimestamp(long time) {
        long hours = Math.round(Math.floor((time / 1000.0 / 60.0 / 60.0)));
        long minutes = Math.round(Math.floor(time / 1000.0 / 60.0 - (hours * 60)));
        long seconds = Math.round(Math.floor(time / 1000.0 - (hours * 60 * 60) - (minutes * 60)));
        long mseconds = Math.round(Math.floor(time - (hours * 60 * 60 * 1000) - (minutes * 60 * 1000) - (seconds * 1000)));

        return String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, mseconds);
    }

    private long convertTimestampToMS(String timestamp) {

        String[] s = timestamp.split(":");
        long hours = Long.valueOf(s[0]) * 60 * 60 * 1000;
        long minutes = Long.valueOf(s[1]) * 60 * 1000;
        long seconds = Long.valueOf(s[2]) * 1000;
        long mseconds = Long.valueOf(s[3]);

        return hours + minutes + seconds + mseconds;
    }


    @Override
    public String getOffsetString() {
        return convertMStoTimestamp(offset);
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Cell getFreshCell() {
        return this;
    }

    @Override
    public void setOffset(final long newOffset) {
        offset = newOffset;
        for (CellListener cl : getListeners(getID())) {
            cl.offsetChanged(offset);
        }
    }

    @Override
    public void setOffset(final String newOffset) {
        offset = convertTimestampToMS(newOffset);
        for (CellListener cl : getListeners(getID())) {
            cl.offsetChanged(offset);
        }
    }

    @Override
    public long getOnset() {
        return onset;
    }

    @Override
    public String getOnsetString() {
        return convertMStoTimestamp(onset);

    }

    @Override
    public void setOnset(final String newOnset) {
        onset = convertTimestampToMS(newOnset);
        for (CellListener cl : getListeners(getID())) {
            cl.onsetChanged(onset);
        }
    }

    @Override
    public void setOnset(final long newOnset) {
        onset = newOnset;
        for (CellListener cl : getListeners(getID())) {
            cl.onsetChanged(onset);
        }
    }

    @Override
    public String getValueAsString() {
        return getValue().toString();
    }

    @Override
    public Value getValue() {
        return this.value;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(final boolean selected) {
        this.selected = selected;
        if (!selected) {
            setHighlighted(false);
        }

        for (CellListener cl : getListeners(getID())) {
            cl.selectionChange(selected);
            if (!selected) {
                cl.highlightingChange(false);
            }
        }
    }

    @Override
    public boolean isHighlighted() {
        return highlighted;
    }

    @Override
    public void setHighlighted(final boolean highlighted) {
        this.highlighted = highlighted;

        if (highlighted) {
            setSelected(highlighted);
        }

        for (CellListener cl : getListeners(getID())) {
            cl.highlightingChange(highlighted);
        }
    }

    @Override
    public void addMatrixValue(Argument type) {
        DatavyuMatrixValue val = (DatavyuMatrixValue) getValue();
        val.createArgument(type);
    }

    @Override
    public void moveMatrixValue(final int old_index, int new_index) {
        DatavyuMatrixValue val = (DatavyuMatrixValue) getValue();
        List<Value> values = val.getArguments();
        Value v = values.get(old_index);

        values.remove(old_index);
        values.add(new_index, v);

        for (int i = 0; i < values.size(); i++) {
            ((DatavyuValue) values.get(i)).setIndex(i);
        }
    }

    @Override
    public void removeMatrixValue(final int index) {
        ((DatavyuMatrixValue) getValue()).removeArgument(index);
    }

    @Override
    public void setMatrixValue(final int index, final String v) {
        DatavyuMatrixValue val = (DatavyuMatrixValue) getValue();
        List<Value> values = val.getArguments();
        values.get(index).set(v);
    }

    @Override
    public Value getMatrixValue(final int index) {
        return ((DatavyuMatrixValue) getValue()).getArguments().get(index);
    }

    @Override
    public void clearMatrixValue(final int index) {
        DatavyuMatrixValue val = (DatavyuMatrixValue) getValue();
        List<Value> values = val.getArguments();
        values.get(index).clear();
    }

    @Override
    public void addListener(final CellListener listener) {
        getListeners(getID()).add(listener);
    }

    @Override
    public void removeListener(final CellListener listener) {
        getListeners(getID()).remove(listener);
    }

    public UUID getID() {
        return id;
    }

    @Override
    public String getCellID() {
        return this.getID().toString();
    }

    @Override
    public int hashCode() {
        return this.getID().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DatavyuCell)) {
            return false;
        }
        DatavyuCell otherC = (DatavyuCell) other;

        if (otherC.getID().toString().equals(this.getID().toString())) {
            return true;
        } else {
            return false;
        }
    }
}

