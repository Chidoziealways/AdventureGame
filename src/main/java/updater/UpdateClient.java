package updater;

import java.io.*;
import java.net.*;

public class UpdateClient {
    public static void updateJar() {
        try {
            URL url = new URL("https://raw.githubusercontent.com/Chidoziealways/UpdateServer/main/UpdateServer.jar");
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpConn.getInputStream();

            // Get the current JAR path
            String jarPath = UpdateClient.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File jarFile = new File(jarPath);
            File tempFile = new File(jarFile.getParentFile(), "YourApp_temp.jar");

            // Download the new JAR to a temporary file
            FileOutputStream fos = new FileOutputStream(tempFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.close();
            inputStream.close();

            // Replace the current JAR with the new one
            if (jarFile.delete()) {
                tempFile.renameTo(jarFile);
                System.out.println("Update downloaded and replaced.");
            } else {
                System.out.println("Failed to delete the old JAR file.");
            }
        } catch (MalformedURLException e) {
            System.out.println("The URL is malformed: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException | URISyntaxException e) {
            System.out.println("An I/O error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
