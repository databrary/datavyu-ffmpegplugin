package org.datavyu.plugins.ffmpegplayer;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;


public class MoviePlayer extends JPanel implements WindowListener {
	
	/** Identifier for object serialization */
	private static final long serialVersionUID = 5109839668203738974L;

	/** The movie stream for this movie player */
	private java.util.List<MovieStreamProvider> movieStreamProviders = new ArrayList<>();
	
	/** Used to open video files */
	private JFileChooser fileChooser = null;
	
	/** The sign for the play back speed: + = forward and - = backward */
	private int speedSign = 1;
	
	/** The speed for play back */
	private float speedValue = 1;
	
	/** The directory last opened is used to initialize the file chooser. */
	private File lastDirectory = new File(System.getProperty("user.home"));
	
	/** A slider displays the current frame and the user can drag the slider to switch to a different frame. */
	private JSlider slider;
	
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
			// Must set is stepping to false BEFORE calling playsAtForward1x 
			isStepping = false; 
			// Play sound only if we are in forward direction
			if (playsAtForward1x()) {
			    for (MovieStreamProvider movieStreamProvider : movieStreamProviders) {
                    movieStreamProvider.startAudio();
                }
			}
			// Play video
            for (MovieStreamProvider movieStreamProvider : movieStreamProviders) {
                movieStreamProvider.startVideo();
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
	
	/** Set to true if this player is in stepping mode */
	private boolean isStepping = false;
	
	class StepSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
            for (MovieStreamProvider movieStreamProvider : movieStreamProviders) {
                if (!isStepping) {
                    // Set natively no sound (otherwise the audio buffer will block the video stream)
                    movieStreamProvider.setPlaySound(false);
                    // Stops all stream providers
                    movieStreamProvider.stop();
                    // Enables stepping to display the frames without starting the video thread
                    movieStreamProvider.startVideoListeners();
                    // We are stepping, set isStepping
                    isStepping = true;
                }
                movieStreamProvider.nextImageFrame();
            }
		}
	}
	
	
	/** Time of the last update */
	private static double lastUpdateTime = System.currentTimeMillis();
	
	class SliderSelection implements ChangeListener {
		
		private final static double DELTA_TIME = 1000;
		
		@Override
		public void stateChanged(ChangeEvent e) {
			if (slider.getValueIsAdjusting()) {
				double sec = ((double)slider.getValue())/1000.0;
				double currentTime = System.currentTimeMillis();
				System.out.println("Current time in milli seconds: " + currentTime);
				
				// Can't show all the frames, random seeks
				if (currentTime - lastUpdateTime > DELTA_TIME) {
					System.out.println("Repaint with " 
							+ "lastUpdateTime = " + lastUpdateTime 
							+ ", currentTime = " + currentTime
							+ ", diffTime = " + (currentTime - lastUpdateTime));
                    for (MovieStreamProvider movieStreamProvider : movieStreamProviders) {
                        movieStreamProvider.seek(sec);
                        movieStreamProvider.dropImageFrame();
                        movieStreamProvider.startVideoListeners();
                        movieStreamProvider.nextImageFrame();
                    }
					// TODO: lastUpdateTime does not seem to be updated!
					lastUpdateTime = currentTime;
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
        MovieStreamProvider movieStreamProvider;
        final Frame frame;

        MoviePlayerFrame(String movieFileName) throws IOException {
            this(movieFileName, "0.0.0.1");
        }

        MoviePlayerFrame(String movieFileName, String version) throws IOException {
            movieStreamProvider = new MovieStreamProvider();
            final ColorSpace reqColorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
            AudioFormat reqAudioFormat = AudioSound.getNewMonoFormat();
            frame = new Frame();
            frame.addWindowListener( new WindowAdapter() {
                public void windowClosing(WindowEvent ev) {
                    try {
                        movieStreamProvider.close();
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                    frame.setVisible(false);
                }
            } );
            // Add the audio sound listener
            movieStreamProvider.addAudioStreamListener(new AudioSoundStreamListener(movieStreamProvider));
            // Add video display
            movieStreamProvider.addVideoStreamListener(new VideoDisplayStreamListener(movieStreamProvider, frame,
                    reqColorSpace));
            // Open the movie stream provider
            movieStreamProvider.open(movieFileName, version, reqColorSpace, reqAudioFormat);
            int width = movieStreamProvider.getWidthOfView();
            int height = movieStreamProvider.getHeightOfView();
            frame.setBounds(0, 0, width, height);
            frame.setVisible(true);
        }

        MovieStreamProvider getMovieStreamProvider() {
            return movieStreamProvider;
        }
    }
	
	private void openFile(String fileName) {
	    try {
            MoviePlayerFrame moviePlayerFrame = new MoviePlayerFrame(fileName);
            movieStreamProviders.add(moviePlayerFrame.getMovieStreamProvider());
        } catch (IOException io) {
            System.err.println(io);
        }
	}
	
	/**
	 * Returns true if values are set for the video to be played back at 1x 
	 * speed.
	 *  
	 * @return True if we play forward at 1x; otherwise false.
	 */
	private boolean playsAtForward1x() {
		return Math.abs(speedValue - 1.0) <= Math.ulp(1.0) && speedSign == 1 && !isStepping;
	}
	
	/**
	 * Set the play back speed for this movie player. Notice that depending on
	 * the play back speed this may stop the audio play back.
	 * 
	 * @param speedValue The speed value for the play back speed.
	 * @param speedSign The sign for the play back speed.
	 */
	private void setSpeed(float speedValue, int speedSign) {
		for (MovieStreamProvider movieStreamProvider : movieStreamProviders) {
            // Need to set speed first so that the reverse is set correctly!!!
            movieStreamProvider.setSpeed(speedSign * speedValue);

            // Then we can start/stop the audio
            if (playsAtForward1x()) {
                movieStreamProvider.startAudio();
            } else {
                movieStreamProvider.stopAudio();
            }
        }
	}
	
	class SpeedValueSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				speedValue = Float.parseFloat(e.getActionCommand());				
			} catch (NumberFormatException ex) {
				System.err.println("Could not parse command: " + e.getActionCommand());
			}
			setSpeed(speedValue, speedSign);
		}
	}
	
	class SpeedSignSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "+":
				speedSign = +1;
				break;
			case "-":
				speedSign = -1;
				break;
			default:
				System.err.println("Could not parse command: " + e.getActionCommand());
				speedSign = +1;
			}
			
			setSpeed(speedValue, speedSign);
			
			// Need to pull two frames as workaround because toggle requires 
			// two frames to revert direction.
            for (MovieStreamProvider movieStreamProvider : movieStreamProviders) {
                movieStreamProvider.dropImageFrame();
                movieStreamProvider.dropImageFrame();
            }
		}
	}
	
	class OpenFileSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			
			// Show the file chooser.
	        int val = fileChooser.showDialog(MoviePlayer.this, "Open");
	        
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
	
	public MoviePlayer() {
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
		JButton step = new JButton("Step");
		JButton view = new JButton("View");
		JButton resetView = new JButton("ResetView");
		
		tools.add(open);
		tools.add(play);
		tools.add(stop);
		tools.add(rewind);
		tools.add(step);
		tools.add(view);
		tools.add(resetView);
		
		open.addActionListener(new OpenFileSelection());
		play.addActionListener(new PlaySelection());
		stop.addActionListener(new StopSelection());
		rewind.addActionListener(new RewindSelection());
		step.addActionListener(new StepSelection());
		slider.addChangeListener(new SliderSelection());
		view.addActionListener(new ViewSelection());
		resetView.addActionListener(new ResetViewSelection());
		
		// Speed selection.
		JRadioButton quarter = new JRadioButton("1/4x");
		JRadioButton half = new JRadioButton("1/2x");
		JRadioButton one = new JRadioButton("1x");
		JRadioButton twice = new JRadioButton("2x");
		JRadioButton four = new JRadioButton("4x");
		// Set default.
		one.setSelected(true);
		
		// Add action commands.
		SpeedValueSelection speedValueSelect = new SpeedValueSelection();
		quarter.setActionCommand("0.25");
		half.setActionCommand("0.5");
		one.setActionCommand("1.0");
		twice.setActionCommand("2.0");
		four.setActionCommand("4.0");
		
		quarter.addActionListener(speedValueSelect);
		half.addActionListener(speedValueSelect);
		one.addActionListener(speedValueSelect);
		twice.addActionListener(speedValueSelect);
		four.addActionListener(speedValueSelect);		
		
		// Add radio buttons to a group for mutual exclusion.
		ButtonGroup speedsGroup = new ButtonGroup();
		speedsGroup.add(quarter);
		speedsGroup.add(half);
		speedsGroup.add(one);
		speedsGroup.add(twice);
		speedsGroup.add(four);
		
		
		// Add radio buttons to a panel for display.
		JPanel speedsPanel = new JPanel(new GridLayout(1, 0));
		speedsPanel.add(quarter);
		speedsPanel.add(half);
		speedsPanel.add(one);
		speedsPanel.add(twice);
		speedsPanel.add(four);
		tools.add(new JLabel("Speed:"));
		tools.add(speedsPanel);
		
		// Direction selection.
		ButtonGroup directionGroup = new ButtonGroup();
		JRadioButton forward = new JRadioButton("+");
		JRadioButton backward = new JRadioButton("-");
		// set default.
		forward.setSelected(true);
		
		directionGroup.add(forward);
		directionGroup.add(backward);
		
		SpeedSignSelection speedSignSelect = new SpeedSignSelection();
		forward.setActionCommand("+");
		backward.setActionCommand("-");
		forward.addActionListener(speedSignSelect);
		backward.addActionListener(speedSignSelect);		
		
		JPanel directionPanel = new JPanel(new GridLayout(1, 0));
		directionPanel.add(forward);
		directionPanel.add(backward);
		
		
		tools.add(new JLabel("Direction:"));
		tools.add(directionPanel);

		JLabel frameNumber = new JLabel("0");
		tools.add(frameNumber);
				
		add(tools, BorderLayout.NORTH);
		add(slider, BorderLayout.SOUTH);

		openFile("C:\\Users\\Florian\\DatavyuSampleVideo.mp4");
		//openFile("C:\\Users\\Florian\\TurkishManGaitClip_KEATalk.mov");
		//openFile("C:\\Users\\Florian\\NoAudio\\TurkishCrawler_NoAudio.mov");
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
     * Create the GUI and show it. For thread safety, this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        // Create and set up the window
        JFrame frame = new JFrame("Video Player");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MoviePlayer player = new MoviePlayer();
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
         
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(()->createAndShowGUI());
    }
}
