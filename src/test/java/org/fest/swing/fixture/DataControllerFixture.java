package org.fest.swing.fixture;

import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.fest.swing.core.Robot;
import org.openshapa.util.UIUtils;
import org.openshapa.views.DataControllerV;
import org.openshapa.views.continuous.DataViewer;


/**
 * Fixture for OpenSHAPA DataController.
 */
public class DataControllerFixture extends DialogFixture {

    /**
     * Constructor.
     * @param robot main frame fixture robot
     * @param target data controller class
     */
    public DataControllerFixture(final Robot robot,
        final DataControllerV target) {
        super(robot, target);
    }

    /**
     * Current time.
     * @return String of currentTime.
     */
    public final String getCurrentTime() {
        return UIUtils.getInnerTextFromHTML(new JLabelFixture(robot,
            findByName("timestampLabel", JLabel.class)).text());
    }

    /**
    * Go back time.
    * @return String of goBackTime.
    */
    public final String getGoBackTime() {
        return new JTextComponentFixture(robot,
                findByName("goBackTextField", JTextField.class)).text();
    }

    /**
    * Set go back time.
    */
    public final void setGoBackTime(String value) {
        new JTextComponentFixture(robot,
            findByName("goBackTextField", JTextField.class)).selectAll()
            .enterText(value);
    }

    /**
     * Press set offset button.
     */
    public final void pressSetOffsetButton() {
        new JButtonFixture(robot,
            findByName("setCellOffsetButton", JButton.class)).click();
    }

    /**
     * Press find button.
     */
    public final void pressFindButton() {
        new JButtonFixture(robot,
            findByName("findButton", JButton.class)).click();
    }

    /**
    * Press Snap Region button.
    */
    public final void pressSnapRegionButton() {
        new JButtonFixture(robot,
            findByName("findButton", JButton.class)).click();
    }

    /**
     * Press set cell onset button.
     */
    public final void pressSetCellOnsetButton() {
        new JButtonFixture(robot,
            findByName("setCellOnsetButton", JButton.class)).click();
    }

    /**
     * Press set cell offset button.
     */
    public final void pressSetCellOffsetButton() {
        new JButtonFixture(robot,
            findByName("setCellOffsetButton", JButton.class)).click();
    }

    /**
     * Press rewind button.
     */
    public final void pressRewindButton() {
        new JButtonFixture(robot,
            findByName("rewindButton", JButton.class)).click();
    }

    /**
    * Press go back button.
    */
    public final void pressGoBackButton() {
        new JButtonFixture(robot,
            findByName("goBackButton", JButton.class)).click();
    }

    /**
     * Press fast forward button.
     */
    public final void pressFastForwardButton() {
        new JButtonFixture(robot,
            findByName("forwardButton", JButton.class)).click();
    }

    /**
     * Press fast forward button.
     */
    public final void pressPlayButton() {
        new JButtonFixture(robot,
            findByName("playButton", JButton.class)).click();
    }

    /**
     * Press shuttle back button.
     */
    public final void pressShuttleBackButton() {
        new JButtonFixture(robot,
            findByName("shuttleBackButton", JButton.class)).click();
    }

    /**
     * Press stop button.
     */
    public final void pressStopButton() {
        new JButtonFixture(robot,
            findByName("stopButton", JButton.class)).click();
    }

    /**
     * Press shuttle forward button.
     */
    public final void pressShuttleForwardButton() {
        new JButtonFixture(robot,
            findByName("shuttleForwardButton", JButton.class)).click();
    }

    /**
     * Press jog back button.
     */
    public final void pressJogBackButton() {
        new JButtonFixture(robot,
            findByName("jogBackButton", JButton.class)).click();
    }

    /**
     * Press jog forward button.
     */
    public final void pressJogForwardButton() {
        new JButtonFixture(robot,
            findByName("jogForwardButton", JButton.class)).click();
    }

    /**
     * Press pause button.
     */
    public final void pressPauseButton() {
        new JButtonFixture(robot,
            findByName("pauseButton", JButton.class)).click();
    }

    /**
     * Press create new cell and set onset.
     */
    public final void pressCreateNewCellWithOnsetButton() {
        new JButtonFixture(robot,
            findByName("newCellAndOnsetButton", JButton.class)).click();
    }

    /**
     * Press create new cell and set onset.
     */
    public final void pressSetNewCellOffsetButton() {
        new JButtonFixture(robot,
            findByName("newCellOffsetButton", JButton.class)).click();
    }

    /**
     * Press create new cell button.
     */
    public final void pressCreateNewCellButton() {
        new JButtonFixture(robot,
            findByName("createNewCellButton", JButton.class)).click();
    }

    /**
     * Press Show Tracks button.
     */
    public final void pressShowTracksButton() {
        new JButtonFixture(robot,
            findByName("showTracksButton", JButton.class)).click();
    }

    /**
     * Press Shift + Find Button.
     */
    public final void pressShiftFindButton() {
        robot.pressModifiers(KeyEvent.SHIFT_MASK);
        pressFindButton();
        robot.releaseModifiers(KeyEvent.SHIFT_MASK);
    }

    /**
     * Returns findOnset time.
     * @return String of find onset time.
     */
    public final String getFindOnset() {
        return new JTextComponentFixture(robot,
                findByName("findOnsetLabel", JTextField.class)).text();
    }

    /**
    * Returns findOnset time.
    * @return String of find onset time.
    */
    public final void setFindOnset(String value) {
        JTextComponentFixture findOnset = new JTextComponentFixture(robot,
                findByName("findOnsetLabel", JTextField.class));
        findOnset.selectAll().enterText(value);
    }

    /**
     * Returns findOffset time.
     * @return String of find offset time.
     */
    public final String getFindOffset() {
        return new JTextComponentFixture(robot,
                findByName("findOffsetLabel", JTextField.class)).text();
    }

    /**
     * Returns speed label.
     * @return String from speed label
     */
    public final String getSpeed() {
        return new JLabelFixture(robot,
                findByName("lblSpeed", JLabel.class)).text();
    }

    /**
     * Returns set of all dataviewers.
     * @return Set<DataViewers> dataviewers
     */
    public final Set<DataViewer> getDataViewers() {
        return ((DataControllerV) target).getDataViewers();
    }

    /**
     * Track mixer controller contains all track related components.
     * @return The track mixer controller
     */
    public final MixerControllerFixture getTrackMixerController() {
        return new MixerControllerFixture(robot,
                ((DataControllerV) target).getMixerController());
    }


    /**
     * @return all videos in the data controller.
     */
    public final ArrayList<DialogFixture> getVideoWindows() {
        ArrayList<DialogFixture> vidWindows = new ArrayList<DialogFixture>();
        Iterator it = getDataViewers().iterator();

        while (it.hasNext()) {
            JDialog vid = ((JDialog) it.next());
            vidWindows.add(new DialogFixture(robot, vid));
        }

        return vidWindows;
    }
}
