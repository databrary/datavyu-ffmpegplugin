package org.openshapa.views.component;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class TracksEditorPainter extends JPanel {
	public TracksEditorPainter() {
		//TODO need to get these numbers from the MixerControllerV class
		setLayout(new MigLayout("wrap, ins 0", "[685]", "[70]"));
		setOpaque(false);
	}
	
	public void paint(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setColor(new Color(237, 237, 237));
		//TODO need to get these numbers from the MixerControllerV class
		g.fillRect(101, 0, 654, getHeight());
		
		super.paint(g);
	}
}
