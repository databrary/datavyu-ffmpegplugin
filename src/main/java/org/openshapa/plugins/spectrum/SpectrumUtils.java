package org.openshapa.plugins.spectrum;

import java.io.File;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.ICodec.Type;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MICROSECONDS;


public final class SpectrumUtils {

    public static long getDuration(final File file) {

        IMediaReader mediaReader = ToolFactory.makeReader(
                file.getAbsolutePath());
        mediaReader.open();

        // Divide by 1000 because the duration is in microseconds.
        // API doesn't give access to the time unit.
        long duration = mediaReader.getContainer().getDuration() / 1000;

        mediaReader.close();

        return duration;
    }

    public static float calculateAudioFPS(final File file) {

        // Create a Xuggler container object
        IContainer container = IContainer.make();

        if (container.open(file.getAbsolutePath(), IContainer.Type.READ, null)
                < 0) {
            container.close();

            // Cannot calculate the FPS.
            return 0;
        }

        // Query how many streams the call to open found
        int numStreams = container.getNumStreams();

        // and iterate through the streams to find the first audio stream
        int audioStreamId = -1;
        IStreamCoder audioCoder = null;

        for (int i = 0; i < numStreams; i++) {

            // Find the stream object
            IStream stream = container.getStream(i);

            // Get the pre-configured decoder that can decode this stream;
            IStreamCoder coder = stream.getStreamCoder();

            if (coder.getCodecType() == Type.CODEC_TYPE_AUDIO) {
                audioStreamId = i;
                audioCoder = coder;

                break;
            }
        }

        // No audio stream.
        if (audioStreamId == -1) {
            container.close();

            return 0;
        }

        // Cannot open the audio decoder.
        if (audioCoder.open() < 0) {
            audioCoder.close();
            container.close();

            return 0;
        }

        IPacket packet = IPacket.make();

        while (container.readNextPacket(packet) >= 0) {

            // Now we have a packet, let's see if it belongs to our audio stream
            if (packet.getStreamIndex() == audioStreamId) {

                /*
                 * We allocate a set of samples with the same number of channels
                 * as the
                 * coder tells us is in this buffer.
                 * We also pass in a buffer size (1024 in our example), although
                 * Xuggler
                 * will probably allocate more space than just the 1024 (it's
                 * not important why).
                 */
                IAudioSamples samples = IAudioSamples.make(1024,
                        audioCoder.getChannels());


                /*
                 * A packet can actually contain multiple sets of samples (or
                 * frames of samples
                 * in audio-decoding speak). So, we may need to call decode
                 * audio multiple
                 * times at different offsets in the packet's data. We capture
                 * that here.
                 */
                int offset = 0;

                /*
                 * Keep going until we've processed all data
                 */
                while (offset < packet.getSize()) {
                    int bytesDecoded = audioCoder.decodeAudio(samples, packet,
                            offset);

                    if (bytesDecoded < 0) {
                        audioCoder.close();
                        container.close();

                        // Cannot decode audio.
                        return 0;
                    }

                    offset += bytesDecoded;

                    /*
                     * Some decoder will consume data in a packet, but will not
                     * be able to construct a full set of samples yet. Therefore
                     * you should always check if you got a complete set of
                     * samples from the decoder.
                     */
                    if (samples.isComplete()) {
                        audioCoder.close();
                        container.close();

                        return (float) ((1000D
                                    / MILLISECONDS.convert(
                                        samples.getTimeStamp(), MICROSECONDS)));
                    }
                }
            }

        }

        return 0;
    }

}
