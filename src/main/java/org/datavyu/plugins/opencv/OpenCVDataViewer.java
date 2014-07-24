package org.datavyu.plugins.opencv;

import nu.pattern.OpenCV;
import org.datavyu.models.db.Datastore;
import org.datavyu.models.id.Identifier;
import org.datavyu.plugins.CustomActions;
import org.datavyu.plugins.CustomActionsAdapter;
import org.datavyu.plugins.DataViewer;
import org.datavyu.plugins.ViewerStateListener;
import org.datavyu.util.DataViewerUtils;
import org.datavyu.views.DataController;
import org.datavyu.views.component.DefaultTrackPainter;
import org.datavyu.views.component.TrackPainter;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;


public class OpenCVDataViewer implements DataViewer {

    private static final float FALLBACK_FRAME_RATE = 24.0f;
    /**
     * Data viewer ID.
     */
    private Identifier id;
    /**
     * Dialog for showing our visualizations.
     */
    private JDialog vlcDialog;
    /**
     * Data viewer offset.
     */
    private long offset;
    /**
     * Data to visualize.
     */
    private File data;
    /**
     * Boolean to keep track of whether or not we are playing
     */
    private boolean playing;
    /**
     * Data viewer state listeners.
     */
    private List<ViewerStateListener> stateListeners;
    /**
     * Action button for demo purposes.
     */
    private JButton sampleButton;
    /**
     * Supported custom actions.
     */
    private CustomActions actions = new CustomActionsAdapter() {
        @Override
        public AbstractButton getActionButton1() {
            return sampleButton;
        }
    };
    /**
     * Surface on which we will display video
     */
    private Canvas videoSurface;
    /**
     * Factory for building our mediaPlayer
     */
    private MediaPlayerFactory mediaPlayerFactory;
    /**
     * The VLC mediaPlayer
     */
    private EmbeddedMediaPlayer mediaPlayer;
    /**
     * How we will handle fullscreen (i.e., not)
     */
    private FullScreenStrategy fullScreenStrategy;
    /**
     * FPS of the video, calculated on launch
     */
    private float fps;
    /**
     * Length of the video, calculated on launch
     */
    private long length;
    /**
     * The last jog position, making sure we are only calling jog once
     * VLC has issues when trying to go to the same spot multiple times
     */
    private long last_position;
    private Thread vlcThread;
    private boolean assumedFPS = false;

    public OpenCVDataViewer(final Frame parent, final boolean modal) {

        playing = false;


        VideoCapture cap = new VideoCapture("C:/Users/jmlin_000/Desktop/video1.avi");


        Mat mat = new Mat();
        JFrame frame = new JFrame();

        while (cap.isOpened()) {
            cap.read(mat);
            BufferedImage img = matToBufferedImage(mat);
            frame.getContentPane().setLayout(new FlowLayout());
            frame.getContentPane().add(new JLabel(new ImageIcon(img)));
//            frame.getContentPane().add(new JLabel(new ImageIcon(img2)));
//            frame.getContentPane().add(new JLabel(new ImageIcon(img3)));
            frame.pack();
            frame.setVisible(true);
        }


    }

