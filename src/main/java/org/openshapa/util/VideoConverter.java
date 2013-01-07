/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openshapa.util;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import org.openshapa.views.VideoConverterV;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

/**
 *
 * @author jesse
 */
public class VideoConverter {
	
	private JFrame frame;
	private JPanel contentPane;
	private Canvas canvas;
	private MediaPlayerFactory factory;
	private EmbeddedMediaPlayer mediaPlayer;
	private CanvasVideoSurface videoSurface;
	
	static {
		// Try to load VLC libraries.
		// This discovery function is platform independent
		new NativeDiscovery().discover();
	}
	
	public VideoConverter() {
		
	}
	
	public void ConvertVideo(File infile, File outfile, final JProgressBar progressBar) {
		String[] libvlcArgs = {":sout=#transcode{vcodec=h264,vb=1024,acodec=mp4"
					+ "ab=192,max-keyint=1,keyint=1,min-keyint=1,idrint=1,bframes=1,fps=29.97}:standard{mux=mp4,dst=" 
					+ outfile.getAbsolutePath() + ",access=file}"};
		
		canvas = new Canvas();
		canvas.setBackground(Color.black);

		contentPane = new JPanel();
		contentPane.setBackground(Color.black);
		contentPane.setLayout(new BorderLayout());
		contentPane.add(canvas, BorderLayout.CENTER);

		frame = new JFrame("Capture");
		frame.setContentPane(contentPane);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setLocation(50, 50);
		frame.setSize(800, 600);

		factory = new MediaPlayerFactory("--no-video-title-show");
		mediaPlayer = factory.newEmbeddedMediaPlayer();

		videoSurface = factory.newVideoSurface(canvas);

		mediaPlayer.setVideoSurface(videoSurface);
		
		frame.setVisible(true);
		
		mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			
			@Override
			public void positionChanged(MediaPlayer mediaPlayer, final float newPosition) {
				if ( SwingUtilities.isEventDispatchThread() ) {
					int value = Math.min(100, Math.round(newPosition * 100.0f));
					progressBar.setValue(value);
				}
				else
					SwingUtilities.invokeLater( new Runnable() {
						@Override
						public void run() {
							int value = Math.min(100, Math.round(newPosition * 100.0f));
							progressBar.setValue(value);
						}
				} );
			}
			
			@Override
			public void finished(final MediaPlayer mediaPlayer) {
				if ( SwingUtilities.isEventDispatchThread() ) {
					progressBar.setValue(progressBar.getMaximum());
					mediaPlayer.stop();
				}
				else
					SwingUtilities.invokeLater( new Runnable() {
						@Override
						public void run() {
							progressBar.setValue(progressBar.getMaximum());
							mediaPlayer.stop();
						}
				} );
			}
		});

		mediaPlayer.playMedia(infile.getAbsolutePath(), libvlcArgs);
		
		frame.setVisible(false);
	}
	
	public void StopConversion() {
		mediaPlayer.stop();
	}
	
	public void DetectIfNeedsConvert(File infile) {
		
	}
}
