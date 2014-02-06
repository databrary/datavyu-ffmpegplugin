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
package org.datavyu.models.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class MixerComponentModelImpl implements PropertyChangeListener, MixerComponentModel {
    private final PropertyChangeSupport change;
    protected final MixerModel mixerModel;

    /**
     * Constructor.
     * <p/>
     * NOTE: You should <b>NOT</b> wire up model listeners in super class constructors, otherwise it may create
     * circular dependencies between model objects' constructors.
     *
     * @param mixerModel reference to the mixer model, otherwise null if this object is the mixer model
     */
    public MixerComponentModelImpl(final MixerModel mixerModel) {
        change = new PropertyChangeSupport(this);
        if (mixerModel != null) {
            this.mixerModel = mixerModel;
        } else {
            assert this instanceof MixerModel;
            this.mixerModel = (MixerModel) this;
        }
    }

    /**
     * Called after the model components have been constructed. Super classes should override this method to
     * wire up any required property change listeners from the model.
     */
    public void wireListeners() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MixerModel getMixerModel() {
        return mixerModel;
    }

    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
        change.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        change.addPropertyChangeListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        change.removePropertyChangeListener(listener);
    }

    /**
     * Called when a model property is changed. The {@link PropertyChangeEvent#getSource()} property will be
     * set to the model where the change notification originated.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }
}
