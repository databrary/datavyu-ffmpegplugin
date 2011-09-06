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
package org.openshapa.plugins.spectrum.engine;

import java.io.File;

import org.openshapa.plugins.spectrum.SpectrumPlugin;
import org.openshapa.plugins.spectrum.models.AmplitudeBlock;
import org.openshapa.plugins.spectrum.models.ProcessorConstants;
import org.openshapa.plugins.spectrum.models.StereoData;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;


/**
 * External C process implementation of an amplitude processor.
 * Only processes audio channels one and two. Assumes 16-bit audio.
 */
final class ExternalAmplitudeProcessor extends AmplitudeProcessor {

    ExternalAmplitudeProcessor(final File mediaFile, final int numChannels,
        final Progress progressHandler) {
        super(mediaFile, numChannels, progressHandler);
    }

    @Override protected void setup() {
    }

    @Override protected StereoData doInBackground() throws Exception {
        File addon = SpectrumPlugin.getAddonFile();

        if (addon == null) {

            // Undefined addon.
            return null;
        }

        // Set up our external process.
        long start = NANOSECONDS.convert(data.getDataTimeStart(),
                data.getDataTimeUnit());
        long end = NANOSECONDS.convert(data.getDataTimeEnd(),
                data.getDataTimeUnit());
        ProcessBuilder pb = new ProcessBuilder(addon.getAbsolutePath(), "-f",
                mediaFile.getAbsolutePath(), "-c",
                Integer.toString(numChannels), "-p",
                Integer.toString(ProcessorConstants.NUM_POINTS), "-s",
                Long.toString(start), "-e", Long.toString(end));

        Process audioPoints = pb.start();
        LineIterator it = IOUtils.lineIterator(audioPoints.getInputStream(),
                "UTF-8");

        try {
            int lineCount = 0;

            while (it.hasNext()) {

                if (isCancelled()) {
                    audioPoints.destroy();

                    return null;
                }

                String line = it.nextLine();
                String[] strData = line.split(";", 3);

                if (strData.length != 3) {

                    // Not a valid line.
                    System.err.println(line);

                    continue;
                }

                int left = Integer.parseInt(strData[0]);
                int right = Integer.parseInt(strData[1]);
                long time = Long.parseLong(strData[2]);
                time = data.getDataTimeUnit().convert(time, NANOSECONDS);

                data.addData(left, right, time);
                lineCount++;

                internalHandler.overallProgress(lineCount
                    / (double) ProcessorConstants.NUM_POINTS);
            }
        } finally {
            LineIterator.closeQuietly(it);

            int retVal = audioPoints.exitValue();

            if (retVal != 0) {

                // Process ended abnormally.
                return null;
            }
        }

        if (isCancelled()) {
            return null;
        }

        // Normalize any blocks that haven't been normalized.
        for (AmplitudeBlock block : data.getDataBlocks()) {

            if (!block.isNormalized()) {
                block.normalize();
            }
        }

        return data;
    }

    /**
     * Update the track data.
     *
     * @see javax.swing.SwingWorker#done()
     */
    @Override protected void done() {

        try {

            StereoData result = get();

            if (result != null) {
                progressHandler.overallProgress(1);
                progressHandler.allDone(result);
            }

        } catch (Exception e) {
            // Do not log; the exception that is generated is normal
            // (thread interruptions and subsequently, task cancellation.).
        } finally {

            // Have to explicitly delete all refs or we leak memory because
            // JVM is still holding onto these threads.
            data = null;

            progressHandler = null;
        }

    }

}
