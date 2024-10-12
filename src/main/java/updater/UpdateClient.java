package updater;

import java.io.*;
import java.net.*;

public class UpdateClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("192.168.100.179", 8080); // Replace with your server's IP address
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Check for updates
        out.println("check_for_update");
        String serverResponse = in.readLine();
        System.out.println("Current version: " + serverResponse);

        // If an update is needed, download and replace the JAR
        if (!serverResponse.equals("1.0.1")) { // Check if update is needed
            out.println("download_update");
            File jarFile = new File("path/to/your/application.jar"); // Path to your current JAR file
            byte[] buffer = new byte[4096];
            InputStream is = socket.getInputStream();
            FileOutputStream fos = new FileOutputStream(jarFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.close();
            System.out.println("Update downloaded and replaced.");
        }
        socket.close();
    }
}
