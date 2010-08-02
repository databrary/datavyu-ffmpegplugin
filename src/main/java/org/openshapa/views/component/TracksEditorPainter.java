package org.openshapa.views.component;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;


public final class TracksEditorPainter extends JPanel {

    private static final Color PANEL_BG_COLOR = Color.WHITE;

    private static final Color TRACK_BG_COLOR = new Color(237, 237, 237);

    public TracksEditorPainter() {
        setLayout(new MigLayout("wrap, ins 0", "", ""));
        setOpaque(false);
    }

    public void paint(final Graphics g) {
        g.setColor(PANEL_BG_COLOR);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(TRACK_BG_COLOR);

        //TODO need to get these numbers from the MixerControllerV class
        g.fillRect(101, 0, 654, getHeight());

        super.paint(g);
    }
}
