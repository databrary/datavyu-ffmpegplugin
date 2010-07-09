package org.openshapa.plugins.spectrum.engine;

import java.io.File;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.openshapa.plugins.spectrum.mediatools.AmplitudeTool;
import org.openshapa.plugins.spectrum.models.StereoAmplitudeData;
import org.openshapa.plugins.spectrum.swing.AmplitudeTrack;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;


public final class AmplitudeProcessor
    extends SwingWorker<StereoAmplitudeData, Void> {

    private File mediaFile;
    private AmplitudeTrack track;

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

        // System.out.println("Interval: " + at.getData().getTimeInterval());
        // System.out.println("LSize: " + at.getData().sizeL());
        // System.out.println("RSize: " + at.getData().sizeR());

        at.getData().normalizeL();
        at.getData().normalizeR();

        return at.getData();
    }

    @Override protected void done() {

        try {
            track.setData(get());
            track.repaint();
        } catch (Exception e) {
            System.err.println(e);
        }

    }

}
