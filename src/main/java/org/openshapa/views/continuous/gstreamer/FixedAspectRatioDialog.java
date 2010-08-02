package org.openshapa.views.continuous.gstreamer;

import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JDialog;
import javax.swing.event.MouseInputListener;

public class FixedAspectRatioDialog extends JDialog implements MouseInputListener, MouseMotionListener, ComponentListener {
	public FixedAspectRatioDialog() {
		init();
	}

	public FixedAspectRatioDialog(Frame owner) {
		super(owner);
		init();
	}

	public FixedAspectRatioDialog(Dialog owner) {
		super(owner);
		init();
	}

	public FixedAspectRatioDialog(Window owner) {
		super(owner);
		init();
	}

	public FixedAspectRatioDialog(Frame owner, boolean modal) {
		super(owner, modal);
		init();
	}

	public FixedAspectRatioDialog(Frame owner, String title) {
		super(owner, title);
		init();
	}

	public FixedAspectRatioDialog(Dialog owner, boolean modal) {
		super(owner, modal);
		init();
	}

	public FixedAspectRatioDialog(Dialog owner, String title) {
		super(owner, title);
		init();
	}

	public FixedAspectRatioDialog(Window owner, ModalityType modalityType) {
		super(owner, modalityType);
		init();
	}

	public FixedAspectRatioDialog(Window owner, String title) {
		super(owner, title);
		init();
	}

	public FixedAspectRatioDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		init();
	}

	public FixedAspectRatioDialog(Dialog owner, String title, boolean modal) {
		super(owner, title, modal);
		init();
	}

	public FixedAspectRatioDialog(Window owner, String title,
			ModalityType modalityType) {
		super(owner, title, modalityType);
		init();
	}

	public FixedAspectRatioDialog(Frame owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		init();
	}

	public FixedAspectRatioDialog(Dialog owner, String title, boolean modal,
			GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		init();
	}

	public FixedAspectRatioDialog(Window owner, String title,
			ModalityType modalityType, GraphicsConfiguration gc) {
		super(owner, title, modalityType, gc);
		init();
	}

	private void init() {
		setResizable(false);
		setUndecorated(true);
		getGlassPane().addMouseListener(this);
		getGlassPane().addMouseMotionListener(this);
		getGlassPane().setVisible(true);
	}
	
	private boolean isResizing = false;
	
	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println(e.getSource().getClass().getSimpleName() + " mouseClicked(" + e + "), dialog width=" + getGlassPane().getWidth() + ", height=" + getGlassPane().getHeight());
		System.out.println(getComponentAt(getWidth() - 3, getHeight() - 3));
	}

	Point mousePressedPoint = new Point(0, 0);
	Point dialogLocation = new Point(0, 0);
	
	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println(e.getSource().getClass().getSimpleName() + " mousePressed(" + e + ")");
		mousePressedPoint = e.getLocationOnScreen();
		dialogLocation = getLocation();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		System.out.println(e.getSource().getClass().getSimpleName() + " mouseReleased(" + e + ")");
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		System.out.println(e.getSource().getClass().getSimpleName() + " mouseEntered(" + e + ")");
	}

	@Override
	public void mouseExited(MouseEvent e) {
		System.out.println(e.getSource().getClass().getSimpleName() + " mouseExited(" + e + ")");
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		System.out.println(e.getSource().getClass().getSimpleName() + " mouseDragged(" + e + ")");
		updateResizeCursor(e);
		System.out.println("" + (e.getXOnScreen() - mousePressedPoint.getX()));
		System.out.println("" + (int) (dialogLocation.getX() - mousePressedPoint.getX() + e.getXOnScreen()));
		setLocation((int) (dialogLocation.getX() - mousePressedPoint.getX() + e.getXOnScreen()), (int) (dialogLocation.getY() - mousePressedPoint.getY() + e.getYOnScreen()));
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		System.out.println(e.getSource().getClass().getSimpleName() + " mouseMoved(" + e + ")");
		updateResizeCursor(e);		
	}

	private final Cursor resizeCursor = new Cursor(Cursor.NW_RESIZE_CURSOR);
	private final Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
	
	private void updateResizeCursor(MouseEvent e) {
		if (!isResizing) {
			final int resizeControlAreaSize = 30;
			final boolean cursorIsInResizeControlArea = (e.getX() >= getGlassPane().getWidth() - resizeControlAreaSize) && (e.getX() < getGlassPane().getWidth()) && (e.getY() >= getGlassPane().getHeight() - resizeControlAreaSize) && (e.getY() < getGlassPane().getHeight()); 
			getGlassPane().setCursor(cursorIsInResizeControlArea ? resizeCursor : defaultCursor);
		}
	}

	@Override
	public void componentResized(ComponentEvent e) {

		try {
			isResizing = true;
			setSize(getWidth(), getWidth() * 3 / 4);
		} finally {
			isResizing = false;
		}
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
}
