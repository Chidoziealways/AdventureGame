package updater;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URISyntaxException;

public class UpdateClient {
    // Change this to your actual GitHub release URL
    private static final String DOWNLOAD_URL = "https://github.com/Chidoziealways/AdventureGame/releases/download/0.0.1-alpha/AdventureGame-0.0.1-alpha.jar";

    public static void updateJar() {
        try {
            System.out.println("[UpdateClient] Downloading update from GitHub Releases...");

            // Get path to current JAR file
            String jarPath = UpdateClient.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File currentJar = new File(jarPath);
            File tempJar = new File(currentJar.getParentFile(), "temp-update.jar");

            // Open connection to GitHub
            HttpURLConnection conn = (HttpURLConnection) new URL(DOWNLOAD_URL).openConnection();
            conn.setRequestProperty("User-Agent", "UpdateClient");
            InputStream in = conn.getInputStream();

            // Download to temp file
            try (FileOutputStream out = new FileOutputStream(tempJar)) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            }

            // Close the connection
            in.close();

            // Replace the current JAR
            if (currentJar.delete()) {
                if (tempJar.renameTo(currentJar)) {
                    System.out.println("[UpdateClient] Update complete. Restart the game to apply.");
                } else {
                    System.err.println("[UpdateClient] Couldn't rename the temp JAR.");
                }
            } else {
                System.err.println("[UpdateClient] Couldn't delete the original JAR.");
            }

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            System.err.println("[UpdateClient] Update failed: " + e.getMessage());
        }
    }
}
