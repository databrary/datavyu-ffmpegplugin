package org.openshapa.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import org.openshapa.component.model.RegionModel;
import org.openshapa.component.model.ViewableModel;

/**
 * This class paints the custom playback region.
 */
public class RegionPainter extends Component {

    /** Polygon region for the start marker */
    private Polygon startMarkerPolygon;
    /** Polygon region for the end marker */
    private Polygon endMarkerPolygon;

    private RegionModel regionModel;
    private ViewableModel viewableModel;

    public RegionPainter() {
        super();
    }

    public RegionModel getRegionModel() {
        return regionModel;
    }

    public void setRegionModel(RegionModel regionModel) {
        this.regionModel = regionModel;
        this.repaint();
    }

    public ViewableModel getViewableModel() {
        return viewableModel;
    }

    public void setViewableModel(ViewableModel viewableModel) {
        this.viewableModel = viewableModel;
        this.repaint();
    }

    public Polygon getEndMarkerPolygon() {
        return endMarkerPolygon;
    }

    public Polygon getStartMarkerPolygon() {
        return startMarkerPolygon;
    }

    @Override
    public boolean contains(Point p) {
        return startMarkerPolygon.contains(p) || endMarkerPolygon.contains(p);
    }

    @Override
    public boolean contains(int x, int y) {
        return startMarkerPolygon.contains(x, y) || endMarkerPolygon.contains(x, y);
    }

    @Override
    public void paint(Graphics g) {
        if (regionModel == null || viewableModel == null) {
            return;
        }
        Dimension size = this.getSize();

        final float ratio = viewableModel.getIntervalWidth() / viewableModel.getIntervalTime();

        // If the left region marker is visible, paint the marker
        final long regionStart = regionModel.getRegionStart();
        final long regionEnd = regionModel.getRegionEnd();
        final int paddingTop = regionModel.getPaddingTop();

        // If the left region marker is visible, paint the marker
        if (regionStart >= viewableModel.getZoomWindowStart()) {
            g.setColor(new Color(15, 135, 0, 100)); // Semi-transparent green
            // The polygon tip
            int pos = Math.round(regionModel.getRegionStart() * ratio
                    - viewableModel.getZoomWindowStart() * ratio)
                    + regionModel.getPaddingLeft();

            // Make an arrow
            startMarkerPolygon = new Polygon();
            startMarkerPolygon.addPoint(pos - 10, paddingTop);
            startMarkerPolygon.addPoint(pos, 19 + paddingTop);
            startMarkerPolygon.addPoint(pos, 37 + paddingTop);
            startMarkerPolygon.addPoint(pos - 10, 37 + paddingTop);
            g.fillPolygon(startMarkerPolygon);

            // Draw outline
            g.setColor(new Color(15, 135, 0));
            g.drawPolygon(startMarkerPolygon);

            // Draw drop down line
            g.drawLine(pos, 37, pos, size.height);
        }

        // If the right region marker is visible, paint the marker
        if (regionEnd <= viewableModel.getZoomWindowEnd()) {
            g.setColor(new Color(15, 135, 0, 100)); // Semi-transparent green

            // The polygon tip
            int pos = Math.round(regionModel.getRegionEnd() * ratio
                    - viewableModel.getZoomWindowStart() * ratio)
                    + regionModel.getPaddingLeft();
            endMarkerPolygon = new Polygon();
            endMarkerPolygon.addPoint(pos + 1, 19 + paddingTop);
            endMarkerPolygon.addPoint(pos + 11, paddingTop);
            endMarkerPolygon.addPoint(pos + 11, 37 + paddingTop);
            endMarkerPolygon.addPoint(pos + 1, 37 + paddingTop);


            g.fillPolygon(endMarkerPolygon);

            // Draw outline
            g.setColor(new Color(15, 135, 0));
            g.drawPolygon(endMarkerPolygon);

            // Draw drop down line
            g.drawLine(pos + 1, 37, pos + 1, size.height);
        }

        /* Check if the selected region is not the maximum viewing window,
         * if it is not the maximum, highlight the areas over the tracks.
         */
        if ((regionStart > 0) || (regionEnd < viewableModel.getEnd())) {
            final long windowStart = viewableModel.getZoomWindowStart();
            final long windowEnd = viewableModel.getZoomWindowEnd();
            
            long visibleStartRegion = regionStart >= windowStart 
                    ? regionStart
                    : windowStart;
            long visibleEndRegion = regionEnd <= windowEnd
                    ? regionEnd
                    : windowEnd;

            int startPos = Math.round(visibleStartRegion * ratio -
                    windowStart * ratio) + regionModel.getPaddingLeft();
            int endPos = Math.round(visibleEndRegion * ratio -
                    windowStart * ratio) + regionModel.getPaddingLeft() + 1;

            g.setColor(new Color(15, 135, 0, 100));
            g.fillRect(startPos, 37, endPos - startPos, size.height);
        }

    }
}
