/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openshapa.plugins.spectrum;

import java.io.File;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import org.gstreamer.Bin;
import org.gstreamer.Bus;

import static org.apache.commons.io.FilenameUtils.isExtension;

import static org.gstreamer.Element.linkMany;

import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.GhostPad;
import org.gstreamer.Gst;
import org.gstreamer.Message;
import org.gstreamer.Pad;
import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.gstreamer.Structure;
import org.gstreamer.ValueList;

import org.gstreamer.elements.DecodeBin;
import org.gstreamer.elements.PlayBin;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;


/**
 * Utility functions.
 */
public final class SpectrumUtils {

    private static final Logger LOGGER = UserMetrix.getLogger(
            SpectrumUtils.class);

    static {
        Gst.init();
    }

    /**
     * Get media file duration.
     *
     * @param file
     *            Media file.
     * @return Duration in milliseconds.
     */
    public static long getDuration(final File file) {
        final PlayBin playBin = new PlayBin("DurationFinder");
        playBin.setAudioSink(ElementFactory.make("fakesink", "audiosink"));
        playBin.setVideoSink(ElementFactory.make("fakesink", "videosink"));
        playBin.setInputFile(file);

        long result;

        playBin.pause();
        playBin.getState();

        result = playBin.queryDuration(MILLISECONDS);

        playBin.setState(State.NULL);
        playBin.dispose();

        return result;
    }

    /**
     * Calculates the number of audio channels using the gstreamer level plugin.
     *
     * @param file
     *            Media file.
     * @return number of channels.
     */
    public static int getNumChannels(final File file) {
        final Pipeline pipeline = new Pipeline("ChannelsFinder");

        DecodeBin decodeBin = new DecodeBin("DecoderBin");

        Element source = ElementFactory.make("filesrc", "src");
        source.set("location", file.getAbsolutePath());

        pipeline.addMany(source, decodeBin);

        if (!linkMany(source, decodeBin)) {
            LOGGER.error("Failed to link source to decodebin.");
        }

        final Bin audioBin = new Bin("AudioBin");


        Element level = ElementFactory.make("level", "level");
        level.set("message", true);

        Element audioSink = ElementFactory.make("fakesink", "sink");
        audioSink.set("sync", false);

        audioBin.addMany(level, audioSink);

        if (!linkMany(level, audioSink)) {
            LOGGER.error("Failed to link level to audiosink.");
        }

        audioBin.addPad(new GhostPad("sink", level.getStaticPad("sink")));

        pipeline.add(audioBin);

        // Video handling bin
        final Bin videoBin = new Bin("Video bin");
        Element videoOutput = ElementFactory.make("fakesink", "videosink");
        videoBin.add(videoOutput);
        videoBin.addPad(new GhostPad("sink", videoOutput.getStaticPad("sink")));

        // Only add the video handling bin if we are not dealing with audio
        // files.
        if (!(isExtension(file.getAbsolutePath(),
                        new String[] { "mp3", "wav", "ogg" }))) {
            pipeline.add(videoBin);
        }

        decodeBin.connect(new DecodeBin.NEW_DECODED_PAD() {

                @Override public void newDecodedPad(final Element element,
                    final Pad pad, final boolean last) {

                    if (pad.isLinked()) {
                        return;
                    }

                    Caps caps = pad.getCaps();
                    Structure struct = caps.getStructure(0);

                    if (struct.getName().startsWith("audio/")) {
                        pad.link(audioBin.getStaticPad("sink"));
                    } else if (struct.getName().startsWith("video/")) {
                        pad.link(videoBin.getStaticPad("sink"));
                    }
                }
            });

        final MutableInteger result = new MutableInteger();

        Bus bus = pipeline.getBus();
        bus.connect(new Bus.MESSAGE() {
                @Override public void busMessage(final Bus bus,
                    final Message message) {
                    Structure msgStruct = message.getStructure();
                    String name = msgStruct.getName();

                    if ("level".equals(name)) {
                        ValueList vl = msgStruct.getValueList("rms");
                        result.number = vl.getSize();

                        synchronized (result) {
                            result.notifyAll();
                        }
                    }
                }
            });

        pipeline.setState(State.PLAYING);

        synchronized (result) {

            try {
                result.wait();
            } catch (InterruptedException e) {
            }
        }

        pipeline.stop();
        pipeline.setState(State.NULL);
        pipeline.dispose();

        return result.number;
    }

    /**
     * Calculates bin values to display and returns their indices. The
     * values are chosen logarithmically. Exponential regression
     * is performed to calculate the chosen index values.
     *
     * @param totalBins
     *            Total number of bin values.
     * @param numIndices
     *            Number of bin values to pick.
     * @return Indices associated with the picked bin values.
     */
    public static int[] findIndices(final int totalBins, final int numIndices) {

        if (numIndices <= 1) {
            throw new IllegalArgumentException("numIndices must be > 1");
        }

        // Exponential regression.
        final double a = Math.pow(totalBins, 1D / (numIndices - 1));

        // Get numIndices frequencies that are log spaced.
        int[] indices = new int[numIndices];

        indices[0] = 0;

        for (int i = 1; i < numIndices; i++) {
            indices[i] = (int) Math.pow(a, i);

            if (indices[i] <= indices[i - 1]) {
                indices[i] = indices[i - 1] + 1;
            }

            if (indices[i] >= totalBins) {
                indices[i] = totalBins - 1;
            }
        }

        return indices;
    }

    /** Helper class for storing intermediate integer results. */
    private static final class MutableInteger {
        int number;
    }

}
