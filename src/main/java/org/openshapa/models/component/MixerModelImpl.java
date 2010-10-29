package org.openshapa.models.component;

import java.beans.PropertyChangeListener;

public final class MixerModelImpl extends MixerComponentModelImpl implements MixerModel {
    private final RegionModel regionModel;
    private final NeedleModel needleModel;
    private final ViewportModel viewportModel;
    
    public MixerModelImpl() {
    	super(null);
    	viewportModel = new ViewportModelImpl(this);
    	regionModel = new RegionModelImpl(this);
    	needleModel = new NeedleModelImpl(this);
    	
    	wireListeners();
    	((MixerComponentModelImpl) viewportModel).wireListeners();
    	((MixerComponentModelImpl) regionModel).wireListeners();
    	((MixerComponentModelImpl) needleModel).wireListeners();
    }

    /**
     * {@inheritDoc}
     */
    @Override public RegionModel getRegionModel() {
    	return regionModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override public NeedleModel getNeedleModel() {
    	return needleModel;
    }

    /**
     * {@inheritDoc}
     */
	@Override public ViewportModel getViewportModel() {
		return viewportModel;
	}
	
    @Override public void addPropertyChangeListener(final PropertyChangeListener listener) {
    	assert false;
    }
}
