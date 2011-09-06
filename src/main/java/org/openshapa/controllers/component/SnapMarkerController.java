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
package org.openshapa.controllers.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import org.openshapa.models.component.MixerModel;
import org.openshapa.models.component.SnapMarkerModel;

import org.openshapa.views.component.SnapMarkerPainter;


/**
 * SnapMarkerController is responsible for managing a SnapMarkerPainter
 */
public final class SnapMarkerController implements PropertyChangeListener {

    /** View */
    private final SnapMarkerPainter view;

    /** Models */
    private final SnapMarkerModel snapMarkerModel;
    private final MixerModel mixerModel;

    public SnapMarkerController(final MixerModel mixerModel) {
        view = new SnapMarkerPainter();

        snapMarkerModel = new SnapMarkerModel();
        snapMarkerModel.setMarkerTime(-1);

        this.mixerModel = mixerModel;

        view.setMixerView(mixerModel);
        view.setSnapMarkerModel(snapMarkerModel);

        mixerModel.getViewportModel().addPropertyChangeListener(this);
    }

    /**
     * Set the current time to be represented by the needle.
     *
     * @param currentTime
     */
    public void setMarkerTime(final long currentTime) {
        snapMarkerModel.setMarkerTime(currentTime);
        view.setSnapMarkerModel(snapMarkerModel);
    }

    /**
     * @return Time represented by the marker
     */
    public long getMarkerTime() {
        return snapMarkerModel.getMarkerTime();
    }

    /**
     * @return View used by the controller
     */
    public JComponent getView() {
        return view;
    }

    @Override public void propertyChange(final PropertyChangeEvent evt) {
        if (evt.getSource() == mixerModel.getViewportModel()) {
            view.repaint();
        }
    }

}
