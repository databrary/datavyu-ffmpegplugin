package org.openshapa.views.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Polygon;

import javax.swing.JComponent;

import org.openshapa.models.component.TrackModel;
import org.openshapa.models.component.ViewableModel;

/**
 * This class is used to paint a track and its information
 */
public class TrackPainter extends JComponent {

    /**
     * Auto generated by Eclipse
     */
    private static final long serialVersionUID = -1528097986586594694L;

    /**
     * Color schemes
     */
    private static final Color NORMAL_CARRIAGE_COLOR = new Color(169, 218, 248);
    private static final Color NORMAL_OUTLINE_COLOR = new Color(129, 167, 188);
    private static final Color SELECTED_CARRIAGE_COLOR =
            new Color(138, 223, 162);
    private static final Color SELECTED_OUTLINE_COLOR =
            new Color(105, 186, 128);

    /** Painted region of the carriage */
    private Polygon carriagePolygon;

    private TrackModel trackModel;

    private ViewableModel viewableModel;

    public TrackPainter() {
        super();
    }

    public TrackModel getTrackModel() {
        return trackModel;
    }

    public void setTrackModel(final TrackModel model) {
        trackModel = model;
        this.repaint();
    }

    public ViewableModel getViewableModel() {
        return viewableModel;
    }

    public void setViewableModel(final ViewableModel viewableModel) {
        this.viewableModel = viewableModel;
        this.repaint();
    }

    public Polygon getCarriagePolygon() {
        return carriagePolygon;
    }

    @Override
    public void paint(final Graphics g) {
        Dimension size = getSize();

        // Paints the background
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, size.width, size.height);

        // If there is an error with track information, don't paint the carriage
        if (trackModel.isErroneous()) {
            g.setColor(Color.red);
            FontMetrics fm = g.getFontMetrics();
            String errorMessage =
                    "Track timing information could not be calculated.";
            int width = fm.stringWidth(errorMessage);
            g.drawString(errorMessage, (size.width / 2) - (width / 2),
                    (size.height / 2) - (fm.getAscent() / 2));
            return;
        }

        final float ratio =
                viewableModel.getIntervalWidth()
                        / viewableModel.getIntervalTime();

        final int carriageHeight = (int) (size.getHeight() * 7D / 10D);
        final int carriageYOffset = (int) (size.getHeight() * 2D / 10D);

        // Calculate carriage start and end pixel positions
        final int startXPos =
                (int) (trackModel.getOffset() * ratio - viewableModel
                        .getZoomWindowStart()
                        * ratio);

        final int endXPos =
                (int) ((trackModel.getDuration() + trackModel.getOffset())
                        * ratio - viewableModel.getZoomWindowStart() * ratio);

        // The carriage
        carriagePolygon = new Polygon();
        // Top left corner
        carriagePolygon.addPoint(startXPos, carriageYOffset);
        // Top right corner
        carriagePolygon.addPoint(endXPos, carriageYOffset);
        // Bottom right corner
        carriagePolygon.addPoint(endXPos, carriageYOffset + carriageHeight);
        // Bottom left corner
        carriagePolygon.addPoint(startXPos, carriageYOffset + carriageHeight);

        // Paint the carriage
        if (trackModel.isSelected()) {
            g.setColor(SELECTED_CARRIAGE_COLOR);
        } else {
            g.setColor(NORMAL_CARRIAGE_COLOR);
        }

        g.fillPolygon(carriagePolygon);

        // Paint the carriage outlines
        if (trackModel.isSelected()) {
            g.setColor(SELECTED_OUTLINE_COLOR);
        } else {
            g.setColor(NORMAL_OUTLINE_COLOR);
        }

        g.drawPolygon(carriagePolygon);

        if (trackModel.getBookmark() < 0) {
            return;
        }

        // Paint the bookmark marker
        final int bookmarkXPos =
                (int) ((trackModel.getOffset() + trackModel.getBookmark())
                        * ratio - viewableModel.getZoomWindowStart() * ratio);

        g.drawLine(bookmarkXPos, carriageYOffset, bookmarkXPos, carriageYOffset
                + carriageHeight);

        // Paint the bookmark diamond

        Polygon bookmarkDiamond = new Polygon();
        // Top of diamond
        bookmarkDiamond.addPoint(bookmarkXPos, carriageYOffset - 10);
        // Right tip of diamond
        bookmarkDiamond.addPoint(bookmarkXPos + 5, carriageYOffset - 5);
        // Bottom of diamond
        bookmarkDiamond.addPoint(bookmarkXPos, carriageYOffset);
        // Left tip of diamond
        bookmarkDiamond.addPoint(bookmarkXPos - 5, carriageYOffset - 5);

        if (trackModel.isSelected()) {
            g.setColor(SELECTED_CARRIAGE_COLOR);
            g.fillPolygon(bookmarkDiamond);
            g.setColor(SELECTED_OUTLINE_COLOR);
            g.drawPolygon(bookmarkDiamond);
        } else {
            g.setColor(NORMAL_CARRIAGE_COLOR);
            g.fillPolygon(bookmarkDiamond);
            g.setColor(NORMAL_OUTLINE_COLOR);
            g.drawPolygon(bookmarkDiamond);
        }

    }
}
