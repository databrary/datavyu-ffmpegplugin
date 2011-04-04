package org.openshapa.views.discrete;

import com.usermetrix.jclient.Logger;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Box.Filler;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import org.openshapa.Configuration;
import org.openshapa.OpenSHAPA;

import com.usermetrix.jclient.UserMetrix;

import java.awt.Color;

import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

import org.openshapa.models.db.legacy.DataColumn;


/**
 * Visual representation of a spreadsheet cell.
 */
public class SpreadsheetEmptyCell extends JPanel implements MouseListener {

    /** Width of spacer between onset and offset timestamps. */
    private static final int TIME_SPACER = 5;

    private static final int ALPHA = 70;

    /** The logger for this class. */
    private static final Logger LOGGER = UserMetrix.getLogger(SpreadsheetEmptyCell.class);

    /** Border to use for normal cell. No extra information to show. */
    private static final Border NORMAL_BORDER = new MatteBorder(0, 0, 1, 1, Configuration.BORDER_COLOUR);

    /** The panel that displays the cell. */
    private JPanel cellPanel;

    /** A panel for holding the header to the cell. */
    private JPanel topPanel;

    /** A panel for holding the value of the cell. */
    private JLabel dataPanel;

    /** The Ordinal display component. */
    private JLabel ord;

    /** The Onset display component. */
    private JLabel onset;

    /** The Offset display component. */
    private JLabel offset;

    /** Component that sets the width of the cell. */
    private Filler stretcher;

    /** strut creates the gap between this cell and the previous cell. */
    private Filler strut;

    /** Parent ID for data column. */
    private DataColumn parent;

    /**
     * The height of the visible portion of the cell requested by the active
     * SheetLayout.
     */
    private int layoutPreferredHeight;

    /**
     * Creates new Empty SpreadsheetCell stub.
     */
    public SpreadsheetEmptyCell(final DataColumn newCol) {
        setName(this.getClass().getSimpleName());

        ResourceMap rMap = Application.getInstance(OpenSHAPA.class).getContext()
                                      .getResourceMap(SpreadsheetCell.class);

        cellPanel = new JPanel();
        cellPanel.addMouseListener(this);
        strut = new Filler(new Dimension(0, 0),
                           new Dimension(0, 0),
                           new Dimension(Short.MAX_VALUE, 0));

        setLayout(new BorderLayout());
        this.add(strut, BorderLayout.NORTH);
        this.add(cellPanel, BorderLayout.CENTER);

        // Build components used for the spreadsheet cell.
        topPanel = new JPanel();
        topPanel.addMouseListener(this);
        ord = new JLabel("+");
        ord.setFont(Configuration.getInstance().getSSLabelFont());
        ord.setForeground(addAlpha(Configuration.getInstance().getSSOrdinalColour(), ALPHA));
        ord.setToolTipText(rMap.getString("ord.tooltip"));
        ord.addMouseListener(this);
        ord.setFocusable(true);

        onset = new JLabel("--:--:--:---");
        onset.setFont(Configuration.getInstance().getSSLabelFont());
        onset.setForeground(addAlpha(Configuration.getInstance().getSSTimestampColour(), ALPHA));
        onset.setToolTipText(rMap.getString("onset.tooltip"));
        onset.addMouseListener(this);
        onset.setName("onsetTextField");

        offset = new JLabel("--:--:--:---");
        offset.setFont(Configuration.getInstance().getSSLabelFont());
        offset.setForeground(addAlpha(Configuration.getInstance().getSSTimestampColour(), ALPHA));
        offset.setToolTipText(rMap.getString("offset.tooltip"));
        offset.addMouseListener(this);
        offset.setName("offsetTextField");

        dataPanel = new JLabel(rMap.getString("empty.text"));
        dataPanel.setFont(Configuration.getInstance().getSSDataFont());
        dataPanel.setForeground(addAlpha(Configuration.getInstance().getSSForegroundColour(), ALPHA));

        dataPanel.setOpaque(false);
        dataPanel.addMouseListener(this);
        dataPanel.setName("cellValue");

        // Set the appearance of the spreadsheet cell.
        cellPanel.setBackground(addAlpha(Configuration.getInstance().getSSBackgroundColour(), ALPHA));
        cellPanel.setBorder(NORMAL_BORDER);
        cellPanel.setLayout(new BorderLayout());

        // Set the apperance of the top panel and add child elements (ord, onset
        // and offset).
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        cellPanel.add(topPanel, BorderLayout.NORTH);
        topPanel.add(ord);

        Component strut1 = Box.createHorizontalStrut(TIME_SPACER);
        topPanel.add(strut1);

        Component glue = Box.createGlue();
        topPanel.add(glue);

        topPanel.add(onset);

        Component strut2 = Box.createHorizontalStrut(TIME_SPACER);
        topPanel.add(strut2);
        topPanel.add(offset);

        // Set the apperance of the data panel - add elements for dis6playing
        // the actual data of the panel.
        cellPanel.add(dataPanel, BorderLayout.CENTER);

        Dimension d = new Dimension(229, 0);
        stretcher = new Filler(d, d, d);
        cellPanel.add(stretcher, BorderLayout.SOUTH);

        parent = newCol;
    }

