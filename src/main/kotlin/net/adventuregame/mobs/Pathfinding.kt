package net.adventuregame.mobs

import com.adv.core.terrains.Terrain
import org.joml.Vector3f
import java.util.*
import java.util.concurrent.Executors
import java.util.function.ToDoubleFunction

object Pathfinding {
    private val executor = Executors.newFixedThreadPool(4){ runnable ->
        Thread(runnable).apply { isDaemon = true }
    }

    data class Vec3Key(val x: Float, val y: Float, val z: Float) {
        constructor(v: Vector3f) : this(v.x, v.y, v.z)
    }

    fun findPath(start: Vector3f, target: Vector3f?, terrain: Terrain): MutableList<Vector3f>? {
        try {
       //     println("Finding Path")
            val openSet =
                PriorityQueue<Node>(Comparator.comparingDouble(ToDoubleFunction { n: Node? -> n!!.fCost.toDouble() }))
            val allNodes: MutableMap<Vec3Key, Node> = HashMap<Vec3Key, Node>()

            if (target == null) {
         //       println("NO TARGET")
                return mutableListOf()
            }

            val startNode = Node(position = start, parent = null, gCost = 0f, hCost = start.distance(target))
           // println("Created Node with UUID=${startNode.id}")
            openSet.add(startNode)
            allNodes.put(Vec3Key(start), startNode)

            while (!openSet.isEmpty()) {
                val current = openSet.poll()

                if (current.position.distance(target) < 5f) {
             //       println("Constructing Path")
                    return constructPath(current)
                }

                for (neighbor in getNeighbors(current.position, terrain)) {
                    val gCost = current.gCost + current.position.distance(neighbor)
                    val hCost = neighbor.distance(target)
                    val neighborNode = allNodes.getOrDefault(
                        Vec3Key(neighbor),
                        Node(position = neighbor, parent = null, gCost = Float.MAX_VALUE, hCost = hCost)
                    )

                    if (gCost < neighborNode.gCost) {
                        neighborNode.gCost = gCost
                        neighborNode.fCost = gCost + hCost
                        neighborNode.parent = current
                        if (!openSet.contains(neighborNode)) {
               //             println("Adding Node ${neighborNode.id} to openSet at ${neighborNode.position}")
                            openSet.add(neighborNode)
                        }
                        allNodes.put(Vec3Key(neighbor), neighborNode)
                    }
                }
            }
            //println("No conditions met, returning empty list")
            return mutableListOf() // No path found
        } catch (e: Exception) {
            //println("Exception Caught in FindPath!")
            return mutableListOf()
        }
    }

    private fun getNeighbors(position: Vector3f, terrain: Terrain): MutableList<Vector3f> {
        val neighbors: MutableList<Vector3f> = ArrayList<Vector3f>()
        val stepSize = 5.0f // Grid resolution

        var dx = -stepSize
        while (dx <= stepSize) {
            var dz = -stepSize
            while (dz <= stepSize) {
                if (dx == 0f && dz == 0f) {
                    dz += stepSize
                    continue
                }
                val newX = position.x + dx
                val newZ = position.z + dz
                val newY = terrain.getHeightOfTerrain(newX, newZ)
                if (newY.isNaN()) println("WARNING: terrain height NaN at $newX, $newZ")
                val newPos = Vector3f(newX, newY, newZ)
                neighbors.add(newPos)
                dz += stepSize
            }
            dx += stepSize
        }
        return neighbors
    }

    private fun constructPath(node: Node?): MutableList<Vector3f>? {
        //println("Constructing PATH!!")
        var node = node
        val path: MutableList<Vector3f>? = ArrayList()
        while (node != null) {
            path!!.add(node.position)
            node = node.parent
        }
        //println("Reversing Path")
        path!!.reverse()
        return path
    }

    private class Node(
        val id: UUID = UUID.randomUUID(), // Add this
        var position: Vector3f,
        var parent: Node?,
        var gCost: Float,
        hCost: Float
    ) {
        var fCost: Float = gCost + hCost

        override fun toString(): String {
            return "Node(id=$id, pos=$position, g=$gCost, f=$fCost)"
        }
    }

    fun findPathAsync(
        start: Vector3f,
        target: Vector3f?,
        terrain: Terrain,
        onComplete: (List<Vector3f>) -> Unit
    ) {
        println("Find Path Async Called")
        println("Executor isShutdown=${executor.isShutdown}, isTerminated=${executor.isTerminated}")
        executor.execute {
            println("Executing Executor")
            try {
                val result = findPath(start, target, terrain) ?: emptyList()
                println(">>> [Kotlin] Pathfinding result: ${result.size}")
                onComplete(result)
            } catch (e: Exception) {
                println(">>> [Kotlin] Pathfinding CRASHED: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}