package net.adventuregame.toolbox

import org.lwjgl.glfw.GLFW

object Settings {
    var WALK_FORWARD_KEY: Int = Key.WALK_FORWARD.keyCode
    var WALK_BACKWARDS_KEY: Int = Key.WALK_BACKWARDS.keyCode
    var JUMP_KEY: Int = Key.JUMP.keyCode
    var CROUCH_KEY: Int = Key.CROUCH.keyCode
    var ATTACK_KEY: Int = Key.ATTACK.keyCode
    var RELOAD_KEY: Int = Key.RELOAD.keyCode
    var INTERACT_KEY: Int = Key.INTERACT.keyCode
    var LEFT_TURN_KEY_1: Int = Key.LEFT_TURN_1.keyCode
    var LEFT_TURN_KEY_2: Int = Key.LEFT_TURN_2.keyCode
    var RIGHT_TURN_KEY_1: Int = Key.RIGHT_TURN_1.keyCode
    var RIGHT_TURN_KEY_2: Int = Key.RIGHT_TURN_2.keyCode

    private enum class Key(val keyCode: Int, name: String) {
        WALK_FORWARD(GLFW.GLFW_KEY_W, "WALK"),
        WALK_BACKWARDS(GLFW.GLFW_KEY_S, "REVERSE"),
        JUMP(GLFW.GLFW_KEY_SPACE, "JUMP"),
        CROUCH(GLFW.GLFW_KEY_C, "CROUCH"),
        ATTACK(GLFW.GLFW_MOUSE_BUTTON_LEFT, "ATTACK"),
        RELOAD(GLFW.GLFW_MOUSE_BUTTON_4, "RELOAD"),
        INTERACT(GLFW.GLFW_KEY_E, "INTERACT"),
        LEFT_TURN_1(GLFW.GLFW_KEY_A, "LEFT_TURN1"),
        LEFT_TURN_2(GLFW.GLFW_KEY_LEFT, "LEFT_TURN2"),
        RIGHT_TURN_1(GLFW.GLFW_KEY_D, "RIGHT_TURN1"),
        RIGHT_TURN_2(GLFW.GLFW_KEY_RIGHT, "RIGHT_TURN2");

        val names: String?

        init {
            this.names = name
        }
    }
}


