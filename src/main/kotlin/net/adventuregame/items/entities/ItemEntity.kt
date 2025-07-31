package net.adventuregame.items.entities

import net.adventuregame.entity.Entity
import net.adventuregame.game.GameState
import net.adventuregame.game.GameState.Companion.removeEntity
import net.adventuregame.items.Item
import net.adventuregame.models.TexturedModel
import org.joml.Vector3f
import org.slf4j.Logger
import org.slf4j.LoggerFactory

open class ItemEntity(model: TexturedModel, pos: Vector3f, rot: Vector3f, scale: Float, private val item: Item) : Entity(model, pos, rot.x, rot.y, rot.z, scale){
    private val log: Logger = LoggerFactory.getLogger(ItemEntity::class.java)

    open fun update() {
        // Optional: idle animation or rotation
        this.increaseRotation(0f, 1f, 0f)


        // Check if player is nearby
        val player = GameState.player // You must have a global access or pass it in
        if (position.distance(player!!.position) < 2.0f) {
            // Add the gun item to the player's inventory
            player.inventory.addItem(item)
            log.info("Added to Inventory")
            // Remove this entity from the world
            removeEntity(this)
        }
    }
}