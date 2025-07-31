package net.adventuregame.toolbox

import com.chidozie.core.renderEngine.WindowManager
import com.chidozie.core.terrains.Terrain
import net.adventuregame.entity.Camera
import net.adventuregame.entity.Entity
import net.adventuregame.game.AdventureMain
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MousePicker(
    private val camera: Camera,
    private val projectionMatrix: Matrix4f?,
    private val terrain: Terrain?,
    private val entities: MutableList<Entity>
) {
    var currentRay: Vector3f = Vector3f()
        private set


    private var viewMatrix: Matrix4f?
    var closestEntity: Entity? = null
        private set
    private val window: WindowManager

    var currentTerrainPoint: Vector3f? = null
        private set


    init {
        viewMatrix = Maths.createViewMatrix(camera)
        window = AdventureMain.window
    }


    fun update() {
        viewMatrix = Maths.createViewMatrix(camera)
        currentRay = calculateMouseRay()
        if (intersectionInRange(0f, RAY_RANGE, currentRay)) {
            currentTerrainPoint = binarySearch(0, 0f, RAY_RANGE, currentRay)
        } else {
            currentTerrainPoint = null
        }

        // Check for the closest entity in the ray range
        closestEntity = getClosestEntity(currentRay)
        if (closestEntity != null) {
            log.info("Caught")
            closestEntity!!.model?.texture?.isSelected = true
        }
    }

    private fun getClosestEntity(ray: Vector3f): Entity? {
        var closestDistance: Float = RAY_RANGE
        var closestEntity: Entity? = null

        for (entity in entities) { // Replace 'entities' with your actual entity list
            val entityPosition = entity.position
            val camPos = camera.position
            val distance = camPos.distance(entityPosition)

            if (distance < closestDistance && isPointInRayRange(ray, entityPosition)) {
                closestDistance = distance
                closestEntity = entity
            }
        }

        return closestEntity
    }

    private fun isPointInRayRange(ray: Vector3f, point: Vector3f?): Boolean {
        val camPos = camera.position
        val pointOnRay = getPointOnRay(ray, camPos.distance(point))

        // Adjust this condition based on your specific requirements
        return pointOnRay.distance(point) < 1.0f
    }


    private fun calculateMouseRay(): Vector3f {
        val mouseX = window.displsVec.x
        val mouseY = window.displsVec.y
        val normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY)
        val clipCoords = Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f)
        val eyeCoords = toEyeCoords(clipCoords)
        val worldRay = toWorldCoords(eyeCoords)
        return worldRay
    }


    private fun toWorldCoords(eyeCoords: Vector4f?): Vector3f {
        val invertedView = Matrix4f(viewMatrix).invert()
        val rayWorld = invertedView.transform(eyeCoords)
        val mouseRay = Vector3f(rayWorld.x, rayWorld.y, rayWorld.z)
        mouseRay.normalize()
        return mouseRay
    }


    private fun toEyeCoords(clipCoords: Vector4f?): Vector4f {
        val invertedProjection = Matrix4f(projectionMatrix).invert()
        val eyeCoords = invertedProjection.transform(clipCoords)
        return Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f)
    }


    private fun getNormalisedDeviceCoordinates(mouseX: Float, mouseY: Float): Vector2f {
        val x = (2.0f * mouseX) / window.width - 1f
        val y = (2.0f * mouseY) / window.height - 1f
        return Vector2f(x, y)
    }

    //**********************************************************
    private fun getPointOnRay(ray: Vector3f, distance: Float): Vector3f {
        val camPos = camera.position
        val start = Vector3f(camPos.x, camPos.y, camPos.z)
        val scaledRay = Vector3f(ray).mul(distance)
        return start.add(scaledRay)
    }

    private fun binarySearch(count: Int, start: Float, finish: Float, ray: Vector3f): Vector3f? {
        val half = start + ((finish - start) / 2f)
        if (count >= RECURSION_COUNT) {
            val endPoint = getPointOnRay(ray, half)
            val terrain = getTerrain(endPoint.x(), endPoint.z())
            if (terrain != null) {
                return endPoint
            } else {
                return null
            }
        }
        if (intersectionInRange(start, half, ray)) {
            return binarySearch(count + 1, start, half, ray)
        } else {
            return binarySearch(count + 1, half, finish, ray)
        }
    }


    private fun intersectionInRange(start: Float, finish: Float, ray: Vector3f): Boolean {
        val startPoint = getPointOnRay(ray, start)
        val endPoint = getPointOnRay(ray, finish)
        if (!isUnderGround(startPoint) && isUnderGround(endPoint)) {
            return true
        } else {
            return false
        }
    }


    private fun isUnderGround(testPoint: Vector3f): Boolean {
        val terrain = getTerrain(testPoint.x(), testPoint.z())
        var height = 0f
        if (terrain != null) {
            height = terrain.getHeightOfTerrain(testPoint.x(), testPoint.z())
        }
        if (testPoint.y < height) {
            return true
        } else {
            return false
        }
    }


    private fun getTerrain(worldX: Float, worldZ: Float): Terrain? {
        return terrain
    }

    companion object {
        private const val RECURSION_COUNT = 200
        private const val RAY_RANGE = 600f
        private val log: Logger = LoggerFactory.getLogger(MousePicker::class.java)
    }
}
