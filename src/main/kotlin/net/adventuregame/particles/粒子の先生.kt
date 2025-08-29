package net.adventuregame.particles

import com.adv.core.renderEngine.Loader
import net.adventuregame.entity.Camera
import org.joml.Matrix4f
import org.slf4j.LoggerFactory

object 粒子の先生 {
    private val log = LoggerFactory.getLogger(粒子の先生::class.java)
    private val 粒子ら = mutableMapOf<ParticleTexture, MutableList<Particle>>()
    private var renderer: ParticleRenderer? = null

    fun init(loader: Loader, projectionMatrix: Matrix4f) {
        renderer = ParticleRenderer(loader, projectionMatrix)
    }

    fun update(camera: Camera) {
        val mapIterator = 粒子ら.entries.iterator()
        while (mapIterator.hasNext()) {
            val (texture, list) = mapIterator.next()
            val iterator = list.iterator()
            while (iterator.hasNext()) {
                val particle = iterator.next()
                if (!particle.update(camera)) {
                    iterator.remove()
                }
            }
            if (list.isEmpty()) {
                mapIterator.remove()
            } else {
                InsertionSort.sortHighToLow(list)
            }
        }
    }

    fun renderParticles(camera: Camera) {
        renderer?.render(粒子ら, camera)
    }

    fun cleanUp() {
        renderer?.cleanUp()
    }

    fun addParticle(particle: Particle) {
        val list = 粒子ら.getOrPut(particle.texture) { mutableListOf() }
        list.add(particle)
    }
}
