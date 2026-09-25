package thirdLevel;

import javax.sound.sampled.*;
import java.net.URL;

/**
 * Simple sound manager. Loads .wav files bundled as classpath resources
 * from the secondLevel package (shared audio assets), so playback works
 * no matter what directory the app is launched from or if run from a JAR.
 * To disable sound effects, set enabled = false.
 */
public class SoundManager {
    public static boolean enabled = true; // set false to mute sound effects
    private static final String ASSET_PATH = "/secondLevel/";

    public static void play(String filename) {
        if (!enabled) return;
        try {
            URL url = SoundManager.class.getResource(ASSET_PATH + filename);
            if (url == null) return;
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
        } catch (Exception ignored) { }
    }
}
