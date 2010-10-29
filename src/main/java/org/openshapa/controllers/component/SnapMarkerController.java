package org.openshapa.controllers.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import org.openshapa.models.component.MixerModel;
import org.openshapa.models.component.SnapMarkerModel;
import org.openshapa.models.component.ViewportState;

import org.openshapa.views.component.SnapMarkerPainter;


/**
 * SnapMarkerController is responsible for managing a SnapMarkerPainter
 */
public final class SnapMarkerController implements PropertyChangeListener {

    /** View */
    private final SnapMarkerPainter view;

    /** Models */
    private final SnapMarkerModel snapMarkerModel;
    private final MixerModel mixer;

    public SnapMarkerController(final MixerModel mixer) {
        view = new SnapMarkerPainter();

        snapMarkerModel = new SnapMarkerModel();
        snapMarkerModel.setMarkerTime(-1);

        this.mixer = mixer;

        view.setMixerView(mixer);
        view.setSnapMarkerModel(snapMarkerModel);

        mixer.getViewportModel().addPropertyChangeListener(this);
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

        if (ViewportState.NAME.equals(evt.getPropertyName())) {
            view.repaint();
        }
    }

}
