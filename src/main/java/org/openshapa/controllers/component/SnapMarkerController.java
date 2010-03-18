package org.openshapa.controllers.component;

import javax.swing.JComponent;

import org.openshapa.models.component.SnapMarkerModel;
import org.openshapa.models.component.ViewableModel;
import org.openshapa.views.component.SnapMarkerPainter;

/**
 * SnapMarkerController is responsible for managing a SnapMarkerPainter
 */
public class SnapMarkerController {
    /** View */
    private transient final SnapMarkerPainter view;
    /** Models */
    private transient final SnapMarkerModel snapMarkerModel;
    private transient final ViewableModel viewableModel;

    public SnapMarkerController() {
        view = new SnapMarkerPainter();

        snapMarkerModel = new SnapMarkerModel();
        snapMarkerModel.setPaddingTop(0);
        snapMarkerModel.setPaddingLeft(101);
        snapMarkerModel.setMarkerTime(-1);

        viewableModel = new ViewableModel();

        view.setViewableModel(viewableModel);
        view.setSnapMarkerModel(snapMarkerModel);
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
     * @return a clone of the viewable model
     */
    public ViewableModel getViewableModel() {
        // return a clone to avoid model tainting
        return viewableModel.clone();
    }

    /**
     * Copies the given viewable model
     * 
     * @param viewableModel
     */
    public void setViewableModel(final ViewableModel viewableModel) {
        /*
         * Just copy the values, do not spread references all over the place to
         * avoid model tainting.
         */
        this.viewableModel.setEnd(viewableModel.getEnd());
        this.viewableModel.setIntervalTime(viewableModel.getIntervalTime());
        this.viewableModel.setIntervalWidth(viewableModel.getIntervalWidth());
        this.viewableModel.setZoomWindowEnd(viewableModel.getZoomWindowEnd());
        this.viewableModel.setZoomWindowStart(viewableModel
                .getZoomWindowStart());
        view.setViewableModel(this.viewableModel);
    }

    /**
     * @return View used by the controller
     */
    public JComponent getView() {
        return view;
    }

}
