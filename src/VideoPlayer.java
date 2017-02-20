import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

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



public class VideoPlayer extends JPanel implements WindowListener {
	
	private static final long serialVersionUID = -5139487296977249036L;
	
	/** The start time is used to adjust slider to correct value. */
	private double startTime = 0; 
	
	/** Used as indicator to play in the display thread. */ 
	private boolean playing = false;
	
	/** The image player provides an interface to ffmpeg and displays images. */
	private ImagePlayer player = null;
	
	/** Used to open files to be played. */
	private JFileChooser fileChooser = null;
	
	/** The sign for the play back speed: + = forward and - = backward. */
	private int speedSign = 1;
	
	/** The speed for play back. */
	private float speedValue = 1;
	
	/** The directory last opened is used to initialize the file chooser. */
	private File lastDirectory = new File(System.getProperty("user.home"));
	
	/** The player thread pulls frames and shows them. */
	private Thread playerThread = null;
	
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
	 * Get the play back speed.
	 * @return Play back speed.
	 */
	protected float getPlaybackSpeed() { return speedSign*speedValue; }

	/**
	 * Show the next frame. Invokes the same method on the image player and 
	 * displays the current time.
	 */
	protected void showNextFrame() {
		player.showNextFrame();
		double time = player.getCurrentTime();
		slider.setValue((int)(1000*(-startTime+time)));
		double timeInSeconds = player.getCurrentTime();
		frameNumber.setText(Math.round(timeInSeconds*1000.0)/1000.0 
				+ " seconds");
		player.repaint();
	}
	
	/**
	 * A private class that encapsulates the player thread.
	 */
	class PlayerThread extends Thread {
		@Override
		public void run() {
			while (playing) {
				showNextFrame();				
			}
		}
	}
	
	class PlaySelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			playing = true;
			playerThread = new PlayerThread();
			playerThread.start();
		}
	}
	
	class StopSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			playing = false;
		}
	}
	
	class RewindSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			player.rewind();				
		}
	}
	
	class StepSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Only pull a frame if it is available.
			if (player.hasNextFrame()) {
				showNextFrame();
			}
		}
	}
	
	class SliderSelection implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			double sec = ((double)slider.getValue())/1000.0 + startTime;
			if (slider.getValueIsAdjusting()) {
				System.out.println("Seconds: " + sec);
				// TODO: Make sure that the slider is not placed behind the end!!
				// At the moment this is possible and the player.getNextFrame() method blocks the UI.
				player.setTime(sec);
				
				// Need to pull one frame because of revert.
				if (player.hasNextFrame()) {
					player.showNextFrame();					
				}
				
				// Can't show all the frames, random seeks. Only show when the user 
				// presses step or play.
				//showNextFrame();
			}
		}
	}
	
	protected void openFile(String fileName) {
		
		// Stop the player.
		playing = false;
		
		// If we had another video playing ensure that the player has stopped 
		// before opening another file.
		while (playerThread != null && playerThread.isAlive()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException ie) {}
		}
	
		// Assign a new movie file.
        player.open(fileName);
        
        // Load and display first frame.
        showNextFrame();

        // Display the start time.
		double timeInSeconds = player.getCurrentTime();
		frameNumber.setText(Math.round(timeInSeconds*1000.0)/1000.0 
				+ " seconds");
				
		// Set the default play back speed.
		one.setSelected(true);
		forward.setSelected(true);
		speedSign = 1;
		speedValue = 1;
		player.setPlaybackSpeed(1);
        
		// Get information about the video file.
        int width = player.getWidth();
        int height = player.getHeight();
        double duration = player.getDuration();
        
        // Get the start time for proper placing of the slider.
        startTime = player.getStartTime();

        // Assign a range of 0 to 1 to the slider.
        slider.setModel(new DefaultBoundedRangeModel(0, 1, 0, (int)(1000*duration)));

        // Set the size for the video frame.
        setMinimumSize(new Dimension(width+50, height+150));
		repaint();
        
        System.out.println("Opened movie " + fileName);
        System.out.println("width = " + width + ", height = " + height);
        System.out.println("duration = " + duration + " seconds.");
        System.out.println("start time = " + player.getStartTime() + " seconds.");
        System.out.println("end time = " + player.getEndTime() + " seconds.");
	}
	
	class OpenFileSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			
			// Show the file chooser.
	        int val = fileChooser.showDialog(VideoPlayer.this, "Open");
	        
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
	
	class SpeedValueSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				speedValue = Float.parseFloat(e.getActionCommand());				
			} catch (NumberFormatException ex) {
				System.err.println("Could not parse command: " 
							+ e.getActionCommand());
			}
			player.setPlaybackSpeed(getPlaybackSpeed());
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
			player.setPlaybackSpeed(getPlaybackSpeed());
			System.out.println("Set speed to: " + getPlaybackSpeed());
			
			// Need to pull two frames as workaround because toggle requires 
			// two frames to revert direction.
			if (player.hasNextFrame()) { player.loadNextFrame(); }
			if (player.hasNextFrame()) { player.loadNextFrame(); }
		}
	}
	
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
					|| ext.equals("mov");
		}
		
		@Override
		public String getDescription() {
			return "mp4, mpg, h264, mov";
		}
	}
	
	
	public VideoPlayer() {
		setLayout(new BorderLayout());
		
		JToolBar tools = new JToolBar();
		
		player = new ImagePlayer();
		
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
		
		tools.add(open);
		tools.add(play);
		tools.add(stop);
		tools.add(rewind);
		tools.add(step);
		
		open.addActionListener(new OpenFileSelection());
		play.addActionListener(new PlaySelection());
		stop.addActionListener(new StopSelection());
		rewind.addActionListener(new RewindSelection());
		step.addActionListener(new StepSelection());
		slider.addChangeListener(new SliderSelection());
		
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
		add(player, BorderLayout.CENTER);
		add(slider, BorderLayout.SOUTH);
		
		openFile("C:\\Users\\Florian\\WalkingVideo.mov");
		//openFile("C:\\Users\\Florian\\SleepingBag.MP4");
		//openFile("C:\\Users\\Florian\\TurkishManGaitClip_KEATalk.mov");
	}
	
	
    void addWindowListener(Window w) {
        w.addWindowListener(this);
    }

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		player.release();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}
	
	/**
     * Create the GUI and show it. For thread safety, this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
         //Create and set up the window.
        JFrame frame = new JFrame("Video Player");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        VideoPlayer player = new VideoPlayer();
        player.addWindowListener(frame);
        //Add content to the window.
        frame.add(player, BorderLayout.CENTER);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] args) {
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
         
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
	
}
