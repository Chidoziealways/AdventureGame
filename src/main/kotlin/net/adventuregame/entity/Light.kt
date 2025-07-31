package net.adventuregame.entity

import org.joml.Vector3f
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Light {
    var position: Vector3f?
    var colour: Vector3f?
    var attenuation: Vector3f? = Vector3f(1f, 0f, 0f)

    constructor(position: Vector3f?, colour: Vector3f?) {
        this.position = position
        this.colour = colour
    }

    constructor(position: Vector3f?, colour: Vector3f?, attenuation: Vector3f?) {
        this.position = position
        this.colour = colour
        this.attenuation = attenuation
    }

    companion object {
        private val log: Logger? = LoggerFactory.getLogger(Light::class.java)
    }
}
