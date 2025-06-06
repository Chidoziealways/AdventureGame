package net.adventuregame.mobs;

import com.chidozie.core.terrains.Terrain;
import org.joml.Vector3f;
import java.util.*;

public class Pathfinding {

    public static List<Vector3f> findPath(Vector3f start, Vector3f target, Terrain terrain) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fCost));
        Map<Vector3f, Node> allNodes = new HashMap<>();

        Node startNode = new Node(start, null, 0, start.distance(target));
        openSet.add(startNode);
        allNodes.put(start, startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.position.equals(target)) {
                return constructPath(current);
            }

            for (Vector3f neighbor : getNeighbors(current.position, terrain)) {
                float gCost = current.gCost + current.position.distance(neighbor);
                float hCost = neighbor.distance(target);
                Node neighborNode = allNodes.getOrDefault(neighbor, new Node(neighbor, null, Float.MAX_VALUE, hCost));

                if (gCost < neighborNode.gCost) {
                    neighborNode.gCost = gCost;
                    neighborNode.fCost = gCost + hCost;
                    neighborNode.parent = current;
                    openSet.add(neighborNode);
                    allNodes.put(neighbor, neighborNode);
                }
            }
        }

        return Collections.emptyList(); // No path found
    }

    private static List<Vector3f> getNeighbors(Vector3f position, Terrain terrain) {
        List<Vector3f> neighbors = new ArrayList<>();
        float stepSize = 5.0f; // Grid resolution
        float terrainHeight = terrain.getHeightOfTerrain(position.x, position.z);

        for (float dx = -stepSize; dx <= stepSize; dx += stepSize) {
            for (float dz = -stepSize; dz <= stepSize; dz += stepSize) {
                if (dx == 0 && dz == 0) continue;
                Vector3f newPos = new Vector3f(position.x + dx, terrainHeight, position.z + dz);
                neighbors.add(newPos);
            }
        }
        return neighbors;
    }

    private static List<Vector3f> constructPath(Node node) {
        List<Vector3f> path = new ArrayList<>();
        while (node != null) {
            path.add(node.position);
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private static class Node {
        Vector3f position;
        Node parent;
        float gCost, fCost;

        Node(Vector3f position, Node parent, float gCost, float hCost) {
            this.position = position;
            this.parent = parent;
            this.gCost = gCost;
            this.fCost = gCost + hCost;
        }
    }
}