    private static Color addAlpha(final Color col, final int alpha) {
        return new Color(col.getRed(), col.getGreen(), col.getBlue(), alpha);
    }

    /**
     * Override Maximum size to return the preferred size or the active layouts
     * preferred height, whichever is greater.
     *
     * @return the maximum size of the cell.
     */
    @Override public final Dimension getMaximumSize() {
        Dimension mysize = super.getPreferredSize();

        if ((mysize != null) && (mysize.height < (layoutPreferredHeight + strut.getHeight()))) {
            mysize = new Dimension(mysize.width, (layoutPreferredHeight + strut.getHeight()));
        }

        return mysize;
    }

    /**
     * Override Preferred size to return the maximum size (which takes into
     * account the super.preferred size. See getMaximumSize.
     *
     * @return the preferred size of the cell.
     */
    @Override public final Dimension getPreferredSize() {
        return getMaximumSize();
    }

    /**
     * Override Minimum size to return the maximum size (which takes into
     * account the super.preferred size. See getMaximumSize.
     *
     * @return the minimum size of the cell.
     */
    @Override public final Dimension getMinimumSize() {
        return getMaximumSize();
    }

    /**
     * Set the width of the SpreadsheetCell.
     *
     * @param width
     *            New width of the SpreadsheetCell.
     */
    public void setWidth(final int width) {
        Dimension d = new Dimension(width, 0);
        stretcher.changeShape(d, d, d);
    }

    /**
     * The action to invoke when the mouse enters this component.
     *
     * @param me
     *            The mouse event that triggered this action.
     */
    @Override public void mouseEntered(final MouseEvent me) {
    }

    /**
     * The action to invoke when the mouse exits this component.
     *
     * @param me
     *            The mouse event that triggered this action.
     */
    @Override public void mouseExited(final MouseEvent me) {
    }

    /**
     * The action to invoke when a mouse button is pressed.
     *
     * @param me
     *            The mouse event that triggered this action.
     */
    @Override public void mousePressed(final MouseEvent me) {
    }

    /**
     * The action to invoke when a mouse button is released.
     *
     * @param me
     *            The mouse event that triggered this action.
     */
    @Override public void mouseReleased(final MouseEvent me) {
    }

    /**
     * The action to invoke when a mouse button is clicked.
     *
     * @param me
     *            The mouse event that triggered this action.
     */
    @Override public void mouseClicked(final MouseEvent me) {
        LOGGER.usage("Pressed empty cell");
        OpenSHAPA.getView().getSpreadsheetPanel().deselectAll();
        OpenSHAPA.getProjectController().setLastCreatedColId(parent.getID());
        OpenSHAPA.getDataController().pressCreateNewCellSettingOffset();
    }

    /**
     * Method to call when painting the component.
     *
     * @param g
     */
    @Override public void paint(final Graphics g) {
        // BugzID:474 - Set the size at paint time - somewhere else may have
        // altered the font.
        dataPanel.setFont(Configuration.getInstance().getSSDataFont());
        super.paint(g);
    }
}
