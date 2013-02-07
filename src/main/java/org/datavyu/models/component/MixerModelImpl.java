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

import java.beans.PropertyChangeListener;
import org.datavyu.models.component.MixerModel;
import org.datavyu.models.component.NeedleModel;
import org.datavyu.models.component.RegionModel;
import org.datavyu.models.component.ViewportModel;

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
