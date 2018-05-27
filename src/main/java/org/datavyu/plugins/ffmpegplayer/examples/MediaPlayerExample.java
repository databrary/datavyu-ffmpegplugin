package org.datavyu.plugins.ffmpegplayer.examples;

import javafx.scene.media.MediaException;
import javafx.util.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.datavyu.plugins.ffmpegplayer.*;
import org.datavyu.plugins.ffmpegplayer.prototypes.MoviePlayer;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import static java.lang.Double.max;


public class MediaPlayerExample extends JPanel implements WindowListener {

    private static final double DEFAULT_FPS = 29.97; // Default value for the frames per second

    private static final long MILLI_IN_SEC = 1000L;

	/** Identifier for object serialization */
	private static final long serialVersionUID = 5109839668203738974L;

    /** The logger for this class */
    private static Logger logger = LogManager.getFormatterLogger(MediaPlayerExample.class);

	/** The movie stream for this movie player */
	private java.util.List<MediaPlayer> mediaPlayers = new ArrayList<>();

	/** Used to open video files */
	private JFileChooser fileChooser;

	/** The directory last opened is used to initialize the file chooser. */
	private File lastDirectory = new File(System.getProperty("user.home"));

	/** A slider displays the current frame and the user can drag the slider to switch to a different frame. */
	private JSlider slider;

	/** A Swing Timer to update the JSlider and Time Stamp Label */
	private Timer timer;

	private JLabel timeStamp;

	private JLabel duration;

	private int ONE_SECONDS = 1000;

	public MediaPlayerExample() {
		setLayout(new BorderLayout());

		JToolBar tools = new JToolBar();

		fileChooser = new JFileChooser();

		fileChooser.addChoosableFileFilter(new VideoFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setSelectedFile(lastDirectory);

		slider = new JSlider();
		slider.setValue(0);
		slider.setMinimum(0);

		// Open file dialog.
		JButton open = new JButton("Open File");
		JButton play = new JButton("Play");
		JButton stop = new JButton("Stop");
		JButton stepBackward = new JButton("<");
		JButton stepForward = new JButton(">");

		tools.add(open);
		tools.add(play);
		tools.add(stop);
		tools.add(stepBackward);
		tools.add(stepForward);

		open.addActionListener(new OpenFileSelection());
		play.addActionListener(new PlaySelection());
		stop.addActionListener(new StopSelection());
		stepBackward.addActionListener(new StepBackwardSelection());
		stepForward.addActionListener(new StepForwardSelection());
		slider.addChangeListener(new SliderSelection());


		timer = new Timer(0, e -> {
			for (MediaPlayer mediaPlayer : mediaPlayers) {
				//There is a Media Player returning -1
				if(mediaPlayer.getCurrentTime() >= 0 && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING){
					slider.setMaximum((int) (mediaPlayer.getDuration() * ONE_SECONDS ));
					slider.setValue((int) (mediaPlayer.getCurrentTime() * ONE_SECONDS ));
					timeStamp.setText(timeStamp((long) mediaPlayer.getCurrentTime() * ONE_SECONDS));
				}
			}
		});

		// Speed selection.
        SpeedValueSelection speedValueSelect = new SpeedValueSelection();
        java.util.List<Pair<String, Double>> speedsLabelValue = new ArrayList<Pair<String, Double>>() {{
            add(new Pair<>("-32x", -32.0));
            add(new Pair<>("-16x", -16.0));
            add(new Pair<>("-8x", -8.0));
            add(new Pair<>("-4x", -4.0));
            add(new Pair<>("-2x", -2.0));
            add(new Pair<>("-1x", -1.0));
            add(new Pair<>("-1/2x", -0.5));
            add(new Pair<>("-1/4x", -0.25));
            add(new Pair<>("-1/8x", -0.125));
            add(new Pair<>("-1/16x", -0.0625));
            add(new Pair<>("-1/32x", -0.03125));
            add(new Pair<>("0x", 0.0));
            add(new Pair<>("1/32x", 0.03125));
            add(new Pair<>("1/16x", 0.0625));
            add(new Pair<>("1/8x", 0.125));
            add(new Pair<>("1/4x", 0.25));
            add(new Pair<>("1/2x", 0.5));
            add(new Pair<>("1x", 1.0));
            add(new Pair<>("2x", 2.0));
            add(new Pair<>("4x", 4.0));
            add(new Pair<>("8x", 8.0));
            add(new Pair<>("16x", 16.0));
            add(new Pair<>("32x", 32.0));
        }};
        java.util.List<JRadioButton> speedButtons = new ArrayList<>(speedsLabelValue.size());
        for (Pair<String, Double> speedLabelValue : speedsLabelValue) {
            JRadioButton speedButton = new JRadioButton(speedLabelValue.getKey());
            speedButton.setActionCommand(speedLabelValue.getValue().toString());
            speedButton.addActionListener(speedValueSelect);
            speedButtons.add(speedButton);
        }

		// Select default.
        speedButtons.get(speedsLabelValue.size()/2).setSelected(true);

		// Add radio buttons to a group for mutual exclusion.
		ButtonGroup speedsGroup = new ButtonGroup();
		for (JRadioButton speedButton : speedButtons) {
		    speedsGroup.add(speedButton);
        }

		// Add radio buttons to a panel for display.
		JPanel speedsPanel = new JPanel(new GridLayout(2, 0));
		for (JRadioButton speedButton : speedButtons) {
		    speedsPanel.add(speedButton);
        }

		tools.add(new JLabel("Speed:"));

		timeStamp = new JLabel("00:00:00:000");
		duration = new JLabel("00:00:00:000");
		tools.add(speedsPanel);

		JLabel frameNumber = new JLabel("0");
		tools.add(frameNumber);

		add(tools, BorderLayout.NORTH);
		add(timeStamp, BorderLayout.CENTER,0);
		add(slider, BorderLayout.SOUTH);

		openFile("C:\\\\Users\\\\Florian\\\\video_1080p.mp4");
		//openFile("C:\\Users\\Florian\\DatavyuSampleVideo.mp4");

		//openFile("C:\\Users\\Florian\\TurkishManGaitClip_KEATalk.mov");
		//openFile("C:\\Users\\Florian\\NoAudio\\TurkishCrawler_NoAudio.mov");
	}

<<<<<<< HEAD
=======
	private double getMaxFrameRate() {
	    double fps = DEFAULT_FPS;
        for (MediaPlayer mediaPlayer : mediaPlayers) {
            fps = max(fps, mediaPlayer.getAverageFrameRate());
        }
        return fps;
    }
	
>>>>>>> 1281e9a8... Fix audio sync in native code
	/**
     * Create the GUI and show it. For thread safety, this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        // Create and set up the window
        JFrame frame = new JFrame("Video Player");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MediaPlayerExample player = new MediaPlayerExample();
        player.addWindowListener(frame);
        // Add content to the window
        frame.add(player, BorderLayout.CENTER);

        // Display the window
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);

        // Log info only
        Configurator.setRootLevel(Level.DEBUG);

        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(()->createAndShowGUI());
    }

	private void openFile(String fileName) {
		MoviePlayerFrame moviePlayerFrame = new MoviePlayerFrame(fileName);
		mediaPlayers.add(moviePlayerFrame.getMovieStreamProvider());
	}

    private void addWindowListener(Window w) {
        w.addWindowListener(this);
    }

	@Override
	public void windowOpened(WindowEvent e) {
		/* Nothing here */
	}

