package org.openshapa.models.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class MixerComponentModelImpl implements PropertyChangeListener, MixerComponentModel {
	private final PropertyChangeSupport change;
    protected final MixerModel mixerModel;

    /**
     * Constructor.
     * <p>
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
    @Override public MixerModel getMixerModel() {
    	return mixerModel;
    }

    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
    	change.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void addPropertyChangeListener(final PropertyChangeListener listener) {
        change.addPropertyChangeListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override public void removePropertyChangeListener(final PropertyChangeListener listener) {
        change.removePropertyChangeListener(listener);
    }

    /**
     * Called when a model property is changed. The {@link PropertyChangeEvent#getSource()} property will be
     * set to the model where the change notification originated.
     */
    @Override public void propertyChange(PropertyChangeEvent evt) {
    }
}
