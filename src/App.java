import javax.swing.SwingUtilities;

import StartGame.SplashScreen;

public class App {
    public static void main(String[] args) {
        // Start the Swing UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.setVisible(true);
        });
    }
}