	@Override
	public void windowClosing(WindowEvent e) {
        for (MediaPlayer movieStreamProvider : mediaPlayers) {
            movieStreamProvider.close();
        }
	}

	@Override
	public void windowClosed(WindowEvent e) {
		/* Nothing here */
	}

	@Override
	public void windowIconified(WindowEvent e) {
		/* Nothing here */
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		/* Nothing here */
	}

	@Override
	public void windowActivated(WindowEvent e) {
		/* Nothing here */
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		/* Nothing here */
	}

	/**
	 * An encapsulated class that ensures that filters files down to video files based on known file extensions.
	 */
	class VideoFilter extends FileFilter {

		private String getFileExtension(File file) {
		    String name = file.getName();
		    int iDot = name.lastIndexOf(".");
		    return (iDot == -1 || iDot == name.length()) ? "" : name.substring(iDot + 1);
		}

		@Override
		public boolean accept(File f) {

			// If this file is a directory display it.
			if (f.isDirectory()) {
				return true;
			}

			// Get the file extension.
			String ext = getFileExtension(f);

			// Convert to all lower case.
			ext = ext.toLowerCase();

			// Filter out the available file formats.
			return ext.equals("mp4") || ext.equals("mpg") || ext.equals("h264") || ext.equals("mov")
					|| ext.equals("wav");
		}

		@Override
		public String getDescription() {
			return "mp4, mpg, h264, mov, wav";
		}
	}

