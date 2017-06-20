package org.datavyu;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.swing.ButtonGroup;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;


public class MoviePlayer extends JPanel implements WindowListener {
	
	/** Identifier for object serialization */
	private static final long serialVersionUID = 5109839668203738974L;
	
	/** The requested color space */
	private final ColorSpace reqColorSpace = ColorSpace.getInstance(
			ColorSpace.CS_sRGB);
	
	/** The requested audio format */
	private final AudioFormat reqAudioFormat = AudioSound.MONO_FORMAT;
	
	/** The movie stream for this movie player */
	private MovieStreamProvider movieStreamProvider;
	
	/** The start time is used to adjust slider to correct value. */
	private double startTime = 0; 
	
	/** Used to open files to be played. */
	private JFileChooser fileChooser = null;
	
	/** The sign for the play back speed: + = forward and - = backward. */
	private int speedSign = 1;
	
	/** The speed for play back. */
	private float speedValue = 1;
	
	/** The directory last opened is used to initialize the file chooser. */
	private File lastDirectory = new File(System.getProperty("user.home"));

	/** A group of radio buttons controls the play back speed. */
	private JRadioButton quarter;
	private JRadioButton half;
	private JRadioButton one;
	private JRadioButton twice;
	private JRadioButton four;
	
	/** A tuple of radio buttons controls the play back direction. */
	private JRadioButton forward;
	private JRadioButton backward;
	
	/** A label to display the frame number. */
	private JLabel frameNumber;
	
	/** 
	 * A slider displays the current frame and the user can drag the slider
	 * to switch to a different frame. 
	 */
	private JSlider slider;
	
