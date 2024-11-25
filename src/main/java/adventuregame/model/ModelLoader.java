package adventuregame.model;

import jassimp.*;

import java.io.IOException;
import java.util.EnumSet;

public class ModelLoader {
    public static void loadModel(String filePath) throws IOException {
        AiScene scene = Jassimp.importFile(filePath,
                EnumSet.of(AiPostProcessSteps.Triangulate,
                        AiPostProcessSteps.FlipUVs));
        if (scene == null) {
            throw new RuntimeException("Error loading adventuregame.model");
        }
        processNode((AiNode) scene.getSceneRoot(), scene);
    }

    private static void processNode(AiNode node, AiScene scene) {
        int[] meshIndices = node.getMeshes();
        for (int meshIndex : meshIndices) {
            AiMesh mesh = scene.getMeshes().get(meshIndex);
            processMesh(mesh);
        }
        for (AiNode childNode : node.getChildren()) {
            processNode(childNode, scene);
        }
    }

    private static void processMesh(AiMesh mesh) {
        for (int i = 0; i < mesh.getNumVertices(); i++) {
            float x = mesh.getPositionX(i);
            float y = mesh.getPositionY(i);
            float z = mesh.getPositionZ(i);
            System.out.println("Vertex: [" + x + ", " + y + ", " + z + "]");
        }
    }
}
