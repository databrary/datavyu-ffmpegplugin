package org.datavyu.plugins.ffmpegplayer.prototypes;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;


public class CheckAudioSupport {
	public static void main(String[] args) {
		AudioFileFormat.Type audioTypes[] = AudioSystem.getAudioFileTypes();
		for (AudioFileFormat.Type type : audioTypes) {
			System.out.println(type);
		}
		Encoding encodings[] = {Encoding.PCM_FLOAT, Encoding.PCM_SIGNED, 
				Encoding.PCM_UNSIGNED};
		float sampleRates[] = {8000f, 11025f, 22050f, 32000f, 44000f, 48000f, 
				64000f, 88000f, 96000f};
		int sampleSizes[] = {8, 16, 32}; // in bits
		int nChannels[] = {1, 2};
		int frameSizes[] = {1, 2}; // in bytes
		
		for (Encoding encoding : encodings) {
			for (float sampleRate : sampleRates) {
				for (int sampleSize : sampleSizes) {
					for (int nChannel : nChannels) {
						for (int frameSize : frameSizes) {
							AudioFormat audioFormat = new AudioFormat(encoding, 
									sampleRate, sampleSize, nChannel, frameSize, 
									(int) sampleRate, false);
							DataLine.Info info = new DataLine.Info(
									SourceDataLine.class, audioFormat);
							if (AudioSystem.isLineSupported(info)) {
								System.out.println(info);
							}
						}
					}
				}
			}
		}
	}
}
