package org.openshapa.plugins.spectrum.mediatools;

import java.nio.ShortBuffer;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.IAudioSamplesEvent;


/**
 * Tool for changing playback volume.
 * The code for this is taken from <a href="http
 * ://build.xuggle.com/view/Stable/job/xuggler_jdk5_stable/ws/workingcopy/
 * src/com/xuggle/mediatool/demos/ModifyAudioAndVideo.java">Xuggle demo</a>.
 */
public final class VolumeTool extends MediaToolAdapter {

    /** Amount to adjust the volume by. */
    private double mVolume;

    /**
     * Construct a volume adjustor.
     *
     * @param volume
     *            volume muliplier, values between 0 and 1.
     */
    public VolumeTool(final double volume) {
        mVolume = volume;
    }

    /**
     * Set the volume.
     *
     * @param volume
     *            volume multiplier, values between 0 and 1.
     */
    public void setVolume(final double volume) {
        mVolume = volume;
    }

    @Override public void onAudioSamples(final IAudioSamplesEvent event) {

        // get the raw audio byes and adjust it's value
        ShortBuffer buffer = event.getAudioSamples().getByteBuffer()
            .asShortBuffer();

        for (int i = 0; i < buffer.limit(); ++i) {
            buffer.put(i, (short) (buffer.get(i) * mVolume));
        }

        // call parent which will pass the audio onto next tool in chain
        super.onAudioSamples(event);
    }

}
