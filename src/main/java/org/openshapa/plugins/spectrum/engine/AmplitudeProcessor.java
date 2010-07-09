package org.openshapa.plugins.spectrum.engine;

import java.io.File;

import javax.swing.SwingWorker;

import org.openshapa.plugins.spectrum.mediatools.AmplitudeTool;
import org.openshapa.plugins.spectrum.models.StereoAmplitudeData;
import org.openshapa.plugins.spectrum.swing.AmplitudeTrack;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;


/**
 * Worker thread for processing audio amplitude data. Only processes audio
 * channels one and two.
 */
public final class AmplitudeProcessor
    extends SwingWorker<StereoAmplitudeData, Void> {

    private static final Logger LOGGER = UserMetrix.getLogger(
            AmplitudeProcessor.class);

    /** Media file to process. */
    private File mediaFile;

    /** Track to send processed data to. */
    private AmplitudeTrack track;

    /**
     * Creates a new worker thread.
     *
     * @param mediaFile
     *            Media file to process.
     * @param track
     *            Track to send processed data to.
     */
    public AmplitudeProcessor(final File mediaFile,
        final AmplitudeTrack track) {
        this.mediaFile = mediaFile;
        this.track = track;
    }

    @Override protected StereoAmplitudeData doInBackground() throws Exception {

        IMediaReader reader = ToolFactory.makeReader(
                mediaFile.getAbsolutePath());

        AmplitudeTool at = new AmplitudeTool();
        reader.addListener(at);

        // Start reading packets so that the amplitude tool can get the data out
        while (reader.readPacket() == null)
            ;

        at.getData().normalizeL();
        at.getData().normalizeR();

        return at.getData();
    }

    @Override protected void done() {

        try {
            track.setData(get());
            track.repaint();
        } catch (Exception e) {
            LOGGER.error(e);
        }

    }

}
