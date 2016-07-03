import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;


/**
 * Open a wav file and replay the sound in java using a direct data line.
 * @author Florian Raudies
 * @date 06/27/2016
 * Code example from https://www.ntu.edu.sg/home/ehchua/programming/java/J8c_PlayingSound.html
 */

public class PlaySoundFromJava {
	public static void main(String[] args) {
		String fileName = "C:\\Users\\Florian\\TakeKeys.wav";
		SourceDataLine soundLine = null;
		int BUFFER_SIZE = 8*1024;  // 8 KB
		// Set up an audio input stream piped from the sound file.
		try {
			File soundFile = new File(fileName);
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			AudioFormat audioFormat = audioInputStream.getFormat();
			System.out.println("encoding: " + audioFormat.getEncoding());
			System.out.println("sample rate: " + audioFormat.getSampleRate());
			System.out.println("sample size: " + audioFormat.getSampleSizeInBits());
			System.out.println("channels: " + audioFormat.getChannels());
			System.out.println("frame rate: " + audioFormat.getFrameRate());
			System.out.println("frame size: " + audioFormat.getFrameSize());
			System.out.println(audioFormat);
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
			soundLine = (SourceDataLine) AudioSystem.getLine(info);
			soundLine.open(audioFormat);
			soundLine.start();
			int nBytes = 0;
			byte[] sampleData = new byte[BUFFER_SIZE];
			while (nBytes != -1) {
				nBytes = audioInputStream.read(sampleData, 0, sampleData.length);
				if (nBytes > 0) {
					soundLine.write(sampleData, 0, nBytes);
				}
			}
		} catch (UnsupportedAudioFileException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		} finally {
			soundLine.drain();
			soundLine.stop();
			soundLine.close();
		}
	}
}
