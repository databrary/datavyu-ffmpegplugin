package org.openshapa.plugins.spectrum.mediatools;

import java.nio.ShortBuffer;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.openshapa.plugins.spectrum.models.StereoAmplitudeData;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IAudioSamplesEvent;


/**
 * Tool for getting time-domain amplitude data.
 */
public final class AmplitudeTool extends MediaToolAdapter {

    private StereoAmplitudeData ampData;

    public AmplitudeTool() {
        ampData = new StereoAmplitudeData();
    }

    public StereoAmplitudeData getData() {
        return ampData;
    }

    @Override public void onAudioSamples(final IAudioSamplesEvent event) {

        // Get the raw audio bytes.
        ShortBuffer buffer = event.getAudioSamples().getByteBuffer()
            .asShortBuffer();

        final int numChannels = event.getAudioSamples().getChannels();

        if (!ampData.isTimeIntervalSet()) {
            ampData.setTimeInterval(event.getTimeStamp(), event.getTimeUnit());
        }

        if (numChannels >= 1) {
            ampData.addDataL(buffer.get(buffer.limit() - numChannels));
        }

        if (numChannels >= 2) {
            ampData.addDataR(buffer.get(buffer.limit() - numChannels + 1));
        }

        // call parent which will pass the audio onto next tool in chain
        super.onAudioSamples(event);
    }

}
