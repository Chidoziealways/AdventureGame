package updater;

import java.io.*;
import java.net.*;

public class UpdateClient {
    public static void updateJar() throws IOException {
        String userHome = System.getProperty("user.home");
        String defaultPath = userHome + "AppData/Roaming/AdventureGame/AdventureGame.jar"; // Default location in user's home directory
        try {
            URL url = new URL("http://adventuregame.infinityfreeapp.com/AdventureGame-1.0-SNAPSHOT.jar");
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpConn.getInputStream();

            File jarFile = new File(defaultPath);
            File parentDir = jarFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs(); // Create directory if it doesn't exist
            }
            FileOutputStream fos = new FileOutputStream(jarFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.close();
            inputStream.close();
            System.out.println("Update downloaded and replaced.");
        } catch (MalformedURLException e) {
            System.out.println("The URL is malformed: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("An I/O error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}