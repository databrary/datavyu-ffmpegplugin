package org.datavyu.plugins.ffmpegplayer.examples;

import javafx.util.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.datavyu.plugins.ffmpegplayer.AudioSoundStreamListener;
import org.datavyu.plugins.ffmpegplayer.MovieStreamProvider;
import org.datavyu.plugins.ffmpegplayer.SliderStreamListener;
import org.datavyu.plugins.ffmpegplayer.VideoStreamListenerContainer;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;


public class MoviePlayerExample extends JPanel implements WindowListener {

	/** Identifier for object serialization */
	private static final long serialVersionUID = 5109839668203738974L;

    /** The logger for this class */
    private static Logger logger = LogManager.getFormatterLogger(MoviePlayerExample.class);

	/** The movie stream for this movie player */
	private java.util.List<MovieStreamProvider> movieStreamProviders = new ArrayList<>();

	/** Used to open video files */
	private JFileChooser fileChooser;

	/** The directory last opened is used to initialize the file chooser. */
	private File lastDirectory = new File(System.getProperty("user.home"));

	/** A slider displays the current frame and the user can drag the slider to switch to a different frame. */
	private JSlider slider;

	public MoviePlayerExample() {
		setLayout(new BorderLayout());

		JToolBar tools = new JToolBar();

		fileChooser = new JFileChooser();

		fileChooser.addChoosableFileFilter(new VideoFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setSelectedFile(lastDirectory);

		slider = new JSlider();

		// Open file dialog.
		JButton open = new JButton("Open File");
		JButton play = new JButton("Play");
		JButton stop = new JButton("Stop");
		JButton rewind = new JButton("Rewind");
		JButton stepBackward = new JButton("<");
		JButton stepForward = new JButton(">");
		JButton view = new JButton("View");
		JButton resetView = new JButton("ResetView");

		tools.add(open);
		tools.add(play);
		tools.add(stop);
		tools.add(rewind);
		tools.add(stepBackward);
		tools.add(stepForward);
		tools.add(view);
		tools.add(resetView);

		open.addActionListener(new OpenFileSelection());
		play.addActionListener(new PlaySelection());
		stop.addActionListener(new StopSelection());
		rewind.addActionListener(new RewindSelection());
		stepBackward.addActionListener(new StepBackwardSelection());
		stepForward.addActionListener(new StepForwardSelection());
		slider.addChangeListener(new SliderSelection());
		view.addActionListener(new ViewSelection());
		resetView.addActionListener(new ResetViewSelection());

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
		tools.add(speedsPanel);

		JLabel frameNumber = new JLabel("0");
		tools.add(frameNumber);

		add(tools, BorderLayout.NORTH);
		add(slider, BorderLayout.SOUTH);

		openFile("C:\\Users\\Florian\\DatavyuSampleVideo.mp4");

		//openFile("C:\\Users\\Florian\\TurkishManGaitClip_KEATalk.mov");
		//openFile("C:\\Users\\Florian\\NoAudio\\TurkishCrawler_NoAudio.mov");
	}
	
	/**
     * Create the GUI and show it. For thread safety, this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        // Create and set up the window
        JFrame frame = new JFrame("Video Player");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MoviePlayerExample player = new MoviePlayerExample();
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
        Configurator.setRootLevel(Level.INFO);

        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(()->createAndShowGUI());
    }

	private void openFile(String fileName) {
	    try {
            MoviePlayerFrame moviePlayerFrame = new MoviePlayerFrame(fileName);
            movieStreamProviders.add(moviePlayerFrame.getMovieStreamProvider());
        } catch (IOException io) {
            System.err.println(io);
        }
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
        for (MovieStreamProvider movieStreamProvider : movieStreamProviders) {
            try {
                movieStreamProvider.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
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
            for (MovieStreamProvider movieStreamProvider : movieStreamProviders) {
                movieStreamProvider.start();
            }
		}
	}
	
	class StopSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
            for (MovieStreamProvider movieStreamProvider : movieStreamProviders) {
                movieStreamProvider.stop();
            }
		}
	}

	class RewindSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
            for (MovieStreamProvider movieStreamProvider : movieStreamProviders) {
                movieStreamProvider.reset();
            }
		}
	}

	class StepBackwardSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			for (MovieStreamProvider movieStreamProvider : movieStreamProviders) {
				movieStreamProvider.stepBackward();
			}
		}
	}

	class StepForwardSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
            for (MovieStreamProvider movieStreamProvider : movieStreamProviders) {
                movieStreamProvider.stepForward();
            }
		}
	}

	class SliderSelection implements ChangeListener {

		private final static long SYNC_INTERVAL = 500;  // in milliseconds

		private final static long SYNC_DELAY = 0; // in milliseconds

		private final static double SYNC_THRESHOLD = 0.5; // in seconds

        SliderSelection() {
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    double streamTime = SliderStreamListener.toStreamTime(slider.getValue());
                    for (MovieStreamProvider movieStreamProvider : movieStreamProviders) {
                        if (Math.abs(streamTime - movieStreamProvider.getCurrentTime()) >= SYNC_THRESHOLD) {
                            logger.info("The slider time is: " + streamTime + " seconds and the stream time is: "
                                    + movieStreamProvider.getCurrentTime() + " seconds");
                            movieStreamProvider.setCurrentTime(streamTime);
                            movieStreamProvider.startVideoListeners();
                            movieStreamProvider.nextImageFrame();
                        }
                    }
                }
            }, SYNC_DELAY, SYNC_INTERVAL);
        }

		@Override
		public void stateChanged(ChangeEvent e) {
            if (slider.getValueIsAdjusting()) {
                for (MovieStreamProvider movieStreamProvider : movieStreamProviders) {
                    movieStreamProvider.stop();
                }
            }
		}
	}

	class ViewSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
            for (MovieStreamProvider movieStreamProvider : movieStreamProviders) {
                try {
                    // Stop the player before changing the window
                    movieStreamProvider.stop();
                    movieStreamProvider.setView(70, 50, 300, 200);
                } catch (IndexOutOfBoundsException iob) {
                    System.err.println("Could not set view: " + iob.getMessage());
                } finally {
                    movieStreamProvider.start();
                }
            }
		}
	}

	class ResetViewSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
            for (MovieStreamProvider movieStreamProvider : movieStreamProviders) {
                try {
                    // Stop the player before changing the window
                    movieStreamProvider.stop();
                    int w = movieStreamProvider.getWidthOfStream();
                    int h = movieStreamProvider.getHeightOfStream();
                    movieStreamProvider.setView(0, 0, w, h);
                } catch (IndexOutOfBoundsException iob) {
                    System.err.println("Could not reset view: " + iob.getMessage());
                    iob.printStackTrace();
                } finally {
                    movieStreamProvider.start();
                }
            }
		}
	}

    private class MoviePlayerFrame {
        final JFrame frame;
        MovieStreamProvider movieStreamProvider;

        MoviePlayerFrame(String movieFileName) throws IOException {
            this(movieFileName, "0.0.0.1");
        }

        MoviePlayerFrame(String movieFileName, String version) throws IOException {
            movieStreamProvider = new MovieStreamProvider();
            final ColorSpace reqColorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
            AudioFormat reqAudioFormat = AudioSoundStreamListener.getNewMonoFormat();
            frame = new JFrame();
            frame.addWindowListener( new WindowAdapter() {
                public void windowClosing(WindowEvent ev) {
                    try {
                        movieStreamProvider.close();
                        movieStreamProviders.remove(movieStreamProvider);
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                    frame.setVisible(false);
                }
            } );
            AudioSoundStreamListener audioListener = new AudioSoundStreamListener(movieStreamProvider);
            VideoStreamListenerContainer displayListener = new VideoStreamListenerContainer(movieStreamProvider, frame,
                    reqColorSpace);

            // Add the audio sound listener
            movieStreamProvider.addAudioStreamListener(audioListener);
            // Add video display
            movieStreamProvider.addVideoStreamListener(displayListener);

            // TODO: This only works if we have only one video; otherwise we need to sync them through one clock
            movieStreamProvider.addVideoStreamListener(new SliderStreamListener(slider, movieStreamProvider));

            // Open the movie stream provider
            movieStreamProvider.open(movieFileName, version, reqColorSpace, reqAudioFormat);
            int width = movieStreamProvider.getWidthOfView();
            int height = movieStreamProvider.getHeightOfView();
            frame.setBounds(0, 0, width, height);
            frame.setVisible(true);
            frame.addComponentListener(new ComponentListener() {
                public void componentResized(ComponentEvent e) {
                    float scale = ((float) frame.getHeight())/movieStreamProvider.getHeightOfView();
//                    displayListener.setScale(scale);
                }

                @Override
                public void componentHidden(ComponentEvent e) { }

                @Override
                public void componentMoved(ComponentEvent e) { }

                @Override
                public void componentShown(ComponentEvent e) { }
            });
        }

        MovieStreamProvider getMovieStreamProvider() {
            return movieStreamProvider;
        }
    }
	
	class SpeedValueSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
			    float newSpeed = Float.parseFloat(e.getActionCommand());
			    for (MovieStreamProvider movieStreamProvider : movieStreamProviders) {
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
	        int val = fileChooser.showDialog(MoviePlayerExample.this, "Open");

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
}
