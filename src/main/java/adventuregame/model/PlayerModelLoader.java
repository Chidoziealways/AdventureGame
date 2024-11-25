package adventuregame.model;

import org.joml.Vector3f;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerModelLoader {

    public static Vector3f[] loadVertices(String modelPath) throws IOException {
        List<Vector3f> vertices = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(modelPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("v ")) {
                    String[] parts = line.split(" ");
                    float x = Float.parseFloat(parts[1]);
                    float y = Float.parseFloat(parts[2]);
                    float z = Float.parseFloat(parts[3]);
                    vertices.add(new Vector3f(x, y, z));
                }
            }
        }
        return vertices.toArray(new Vector3f[0]);
    }

    public static int[] loadIndices(String modelPath) throws IOException {
        List<Integer> indices = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(modelPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("f ")) {
                    String[] parts = line.split(" ");
                    indices.add(Integer.parseInt(parts[1]) - 1);
                    indices.add(Integer.parseInt(parts[2]) - 1);
                    indices.add(Integer.parseInt(parts[3]) - 1);
                }
            }
        }
        return indices.stream().mapToInt(i -> i).toArray();
    }
}
