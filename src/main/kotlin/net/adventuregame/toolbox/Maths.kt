package net.adventuregame.toolbox

import net.adventuregame.entities.Camera
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f

object Maths {
    fun createTransformationMatrix(translation: Vector2f, scale: Vector2f): Matrix4f {
        val matrix = Matrix4f().identity() // Set the matrix to identity
        matrix.translate(translation.x, translation.y, 0f) // Apply translation
        matrix.scale(scale.x, scale.y, 1f) // Apply scaling
        return matrix
    }


    fun barryCentric(p1: Vector3f, p2: Vector3f, p3: Vector3f, pos: Vector2f): Float {
        val det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z)
        val l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det
        val l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det
        val l3 = 1.0f - l1 - l2
        return l1 * p1.y + l2 * p2.y + l3 * p3.y
    }


    fun createTransformationMatrix(translation: Vector3f?, rx: Float, ry: Float, rz: Float, scale: Float): Matrix4f {
        val matrix = Matrix4f().identity()
        matrix.translate(translation)
            .rotateX(Math.toRadians(rx.toDouble()).toFloat())
            .rotateY(Math.toRadians(ry.toDouble()).toFloat())
            .rotateZ(Math.toRadians(rz.toDouble()).toFloat())
            .scale(scale)
        return matrix
    }

    fun createViewMatrix(camera: Camera): Matrix4f {
        val viewMatrix = Matrix4f().identity()
        viewMatrix.rotateX(Math.toRadians(camera.pitch.toDouble()).toFloat())
            .rotateY(Math.toRadians(camera.yaw.toDouble()).toFloat())

        val cameraPos = camera.position
        val negativeCameraPos = Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z)
        viewMatrix.translate(negativeCameraPos)

        return viewMatrix
    }
}
