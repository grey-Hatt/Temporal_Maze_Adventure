package MainGame;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class SoundPlayer {
    private Clip clip;

    public void playLoop(String soundFile) {
        try {
            URL soundURL = getClass().getResource(soundFile);
            if (soundURL == null) {
                System.out.println("Sound file not found: " + soundFile);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Keep looping forever
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException
                | IllegalArgumentException e) {
            // Any audio failure (missing/unsupported audio device, unsupported format, etc.)
            // must never prevent the rest of the UI from being built and shown.
            System.out.println("Background music unavailable, continuing without sound: " + e.getMessage());
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}
