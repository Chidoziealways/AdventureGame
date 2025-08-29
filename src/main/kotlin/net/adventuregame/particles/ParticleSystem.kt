package net.adventuregame.particles

import com.adv.core.renderEngine.WindowManager
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import java.util.*
import kotlin.math.*

class ParticleSystem(
    private val texture: ParticleTexture?,
    private val pps: Float,
    private val averageSpeed: Float,
    private val gravityComplient: Float,
    private val averageLifeLength: Float,
    private val averageScale: Float
) {
    private var speedError = 0f
    private var lifeError = 0f
    private var scaleError = 0f
    private var randomRotation = false
    private var direction: Vector3f? = null
    private var directionDeviation = 0f


    private val random = Random()


    /**
     * @param direction - The average direction in which particles are emitted.
     * @param deviation - A value between 0 and 1 indicating how far from the chosen direction particles can deviate.
     */
    fun setDirection(direction: Vector3f, deviation: Float) {
        this.direction = Vector3f(direction)
        this.directionDeviation = (deviation * Math.PI).toFloat()
    }


    fun randomizeRotation() {
        randomRotation = true
    }


    /**
     * @param error
     * - A number between 0 and 1, where 0 means no error margin.
     */
    fun setSpeedError(error: Float) {
        this.speedError = error * averageSpeed
    }


    /**
     * @param error
     * - A number between 0 and 1, where 0 means no error margin.
     */
    fun setLifeError(error: Float) {
        this.lifeError = error * averageLifeLength
    }


    /**
     * @param error
     * - A number between 0 and 1, where 0 means no error margin.
     */
    fun setScaleError(error: Float) {
        this.scaleError = error * averageScale
    }


    fun generateParticles(systemCenter: Vector3f) {
        val delta: Float = WindowManager.frameTimeSeconds
        val particlesToCreate = pps * delta
        val count = floor(particlesToCreate.toDouble()).toInt()
        val partialParticle = particlesToCreate % 1
        for (i in 0..<count) {
            emitParticle(systemCenter)
        }
        if (Math.random() < partialParticle) {
            emitParticle(systemCenter)
        }
    }


    private fun emitParticle(center: Vector3f) {
        var velocity: Vector3f? = null
        if (direction != null) {
            velocity = Companion.generateRandomUnitVectorWithinCone(direction!!, directionDeviation)
        } else {
            velocity = generateRandomUnitVector()
        }
        velocity.normalize()
        velocity.mul(generateValue(averageSpeed, speedError))
        val scale = generateValue(averageScale, scaleError)
        val lifeLength = generateValue(averageLifeLength, lifeError)
        Particle(texture!!, Vector3f(center), velocity, gravityComplient, lifeLength, generateRotation(), scale)
    }


    private fun generateValue(average: Float, errorMargin: Float): Float {
        val offset = (random.nextFloat() - 0.5f) * 2f * errorMargin
        return average + offset
    }


    private fun generateRotation(): Float {
        if (randomRotation) {
            return random.nextFloat() * 360f
        } else {
            return 0f
        }
    }


    private fun generateRandomUnitVector(): Vector3f {
        val theta = (random.nextFloat() * 2f * Math.PI).toFloat()
        val z = (random.nextFloat() * 2) - 1
        val rootOneMinusZSquared = sqrt((1 - z * z).toDouble()).toFloat()
        val x = (rootOneMinusZSquared * cos(theta.toDouble())).toFloat()
        val y = (rootOneMinusZSquared * sin(theta.toDouble())).toFloat()
        return Vector3f(x, y, z)
    }


    companion object {
        private fun generateRandomUnitVectorWithinCone(coneDirection: Vector3f, angle: Float): Vector3f {
            val cosAngle = cos(angle.toDouble()).toFloat()
            val random = Random()
            val theta = (random.nextFloat() * 2f * Math.PI).toFloat()
            val z = cosAngle + (random.nextFloat() * (1 - cosAngle))
            val rootOneMinusZSquared = sqrt((1 - z * z).toDouble()).toFloat()
            val x = (rootOneMinusZSquared * cos(theta.toDouble())).toFloat()
            val y = (rootOneMinusZSquared * sin(theta.toDouble())).toFloat()


            val direction = Vector4f(x, y, z, 1f)
            if (coneDirection.x != 0f || coneDirection.y != 0f || (coneDirection.z != 1f && coneDirection.z != -1f)) {
                val rotateAxis = coneDirection.cross(Vector3f(0f, 0f, 1f), Vector3f())
                rotateAxis.normalize()
                val rotateAngle = acos(coneDirection.dot(Vector3f(0f, 0f, 1f)).toDouble()).toFloat()
                val rotationMatrix = Matrix4f()
                rotationMatrix.rotate(-rotateAngle, rotateAxis)
                rotationMatrix.transform(direction)
            } else if (coneDirection.z == -1f) {
                direction.z *= -1f
            }
            return Vector3f(direction.x, direction.y, direction.z)
        }
    }
}