	/**
	 * An encapsulated class that ensures that filters files down to video files
	 * based on known file extensions.
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
			return ext.equals("mp4") || ext.equals("mpg") || ext.equals("h264") 
					|| ext.equals("mov") || ext.equals("wav");
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
			if (playsAtForward1x()) { movieStreamProvider.startAudio(); }
			// Play video
			movieStreamProvider.startVideo();
		}
	}
	
	class StopSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			movieStreamProvider.stop();
		}
	}
	
	class RewindSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			movieStreamProvider.reset();
		}
	}
	
	private boolean isStepping = false;
	
	class StepSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
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
	
	class SliderSelection implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			double sec = ((double)slider.getValue())/1000.0 + startTime;
			if (slider.getValueIsAdjusting()) {
				System.out.println("Seconds: " + sec);
				//player.setTime(sec);
				movieStreamProvider.seek(sec);
				
				// Pull frame that has been blocked and is not part of the new playback.
				//if (player.hasNextFrame()) { player.loadNextFrame(); }
				
				// Can't show all the frames, random seeks. Only show when the user 
				// presses step or play.
				//showNextFrame();
			}
		}
	}
	
	class ViewSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
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
	
	
	class ResetViewSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
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
	
	private void showNextImageFrame() {
        movieStreamProvider.nextImageFrame();
		double time = movieStreamProvider.getCurrentTime();
		slider.setValue((int)(1000*(-startTime+time)));
		frameNumber.setText(Math.round(time*1000.0)/1000.0 + " seconds");
	}	
	
	protected void openFile(String fileName) {
		movieStreamProvider.stop();
		// Assign a new movie file.
		try {
			// The input audio format will be manipulated by the open method!
			AudioFormat input = new AudioFormat(
					reqAudioFormat.getEncoding(), 
					reqAudioFormat.getSampleRate(),
					reqAudioFormat.getSampleSizeInBits(),
					reqAudioFormat.getChannels(),
					reqAudioFormat.getFrameSize(),
					reqAudioFormat.getFrameRate(),
					reqAudioFormat.isBigEndian());
			
			// Open the stream
	        movieStreamProvider.open(fileName, "0.0.1", reqColorSpace, input);
	        // Load and display first frame.
	        //showNextFrame();
	        showNextImageFrame();

	        // Display the start time.
			double timeInSeconds = movieStreamProvider.getCurrentTime();
			frameNumber.setText(Math.round(timeInSeconds*1000.0)/1000.0 
					+ " seconds");
					
			// Set the default play back speed.
			one.setSelected(true);
			forward.setSelected(true);
			speedSign = 1;
			speedValue = 1;
			movieStreamProvider.setSpeed(1);
	        
			// Get information about the video file.
	        int width = movieStreamProvider.getWidthOfStream();
	        int height = movieStreamProvider.getHeightOfStream();
	        double duration = movieStreamProvider.getDuration();
	        
	        // Get the start time for proper placing of the slider.
	        startTime = movieStreamProvider.getStartTime();

	        // Assign a range of 0 to 1 to the slider.
	        slider.setModel(new DefaultBoundedRangeModel(0, 1, 0, 
	        		(int)(1000*duration)));

	        // Set the size for the video frame.
	        setPreferredSize(new Dimension(width+50, height+50));
			repaint();
	        
	        System.out.println("Opened movie " + fileName);
	        System.out.println("width = " + width + ", height = " + height);
	        System.out.println("duration = " + duration + " seconds.");
	        System.out.println("start time = " + startTime + " seconds.");
	        System.out.println("end time = " + movieStreamProvider.getEndTime() 
	        		+ " seconds.");
	        System.out.println("Has video stream  = " 
	        		+ movieStreamProvider.hasVideoStream());
	        System.out.println("Has audio stream = " 
	        		+ movieStreamProvider.hasAudioStream());
	        
		} catch (IOException io) {
			io.printStackTrace();
		}        
	}
	
	private boolean playsAtForward1x() {
		return Math.abs(speedValue - 1.0) <= Math.ulp(1.0) && speedSign == 1 
				&& !isStepping;
	}
	
	private void setSpeed(float speedValue, int speedSign) {
		if (playsAtForward1x()) {
			movieStreamProvider.startAudio();
		} else {
			System.out.println("Stopping audio.");
			movieStreamProvider.stopAudio();
			System.out.println("Audio stopped.");
		}
		movieStreamProvider.setSpeed(speedSign * speedValue);
		System.out.println("Set speed.");
	}
	
	class SpeedValueSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				speedValue = Float.parseFloat(e.getActionCommand());				
			} catch (NumberFormatException ex) {
				System.err.println("Could not parse command: " 
							+ e.getActionCommand());
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
				System.err.println("Could not parse command: " 
									+ e.getActionCommand());
				speedSign = +1;
			}
			setSpeed(speedValue, speedSign);
			// Need to pull two frames as workaround because toggle requires 
			// two frames to revert direction.
			movieStreamProvider.dropImageFrame();
			movieStreamProvider.dropImageFrame();
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
		quarter = new JRadioButton("1/4x");
		half = new JRadioButton("1/2x");
		one = new JRadioButton("1x");
		twice = new JRadioButton("2x");
		four = new JRadioButton("4x");
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
		forward = new JRadioButton("+");
		backward = new JRadioButton("-");
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
		
		frameNumber = new JLabel("0");
		tools.add(frameNumber);
				
		add(tools, BorderLayout.NORTH);
		add(slider, BorderLayout.SOUTH);
		
		movieStreamProvider = new MovieStreamProvider();
		// Add the audio sound listener
		movieStreamProvider.addAudioStreamListener(
				new AudioSoundStreamListener(movieStreamProvider));
		// Add video display
		movieStreamProvider.addVideoStreamListener(
				new VideoDisplayStreamListener(movieStreamProvider, this, 
						BorderLayout.CENTER, reqColorSpace));
		
		openFile("C:\\Users\\Florian\\TurkishManGaitClip_KEATalk.mov");
		//openFile("C:\\Users\\Florian\\NoAudio\\TurkishCrawler_NoAudio.mov");
	}
	
    void addWindowListener(Window w) {
        w.addWindowListener(this);
    }

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		try {
			movieStreamProvider.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	/**
     * Create the GUI and show it. For thread safety, this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        // Create and set up the window
        JFrame frame = new JFrame("Video Player");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
