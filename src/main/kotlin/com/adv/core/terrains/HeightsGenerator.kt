package com.adv.core.terrains

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.math.cos
import kotlin.math.pow

class HeightsGenerator(seed: Int) {
    private val random = Random()
    var seed: Int = 0

    init {
        if (seed == 0) {
            this.seed = random.nextInt(1000000000)
        } else {
            this.seed = seed
        }
        log.info("Generated A Seed")
    }

    fun generateHeight(x: Int, z: Int): Float {
        var total = 0f
        val d = 2.0.pow((OCTAVES - 1).toDouble()).toFloat()
        for (i in 0..<OCTAVES) {
            val freq = (2.0.pow(i.toDouble()) / d).toFloat()
            val amp: Float = ROUGHNESS.toDouble().pow(i.toDouble()).toFloat() * AMPLITUDE
            total += getInterpolatedNoise(x * freq, z * freq) * amp
        }
        return total
    }

    private fun getInterpolatedNoise(x: Float, z: Float): Float {
        val intX = x.toInt()
        val intZ = z.toInt()
        val fracX = x - intX
        val fracZ = z - intZ

        val v1 = getSmoothNoise(intX, intZ)
        val v2 = getSmoothNoise(intX + 1, intZ)
        val v3 = getSmoothNoise(intX, intZ + 1)
        val v4 = getSmoothNoise(intX + 1, intZ + 1)
        val i1 = interpolate(v1, v2, fracX)
        val i2 = interpolate(v3, v4, fracX)
        return interpolate(i1, i2, fracZ)
    }

    private fun interpolate(a: Float, b: Float, blend: Float): Float {
        val theta = blend * Math.PI
        val f = ((1f - cos(theta)) * 0.5f).toFloat()
        return a * (1f - f) + b * f
    }

    private fun getSmoothNoise(x: Int, z: Int): Float {
        val corners = ((getNoise(x - 1, z - 1) + getNoise(x + 1, z - 1) + getNoise(x - 1, z + 1)
                + getNoise(x + 1, z + 1))) / 16f
        val sides = ((getNoise(x - 1, z) + getNoise(x + 1, z) + getNoise(x, z - 1)
                + getNoise(x, z + 1))) / 8f
        val center = getNoise(x, z) / 4f
        return corners + sides + center
    }

    private fun getNoise(x: Int, z: Int): Float {
        random.setSeed((x * 49632 + z * 325176 + seed).toLong())
        return random.nextFloat() * 2f - 1f
    }

    companion object {
        private const val AMPLITUDE = 70f
        private const val OCTAVES = 3
        private const val ROUGHNESS = 0.3f
        private val log: Logger = LoggerFactory.getLogger(HeightsGenerator::class.java)
    }
}
