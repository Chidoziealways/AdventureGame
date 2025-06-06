package net.adventuregame.toolbox;

import static org.lwjgl.glfw.GLFW.*;

public class Settings {
    public static int WALK_FORWARD_KEY = Key.WALK_FORWARD.getKeyCode();
    public static int WALK_BACKWARDS_KEY = Key.WALK_BACKWARDS.getKeyCode();
    public static int JUMP_KEY = Key.JUMP.getKeyCode();
    public static int CROUCH_KEY = Key.CROUCH.getKeyCode();
    public static int ATTACK_KEY = Key.ATTACK.getKeyCode();
    public static int RELOAD_KEY = Key.RELOAD.getKeyCode();
    public static int INTERACT_KEY = Key.INTERACT.getKeyCode();
    public static int LEFT_TURN_KEY_1 = Key.LEFT_TURN_1.getKeyCode();
    public static int LEFT_TURN_KEY_2 = Key.LEFT_TURN_2.getKeyCode();
    public static int RIGHT_TURN_KEY_1 = Key.RIGHT_TURN_1.getKeyCode();
    public static int RIGHT_TURN_KEY_2 = Key.RIGHT_TURN_2.getKeyCode();

    private enum Key {
        WALK_FORWARD(GLFW_KEY_W, "WALK"),
        WALK_BACKWARDS(GLFW_KEY_S, "REVERSE"),
        JUMP(GLFW_KEY_SPACE, "JUMP"),
        CROUCH(GLFW_KEY_C, "CROUCH"),
        ATTACK(GLFW_MOUSE_BUTTON_LEFT, "ATTACK"),
        RELOAD(GLFW_MOUSE_BUTTON_4, "RELOAD"),
        INTERACT(GLFW_KEY_E, "INTERACT"),
        LEFT_TURN_1(GLFW_KEY_A, "LEFT_TURN1"),
        LEFT_TURN_2(GLFW_KEY_LEFT, "LEFT_TURN2"),
        RIGHT_TURN_1(GLFW_KEY_D, "RIGHT_TURN1"),
        RIGHT_TURN_2(GLFW_KEY_RIGHT, "RIGHT_TURN2");

        private final int keyCode;
        private final String name;

        Key(int keyCode, String name) {
            this.keyCode = keyCode;
            this.name = name;
        }

        public int getKeyCode() {
            return keyCode;
        }

        public String getName() {
            return name;
        }
    }
}