	class PlaySelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			timer.start();
            for (MediaPlayer movieStreamProvider : mediaPlayers) {
                movieStreamProvider.play();
            }
		}
	}

	class StopSelection implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	timer.stop();
            for (MediaPlayer mediaPlayer : mediaPlayers) {
                mediaPlayer.stop();
            }
        }
    }

	class StepBackwardSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
            long stepSize = (long) Math.ceil(MILLI_IN_SEC / getMaxFrameRate()); // step size is in milliseconds
            for (MediaPlayer mediaPlayer : mediaPlayers) {
                // TODO: Check boundaries, add modulo
                mediaPlayer.seek(mediaPlayer.getCurrentTime() - stepSize);
            }
		}
	}

	class StepForwardSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
            long stepSize = (long) Math.ceil(MILLI_IN_SEC / getMaxFrameRate()); // step size is in milliseconds
            for (MediaPlayer mediaPlayer : mediaPlayers) {
                // TODO: Check boundaries, add modulo
                mediaPlayer.seek(mediaPlayer.getCurrentTime() + stepSize);
            }
		}
	}

	class SliderSelection implements ChangeListener {

		private final static long SYNC_INTERVAL = 500;  // in milliseconds

		private final static long SYNC_DELAY = 0; // in milliseconds

		private final static double SYNC_THRESHOLD = 0.5; // in seconds

/*        SliderSelection() {
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    double streamTime = SliderStreamListener.toStreamTime(slider.getValue());
                    for (MediaPlayer movieStreamProvider : mediaPlayers) {
                        if (Math.abs(streamTime - movieStreamProvider.getCurrentTime()) >= SYNC_THRESHOLD) {
                            logger.info("The slider time is: " + streamTime + " seconds and the stream time is: "
                                    + movieStreamProvider.getCurrentTime() + " seconds");
                            movieStreamProvider.seek(streamTime);
                            //mediaPlayer.startVideoListeners();
                            //mediaPlayer.nextImageFrame();
                        }
                    }
                }
            }, SYNC_DELAY, SYNC_INTERVAL);
        }*/

		@Override
		public void stateChanged(ChangeEvent e) {
            if (slider.getValueIsAdjusting()) {
                for (MediaPlayer mediaPlayer : mediaPlayers) {
//                    mediaPlayer.stop();
					//TODO: Add the seek here
                }
            }
		}
	}

    private class MoviePlayerFrame {
        final JFrame frame;
        MediaPlayer mediaPlayer;

        MoviePlayerFrame(String movieFileName) {
            this(movieFileName, "0.0.1");
        }

        MoviePlayerFrame(String movieFileName, String version) throws MediaException {
            final ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
            final AudioFormat audioFormat = AudioSoundStreamListener.getNewMonoFormat();

            frame = new JFrame();
            frame.addWindowListener( new WindowAdapter() {
                public void windowClosing(WindowEvent ev) {
                    mediaPlayer.close();
                    mediaPlayers.remove(mediaPlayer);
                    frame.setVisible(false);
                }
            } );

			mediaPlayer = MediaPlayer
					.newBuilder()
					.setFileName(movieFileName)
					.setVersion(version)
					.setColorSpace(colorSpace)
					.setAudioFormat(audioFormat)
                    .addAudioStreamListener(new AudioVisualizer(audioFormat)) // Add audio visualizer
                    .addAudioStreamListener(new AudioSoundStreamListener(audioFormat)) // Add the audio sound listener
                    .addImageStreamListener(new ImageStreamListenerFrame(frame, colorSpace)) // Add video display
					.build();

			if (mediaPlayer.hasError()) {
				throw mediaPlayer.getError();
			}
        }

        MediaPlayer getMovieStreamProvider() {
            return mediaPlayer;
        }
    }

	class SpeedValueSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
			    float newSpeed = Float.parseFloat(e.getActionCommand());
			    for (MediaPlayer movieStreamProvider : mediaPlayers) {
			        movieStreamProvider.setSpeed(newSpeed);
                }
			} catch (NumberFormatException ex) {
				System.err.println("Could not parse command: " + e.getActionCommand());
			}
		}
	}

	class OpenFileSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			// Show the file chooser.
	        int val = fileChooser.showDialog(MediaPlayerExample.this, "Open");

	        // Process the results.
	        if (val == JFileChooser.APPROVE_OPTION) {
	            File file = fileChooser.getSelectedFile();
	            lastDirectory = new File(file.getAbsolutePath());
	            openFile(file.getAbsolutePath());
	        }

	        // Set the file chooser's default directory for next time.
	        fileChooser.setSelectedFile(lastDirectory);
		}
	}

	private String timeStamp(final long timeInMillis){
		String hours = String.format("%02d", TimeUnit.MILLISECONDS.toHours(timeInMillis));
		String minutes = String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(timeInMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeInMillis)));
		String seconds = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(timeInMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillis)));
		String millis = String.format("%03d", TimeUnit.MILLISECONDS.toMillis(timeInMillis) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(timeInMillis)));

		String timeStampString = hours + ":" + minutes + ":" + seconds + ":" + millis;
		return timeStampString;
	}
}
