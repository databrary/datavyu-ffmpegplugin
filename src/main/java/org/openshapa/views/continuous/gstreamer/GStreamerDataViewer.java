package org.openshapa.views.continuous.gstreamer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.gstreamer.ClockTime;
import org.gstreamer.Gst;
import org.gstreamer.State;
import org.gstreamer.elements.PlayBin;
import org.gstreamer.swing.VideoComponent;
import org.openshapa.views.component.DefaultTrackPainter;
import org.openshapa.views.component.TrackPainter;
import org.openshapa.views.continuous.DataController;
import org.openshapa.views.continuous.DataViewer;
import org.openshapa.views.continuous.ViewerStateListener;

public class GStreamerDataViewer implements DataViewer {
	public GStreamerDataViewer(Frame parent, boolean modal) {
		System.out.println("GStreamerDataViewer.GStreamerDataViewer()");
		trackPainter = new DefaultTrackPainter();
		setOffset(0);
	}
	
	static {
		Gst.init();
		//DAVETODO add unload hooks
	}

	private long offset;
	private final TrackPainter trackPainter;
	private File dataFeed;
	private DataController parentDataController;
	private PlayBin playBin;
	private long duration = 0;
	private boolean isPlaying = false;
	
	@Override
	public long getCurrentTime() throws Exception {
//		System.out.println("GStreamerDataViewer.getCurrentTime() = " + (playBin != null ? playBin.queryPosition(TimeUnit.MILLISECONDS) : 0));
		return 0;
//		return playBin != null ? playBin.queryPosition(TimeUnit.MILLISECONDS) : 0;
	}

	@Override
	public File getDataFeed() {
		System.out.println("GStreamerDataViewer.getDataFeed()");
		return dataFeed;
	}

	@Override
	public long getDuration() {
		return duration;
	}

	@Override
	public float getFrameRate() {
		System.out.println("GStreamerDataViewer.getFrameRate()");
		return 25;
	}

	@Override
	public long getOffset() {
		return offset;
	}

	@Override
	public JDialog getParentJDialog() {
		System.out.println("GStreamerDataViewer.getParentJDialog()");
		return new JDialog();
	}

	@Override
	public TrackPainter getTrackPainter() {
		System.out.println("GStreamerDataViewer.getTrackPainter()");
		return trackPainter;
	}

	@Override
	public synchronized boolean isPlaying() {
		return isPlaying;
	}

	@Override
	public void loadSettings(InputStream is) {
		System.out.println("GStreamerDataViewer.loadSettings()");
	}

	@Override
	public synchronized void play() {
		System.out.println("GStreamerDataViewer.play()");
		if (playBin != null) {
			playBin.setState(State.PLAYING);
			isPlaying = true;
			playBin.getState(); // wait for the state change to take effect
		}
	}

	@Override
	public synchronized void seekTo(long position) {
//		System.out.println("GStreamerDataViewer.seekTo(" + position + ")");
		if (playBin != null) {
			if (!isPlaying) {
//		        playBin.setState(State.PAUSED);
//				System.out.println("seeking...");
//				playBin.seek(1.0f, Format.TIME, SeekFlags.FLUSH | SeekFlags.SEGMENT, SeekType.SET, playBin.queryPosition(TimeUnit.NANOSECONDS), SeekType.SET, playBin.queryDuration(TimeUnit.NANOSECONDS));
				playBin.seek(ClockTime.fromMillis(position));
//		        playBin.setState(State.PLAYING);
//				lastSeekTime = System.currentTimeMillis();
			} else {
//				System.out.println("ignoring seek.");
			}
		}
	}
	
	@Override
	public synchronized void setDataFeed(final File dataFeed) {
		System.out.println("GStreamerDataViewer.setDataFeed(\"" + dataFeed.getAbsolutePath() + "\")");

		if (this.dataFeed != null) {
			return;
		}
		
		this.dataFeed = dataFeed;
		
		playBin = new PlayBin("OpenSHAPA");
        playBin.setInputFile(dataFeed);

        final VideoComponent videoComponent = new VideoComponent();
        playBin.setVideoSink(videoComponent.getElement());

		playBin.setState(State.PAUSED);
		isPlaying = false;
		playBin.getState(); // wait for the state change to take effect
        
		System.out.println("**** Duration = " + getDuration());
		duration = playBin.queryDuration(TimeUnit.MILLISECONDS);
		
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(dataFeed.getName());
                frame.getContentPane().add(videoComponent, BorderLayout.CENTER);
                frame.setPreferredSize(new Dimension(640, 480));
                frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
	}

	@Override
	public void setOffset(long offset) {
		System.out.println("GStreamerDataViewer.setOffset(" + offset + ")");
		this.offset = offset;
	}

	@Override
	public synchronized void setParentController(DataController dataController) {
		System.out.println("GStreamerDataViewer.setParentController()");
		parentDataController = dataController;
	}

	@Override
	public void setPlaybackSpeed(float rate) {
		System.out.println("GStreamerDataViewer.setPlaybackSpeed(" + rate + ")");
		if (playBin != null) {
			if (rate > 0) {
				playBin.setState(State.PAUSED);
//				playBin.seek(rate, Format.TIME, 0, SeekType.SET, playBin.queryPosition(TimeUnit.NANOSECONDS), SeekType.SET, playBin.queryDuration(TimeUnit.NANOSECONDS));
				playBin.setState(State.PLAYING);
				isPlaying = true;
			} else {
				playBin.setState(State.PAUSED);
				isPlaying = false;
			}
			playBin.getState(); // wait for the state change to take effect
		}
	}

	@Override
	public synchronized void stop() {
		System.out.println("GStreamerDataViewer.stop()");
		if (playBin != null) {
			playBin.setState(State.PAUSED);
			isPlaying = false;
			playBin.getState(); // wait for the state change to take effect
		}
	}

	@Override
	public void storeSettings(OutputStream os) {
		System.out.println("GStreamerDataViewer.storeSettings()");
	}

	@Override
	public void addViewerStateListener(ViewerStateListener vsl) {
		System.out.println("GStreamerDataViewer.addViewerStateListener()");
	}

	@Override
	public ImageIcon getActionButtonIcon1() {
		System.out.println("GStreamerDataViewer.getActionButtonIcon1()");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageIcon getActionButtonIcon2() {
		System.out.println("GStreamerDataViewer.getActionButtonIcon2()");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageIcon getActionButtonIcon3() {
		System.out.println("GStreamerDataViewer.getActionButtonIcon3()");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleActionButtonEvent1(ActionEvent event) {
		System.out.println("GStreamerDataViewer.handleActionButtonEvent1()");
		// TODO Auto-generated method stub

	}

	@Override
	public void handleActionButtonEvent2(ActionEvent event) {
		System.out.println("GStreamerDataViewer.handleActionButtonEvent2()");
		// TODO Auto-generated method stub

	}

	@Override
	public void handleActionButtonEvent3(ActionEvent event) {
		System.out.println("GStreamerDataViewer.handleActionButtonEvent3()");
		// TODO Auto-generated method stub

	}
}