    public static void loadLibrary() {
        try {
      /* Prefer loading the installed library. */
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        } catch (final UnsatisfiedLinkError ule) {
      /* Failing that, deploy and run a packaged binary. */

            final OS os = OS.getCurrent();
            final Arch arch = Arch.getCurrent();
            final String location;

            switch (os) {
                case LINUX:
                    switch (arch) {
                        case X86_32:
                            location = "/nu/pattern/opencv/linux/x86_32/libopencv_java249.so";
                            break;
                        case X86_64:
                            location = "/nu/pattern/opencv/linux/x86_64/libopencv_java249.so";
                            break;
                        default:
                            throw new UnsupportedPlatformException(os, arch);
                    }
                    break;
                case OSX:
                    switch (arch) {
                        case X86_64:
                            location = "/nu/pattern/opencv/osx/x86_64/libopencv_java249.dylib";
                            break;
                        default:
                            throw new UnsupportedPlatformException(os, arch);
                    }
                    break;
                case WINDOWS:
                    switch (arch) {
                        case X86_32:
                            location = "/nu/pattern/opencv/windows/x86_32/opencv_java249.dll";
                            break;
                        case X86_64:
                            location = "/nu/pattern/opencv/windows/x86_64/opencv_java249.dll";
                            break;
                        default:
                            throw new UnsupportedPlatformException(os, arch);
                    }
                    break;
                default:
                    throw new UnsupportedPlatformException(os, arch);
            }

            final InputStream binary = OpenCV.class.getResourceAsStream(location);
            final Path destination = new TemporaryDirectory().markDeleteOnExit().getPath().resolve("./" + location).normalize();

            try {
                Files.createDirectories(destination.getParent());
                Files.copy(binary, destination);

                final String originalLibaryPath = System.getProperty("java.library.path");
                System.setProperty("java.library.path", originalLibaryPath + System.getProperty("path.separator") + destination.getParent());

        /* See https://github.com/atduskgreg/opencv-processing/blob/master/src/gab/opencv/OpenCV.java for clarification. */
                final Field systemPathsField = ClassLoader.class.getDeclaredField("sys_paths");
                systemPathsField.setAccessible(true);
                systemPathsField.set(null, null);

                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            } catch (final IOException ioe) {
                throw new IllegalStateException(String.format("Error writing native library to \"%s\".", destination), ioe);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    public static void showResult(Mat img) {
        Imgproc.resize(img, img, new Size(640, 480));
        MatOfByte matOfByte = new MatOfByte();
        Highgui.imencode(".jpg", img, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
            JFrame frame = new JFrame();
            frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage matToBufferedImage(Mat matrix) {
        int cols = matrix.cols();
        int rows = matrix.rows();
        int elemSize = (int) matrix.elemSize();
        byte[] data = new byte[cols * rows * elemSize];
        int type;
        matrix.get(0, 0, data);
        switch (matrix.channels()) {
            case 1:
                type = BufferedImage.TYPE_BYTE_GRAY;
                break;
            case 3:
                type = BufferedImage.TYPE_3BYTE_BGR;
                // bgr to rgb
                byte b;
                for (int i = 0; i < data.length; i = i + 3) {
                    b = data[i];
                    data[i] = data[i + 2];
                    data[i + 2] = b;
                }
                break;
            default:
                return null;
        }
        BufferedImage image2 = new BufferedImage(cols, rows, type);
        image2.getRaster().setDataElements(0, 0, cols, rows, data);
        return image2;
    }

    private void launchEdtTaskNow(Runnable edtTask) {
        if (SwingUtilities.isEventDispatchThread()) {
            edtTask.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(edtTask);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void launchEdtTaskLater(Runnable edtTask) {
        if (SwingUtilities.isEventDispatchThread()) {
            edtTask.run();
        } else {
            try {
                SwingUtilities.invokeLater(edtTask);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public JDialog getParentJDialog() {
        return vlcDialog;
    }

    @Override
    public float getFrameRate() {
        return fps;
    }

    public void setFrameRate(float fpsIn) {
        fps = fpsIn;
        assumedFPS = false;
    }

    @Override
    public float getDetectedFrameRate() {
        return mediaPlayer.getFps();
    }

    @Override
    public Identifier getIdentifier() {
        return id;
    }

    @Override
    public void setIdentifier(final Identifier id) {
        this.id = id;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public void setOffset(final long offset) {
        this.offset = offset;
    }

    @Override
    public TrackPainter getTrackPainter() {
        return new DefaultTrackPainter();
    }

    @Override
    public void setDataViewerVisible(final boolean isVisible) {
        vlcDialog.setVisible(isVisible);
    }

    @Override
    public File getDataFeed() {
        return data;
    }

    @Override
    public void setDataFeed(final File dataFeed) {
        data = dataFeed;
        vlcDialog.setVisible(true);
        vlcDialog.setName(vlcDialog.getName() + "-" + dataFeed.getName());
        mediaPlayer.startMedia(dataFeed.getAbsolutePath());

        // Grab FPS and length

        // Because of the way VLC works, we have to wait for the metadata to become
        // available a short time after we start playing.
        // TODO: reimplement this using the video output event
        try {
            int i = 0;
            while (mediaPlayer.getVideoDimension() == null) {
                if (i > 100)
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

        // Test to see if we should prompt user to convert the video to
        // the ideal format

        // Stop the player. This will rewind whatever
        // frames we just played to get the FPS and length
        mediaPlayer.pause();
        mediaPlayer.setTime(0);

        playing = false;

        if (d != null) {
            vlcDialog.setSize(d);
        }

        // Test to make sure we got the framerate.
        // If we didn't, alert the user that this
        // may not work right.
        if (fps < 1.0) {
            // VLC can't read the framerate for this video for some reason.
            // Set it to the fallback rate so it is still usable for coding.
            fps = FALLBACK_FRAME_RATE;
            /*
            JOptionPane.showMessageDialog(vlcDialog,
                    "Warning: Unable to detect framerate in video.\n"
                            + "This video may not behave properly. "
                            + "Please try converting to H.264.\n\n"
                            + "This can be done under Controller->Convert Videos.\n"
                            + "Setting framerate to " + FALLBACK_FRAME_RATE);
                    */
        }
    }

    @Override
    public long getDuration() {
        return length;
    }

    @Override
    public long getCurrentTime() throws Exception {
        return mediaPlayer.getTime();
    }

    @Override
    public void seekTo(final long position) {
        Runnable edtTask = new Runnable() {
            @Override
            public void run() {

                long current = mediaPlayer.getTime();


                if (!playing) {
                    if (position > 0) {
                        mediaPlayer.setTime(position);
                    } else {
                        mediaPlayer.setTime(0);
                    }
                }
            }
        };

        launchEdtTaskLater(edtTask);
    }

    @Override
    public boolean isPlaying() {
        return playing;
    }

    @Override
    public void stop() {
        Runnable edtTask = new Runnable() {
            @Override
            public void run() {
                if (playing) {
                    mediaPlayer.pause();
                    playing = false;
                }
            }
        };

        launchEdtTaskLater(edtTask);
    }

    @Override
    public void setPlaybackSpeed(final float rate) {
        Runnable edtTask = new Runnable() {
            @Override
            public void run() {
                if (rate < 0) {
                    // VLC cannot play in reverse, so we're going to rely
                    // on the clock to do fake jumping
                    mediaPlayer.setRate(0);
                    if (playing) {
                        mediaPlayer.pause();
                        playing = false;
                    }
                }
                mediaPlayer.setRate(rate);
                mediaPlayer.setTime(mediaPlayer.getTime());
            }
        };
        launchEdtTaskLater(edtTask);
    }

    @Override
    public void play() {
        Runnable edtTask = new Runnable() {
            @Override
            public void run() {
                if (!playing && mediaPlayer.getRate() > 0) {
                    mediaPlayer.play();
                    playing = true;
                }
            }
        };

        launchEdtTaskLater(edtTask);
    }

    @Override
    public void storeSettings(final OutputStream os) {
        try {
            DataViewerUtils.storeDefaults(this, os);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void loadSettings(final InputStream is) {

        try {
            DataViewerUtils.loadDefaults(this, is);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void addViewerStateListener(
            final ViewerStateListener vsl) {

        if (vsl != null) {
            stateListeners.add(vsl);
        }
    }

    @Override
    public void removeViewerStateListener(
            final ViewerStateListener vsl) {

        if (vsl != null) {
            stateListeners.remove(vsl);
        }
    }

    @Override
    public CustomActions getCustomActions() {
        return actions;
    }

    @Override
    public void clearDataFeed() {
        stop();
        videoSurface.setVisible(false);
        vlcDialog.setVisible(false);
        mediaPlayerFactory.release();
    }

    @Override
    public void setDatastore(final Datastore sDB) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setParentController(
            final DataController dataController) {
        // TODO Auto-generated method stub
    }

    public boolean usingAssumedFPS() {
        return assumedFPS;
    }

    static enum OS {
        OSX("^[Mm]ac OS X$"),
        LINUX("^[Ll]inux$"),
        WINDOWS("^[Ww]indows.*");

        private final Set<Pattern> patterns;

        private OS(final String... patterns) {
            this.patterns = new HashSet<Pattern>();

            for (final String pattern : patterns) {
                this.patterns.add(Pattern.compile(pattern));
            }
        }

        public static OS getCurrent() {
            final String osName = System.getProperty("os.name");

            for (final OS os : OS.values()) {
                if (os.is(osName)) {
                    return os;
                }
            }

            throw new UnsupportedOperationException(String.format("Operating system \"%s\" is not supported.", osName));
        }

        private boolean is(final String id) {
            for (final Pattern pattern : patterns) {
                if (pattern.matcher(id).matches()) {
                    return true;
                }
            }
            return false;
        }
    }

    static enum Arch {
        X86_32("i386", "i686"),
        X86_64("amd64", "x86_64");

        private final Set<String> patterns;

        private Arch(final String... patterns) {
            this.patterns = new HashSet<String>(Arrays.asList(patterns));
        }

        public static Arch getCurrent() {
            final String osArch = System.getProperty("os.arch");

            for (final Arch arch : Arch.values()) {
                if (arch.is(osArch)) {
                    return arch;
                }
            }

            throw new UnsupportedOperationException(String.format("Architecture \"%s\" is not supported.", osArch));
        }

        private boolean is(final String id) {
            return patterns.contains(id);
        }
    }

    private static class UnsupportedPlatformException extends RuntimeException {
        private UnsupportedPlatformException(final OS os, final Arch arch) {
            super(String.format("Operating system \"%s\" and architecture \"%s\" are not supported.", os, arch));
        }
    }

    private static class TemporaryDirectory {
        final Path path;

        public TemporaryDirectory() {
            try {
                path = Files.createTempDirectory("");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Path getPath() {
            return path;
        }

        public TemporaryDirectory markDeleteOnExit() {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    delete();
                }
            });

            return this;
        }

        public void delete() {
            if (!Files.exists(path)) {
                return;
            }

            try {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult postVisitDirectory(final Path dir, final IOException e)
                            throws IOException {
                        Files.deleteIfExists(dir);
                        return super.postVisitDirectory(dir, e);
                    }

                    @Override
                    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                            throws IOException {
                        Files.deleteIfExists(file);
                        return super.visitFile(file, attrs);
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    static {
        loadLibrary();
    }

}
