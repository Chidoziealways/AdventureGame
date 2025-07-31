package net.adventuregame.particles

import com.chidozie.core.renderEngine.WindowManager
import net.adventuregame.entities.Camera
import net.adventuregame.player.Player
import org.joml.Vector2f
import org.joml.Vector3f
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.floor

class Particle(
    val texture: ParticleTexture,
    val position: Vector3f,
    private val velocity: Vector3f,
    private val gravityEffect: Float,
    private val lifeLength: Float,
    val rotation: Float,
    val scale: Float
) {
    val texOffset1: Vector2f = Vector2f()
    val texOffset2: Vector2f = Vector2f()
    var blend: Float = 0f
        private set
    private var elapsedTime = 0f
    val distance: Float = 0f

    private val reusableChange = Vector3f()

    init {
        粒子の先生.addParticle(this)
    }

    fun update(camera: Camera): Boolean {
        velocity.y += Player.Companion.GRAVITY * gravityEffect * WindowManager.frameTimeSeconds
        reusableChange.set(velocity)
        reusableChange.mul(WindowManager.frameTimeSeconds)
        position.add(reusableChange)
        val distance = Vector3f(camera.position).sub(position).lengthSquared()
        updateTextureCoordInfo()
        elapsedTime += WindowManager.frameTimeSeconds
        return elapsedTime < lifeLength
    }

    private fun updateTextureCoordInfo() {
        val lifeFactor = elapsedTime / lifeLength
        val stageCount = texture.numberOfRows * texture.numberOfRows
        val atlasProgression = lifeFactor * stageCount
        val index1 = floor(atlasProgression.toDouble()).toInt()
        val index2 = if (index1 < stageCount - 1) index1 + 1 else index1
        this.blend = atlasProgression % 1
        setTextureOffset(texOffset1, index1)
        setTextureOffset(texOffset2, index2)
    }

    private fun setTextureOffset(offset: Vector2f, index: Int) {
        val column = index % texture.numberOfRows
        val row = index / texture.numberOfRows
        offset.x = column.toFloat() / texture.numberOfRows
        offset.y = row.toFloat() / texture.numberOfRows
    }

    companion object {
        private val log: Logger? = LoggerFactory.getLogger(Particle::class.java)
    }
}
