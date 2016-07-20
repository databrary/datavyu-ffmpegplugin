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
		
		AudioFormat audioFormat = new AudioFormat(Encoding.PCM_UNSIGNED, 48000.0f, 8, 1, 1, 48000, false);		
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		System.out.println(info);
		System.out.println("Supported: " + AudioSystem.isLineSupported(info));
	}
}
