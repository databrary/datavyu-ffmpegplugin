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
import javax.swing.filechooser.FileFilter;



public class VideoPlayer extends JPanel implements WindowListener {
	private boolean playing = false;
	private PlayImageFromVideo player = null;
	private JFileChooser fileChooser = null;
	private int speedSign = 1;
	private float speedValue = 1;
	private File lastDirectory = new File(System.getProperty("user.home"));
	
	private JRadioButton quarter;
	private JRadioButton half;
	private JRadioButton one;
	private JRadioButton twice;
	private JRadioButton four;
	
	private JRadioButton forward;
	private JRadioButton backward;
	
	private JLabel frameNumber;
	private JSlider slider;
	
	protected float getSpeed() { return speedSign*speedValue; }

	private static final long serialVersionUID = -5139487296977249036L;
	
	protected void showNextFrame() {
		player.getNextFrame();
		double time = player.getMovieTimeInSeconds();
		slider.setValue((int)(1000*time));
		//frameNumber.setText((int)time + " sec");
		//long timeInStreamUnits = player.getMoveTimeInStreamUnits();
		long timeInFrames = player.getMovieTimeInFrames();
		frameNumber.setText(timeInFrames + " frame");
		player.repaint();		
	}
	
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
			new PlayerThread().start();
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
			// rewind to start
			player.setTimeInSeconds(0);
		}
	}
	
	class StepSelection implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			showNextFrame();
		}
	}
	
	protected void openFile(String fileName) {
        player.setMovie(fileName);
        // Load first frame.
		player.getNextFrame();
		
		// Set default speed.
		one.setSelected(true);
		forward.setSelected(true);
		speedSign = 1;
		speedValue = 1;
		player.setPlaybackSpeed(1);
        
        int width = player.getMovieWidth();
        int height = player.getMovieHeight();
        double duration = player.getMovieDuration();
        slider.setModel(new DefaultBoundedRangeModel(0, 1, 0, (int)(1000*duration)));
        
        //player.setMinimumSize(new Dimension(width,height));
        setMinimumSize(new Dimension(width+50, height+150));
		repaint();
        
        System.out.println("Opened movie " + fileName);
        System.out.println("width = " + width + ", height = " + height);
        System.out.println("duration = " + duration + " seconds.");
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
	        
	        // Reset the file chooser for the next time it's shown.
	        fileChooser.setSelectedFile(lastDirectory);
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
			player.setPlaybackSpeed(getSpeed());
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
			player.setPlaybackSpeed(getSpeed());
			System.out.println("Set speed to: " + getSpeed());
		}
	}
	
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
			return ext.equals("mp4") || ext.equals("mpg") || ext.equals("h264") || ext.equals("mov");
		}
		
		@Override
		public String getDescription() {
			return "mp4, mpg, h264, mov";
		}
	}
	
	
	public VideoPlayer() {
		setLayout(new BorderLayout());
		
		JToolBar tools = new JToolBar();
		
		player = new PlayImageFromVideo();
		
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
		player.releaseMovie();
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
