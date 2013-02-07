/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.datavyu.plugins.vlcrc;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 *
 * @author jesse
 */
public class VLCRCProcess extends Thread {
	
	/** Dialog for showing our visualizations. */
	JDialog vlcDialog;

	/** Data viewer offset. */
	long offset;
	
	boolean initialized;

	/** Data to visualize. */
	File data;

	/** Boolean to keep track of whether or not we are playing */
	boolean playing;

		/** Surface on which we will display video */
	Canvas videoSurface;

	/** Factory for building our mediaPlayer */
	MediaPlayerFactory mediaPlayerFactory;

	/** The VLC mediaPlayer */
	EmbeddedMediaPlayer mediaPlayer;
	
	BufferedImage[] previousFrameBuffer;
	
	float fps;
	
	long length;
	
	public VLCRCProcess() {
		initialized = false;
	}
	
	@Override
	public void run() {
		playing = false;
		vlcDialog = new JDialog();
		vlcDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		vlcDialog.setName("VLCDataViewer");
		vlcDialog.setResizable(true);

		// Set an initial size
		vlcDialog.setSize(800, 600);

		videoSurface = new Canvas();
		videoSurface.setBackground(Color.black);

		// Set some options for libvlc
		String[] libvlcArgs = {"file-caching=5000", "-I rc", "--rc-fake-tty"};

		// Create a factory instance (once), you can keep a reference to this
		mediaPlayerFactory = new MediaPlayerFactory(libvlcArgs);

		// Create a media player instance
		mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer();

		// Add it to the dialog and place the video onto the surface
		vlcDialog.setLayout(new BorderLayout());
		vlcDialog.add(videoSurface, BorderLayout.CENTER);
		mediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(videoSurface));
		mediaPlayer.setFullScreen(false);

		vlcDialog.setVisible(true);
		
		initialized = true;
	}
	
	public void setMedia(File dataFeed) {
		vlcDialog.setVisible(true);
		vlcDialog.setName(vlcDialog.getName() + "-" + dataFeed.getName());
		mediaPlayer.startMedia(dataFeed.getAbsolutePath());

		// Grab FPS and length

		// Because of the way VLC works, we have to wait for the metadata to become
		// available a short time after we start playing.
		// TODO: reimplement this using the video output event
		try {
			int i = 0;
			while(mediaPlayer.getVideoDimension() == null) {
				if(i > 100)
					break;
				Thread.sleep(5); 
				i++; 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		fps = mediaPlayer.getFps();
		length = mediaPlayer.getLength();
		Dimension d = mediaPlayer.getVideoDimension();

		System.out.println(String.format("FPS: %f", fps));
		System.out.println(String.format("Length: %d", length));

		// Stop the player. This will rewind whatever
		// frames we just played to get the FPS and length
		mediaPlayer.pause();
		mediaPlayer.setTime(0);

		playing = false;

		vlcDialog.setSize(d);

		// Test to make sure we got the framerate.
		// If we didn't, alert the user that this
		// may not work right.
		if(fps < 1.0) {
			// VLC can't read the framerate for this video for some reason.
			// Set it to 29.97fps so it is still usable for coding.
			fps = 29.97f;
			JOptionPane.showMessageDialog(null, 
					"Warning: Unable to detect framerate in video.\n"
					+ "This video may not behave properly. "
					+ "Please try converting to H.264.\n\nSetting "
					+ "framerate to 29.97.");
		}
	}
	
	public float getFps() {
	   return fps;
	}
	
	public void play() {
	   System.out.println("Playing...");
	   System.out.println(System.currentTimeMillis());
	   mediaPlayer.play();
	}
	
	public void pause() {
	   System.out.println("Pausing...");
	   System.out.println(System.currentTimeMillis());
	   mediaPlayer.pause();
	}
	
	public void stopMedia() {
	   System.out.println("Stopping...");
	   System.out.println(System.currentTimeMillis());
	   mediaPlayer.stop();
	}
	
	public long getTime() {
	   return mediaPlayer.getTime();
	}
	
	public float getRate() {
	   return mediaPlayer.getRate();
	}
	
	public void setTime(long time) {
	   mediaPlayer.setTime(time);
	}
	
	public void jogForward() {
	   mediaPlayer.nextFrame();
	}
	
	public void jogBackward() {
	}
	
	public boolean isPlaying() {
	   return playing;
	}
	
	public void setRate(float rate) {
	   if(rate != mediaPlayer.getRate()) {
		System.out.println("Setting rate...");
		System.out.println(System.currentTimeMillis());
		mediaPlayer.setRate(rate);
		System.out.println(System.currentTimeMillis());
	   }
	}
	
	public void clearDataFeed() {
	   stopMedia();
	   videoSurface.setVisible(false);
	   vlcDialog.setVisible(false);
	   mediaPlayerFactory.release();
	}
	
	
}
