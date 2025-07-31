package net.adventuregame.mobs

import com.chidozie.core.terrains.Terrain
import org.joml.Vector3f
import java.util.*
import java.util.function.ToDoubleFunction

object Pathfinding {
    fun findPath(start: Vector3f, target: Vector3f?, terrain: Terrain): MutableList<Vector3f>? {
        val openSet = PriorityQueue<Node>(Comparator.comparingDouble(ToDoubleFunction { n: Node? -> n!!.fCost.toDouble() }))
        val allNodes: MutableMap<Vector3f?, Node> = HashMap<Vector3f?, Node>()

        val startNode = Node(start, null, 0f, start.distance(target))
        openSet.add(startNode)
        allNodes.put(start, startNode)

        while (!openSet.isEmpty()) {
            val current = openSet.poll()

            if (current.position == target) {
                return constructPath(current)
            }

            for (neighbor in getNeighbors(current.position, terrain)) {
                val gCost = current.gCost + current.position.distance(neighbor)
                val hCost = neighbor.distance(target)
                val neighborNode =
                    allNodes.getOrDefault(neighbor, Node(neighbor, null, Float.Companion.MAX_VALUE, hCost))

                if (gCost < neighborNode.gCost) {
                    neighborNode.gCost = gCost
                    neighborNode.fCost = gCost + hCost
                    neighborNode.parent = current
                    openSet.add(neighborNode)
                    allNodes.put(neighbor, neighborNode)
                }
            }
        }

        return mutableListOf() // No path found
    }

    private fun getNeighbors(position: Vector3f, terrain: Terrain): MutableList<Vector3f> {
        val neighbors: MutableList<Vector3f> = ArrayList<Vector3f>()
        val stepSize = 5.0f // Grid resolution
        val terrainHeight = terrain.getHeightOfTerrain(position.x, position.z)

        var dx = -stepSize
        while (dx <= stepSize) {
            var dz = -stepSize
            while (dz <= stepSize) {
                if (dx == 0f && dz == 0f) {
                    dz += stepSize
                    continue
                }
                val newPos = Vector3f(position.x + dx, terrainHeight, position.z + dz)
                neighbors.add(newPos)
                dz += stepSize
            }
            dx += stepSize
        }
        return neighbors
    }

    private fun constructPath(node: Node?): MutableList<Vector3f>? {
        var node = node
        val path: MutableList<Vector3f>? = ArrayList()
        while (node != null) {
            path!!.add(node.position)
            node = node.parent
        }
        path!!.reverse()
        return path
    }

    private class Node(var position: Vector3f, var parent: Node?, var gCost: Float, hCost: Float) {
        var fCost: Float

        init {
            this.fCost = gCost + hCost
        }
    }
}

