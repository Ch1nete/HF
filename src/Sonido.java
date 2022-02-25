

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.Mixer.Info;

public class Sonido {
    public Sonido(String file) throws LineUnavailableException, IOException, UnsupportedAudioFileException, InterruptedException {
        Clip sonido = AudioSystem.getClip((Info)null);
        URL url = this.getClass().getResource(file);
        AudioInputStream ais = AudioSystem.getAudioInputStream(url);
        sonido.open(ais);
        sonido.start();
    }
}
