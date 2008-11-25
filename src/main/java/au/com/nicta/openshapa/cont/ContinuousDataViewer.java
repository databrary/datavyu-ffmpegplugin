package au.com.nicta.openshapa.cont;

/**
 * Default interface for all Continuous Data Viewers
 * @author FGA
 */
public interface ContinuousDataViewer {
    public void createNewCell();
    public void jogBack();
    public void stop();
    public void jogForward();
    public void shuttleBack();
    public void pause();
    public void shuttleForward();
    public void rewind();
    public void play();
    public void forward();
    public void setCellOffset();
    public void find();
    public void goBack();
    public void setNewCellOnset();
    public void syncCtrl();
    public void sync();
    public void setCellOnset();
} //End of ContinuousDataViewer interface definition
