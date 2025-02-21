    package com.chidozie.core.renderEngine;
    
    import net.adventuregame.game.AdventureMain;
    import org.joml.Vector2d;
    import org.joml.Vector2f;
    import org.lwjgl.glfw.GLFW;
    import org.lwjgl.glfw.GLFWScrollCallback;
    
    public class MouseInput {
    
        private final Vector2d previousPos, currentPos;
        private final Vector2f displsVec;
    
        private boolean inWindow = false, leftButtonPress = false, rightButtonPress = false;
        private double scrollOffset = 0.0; // New field to store the scroll offset
    
        public MouseInput() {
            previousPos = new Vector2d(-1, -1);
            currentPos = new Vector2d(0, 0);
            displsVec = new Vector2f();
            init();
        }
    
        public void init() {
            GLFW.glfwSetCursorPosCallback(AdventureMain.getWindow().getWindow(), ((window, xpos, ypos) -> {
                currentPos.x = xpos;
                currentPos.y = ypos;
            }));
    
            GLFW.glfwSetCursorEnterCallback(AdventureMain.getWindow().getWindow(), ((window, entered) -> {
                inWindow = entered;
            }));
    
            GLFW.glfwSetMouseButtonCallback(AdventureMain.getWindow().getWindow(), ((window, button, action, mods) -> {
                leftButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS;
                rightButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS;
            }));
    
            // Adding scroll callback
            GLFW.glfwSetScrollCallback(AdventureMain.getWindow().getWindow(), new GLFWScrollCallback() {
                @Override
                public void invoke(long window, double xOffset, double yOffset) {
                    scrollOffset = yOffset; // Capture the scroll offset
                }
            });
        }
    
        public void input() {
            displsVec.x = 0;
            displsVec.y = 0;
            if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
                double x = currentPos.x - previousPos.x;
                double y = currentPos.y - previousPos.y;
                boolean rotateX = x != 0;
                boolean rotateY = y != 0;
                if (rotateX)
                    displsVec.y = (float) x;
                if (rotateY)
                    displsVec.x = (float) y;
            }
            previousPos.x = currentPos.x;
            previousPos.y = currentPos.y;
        }
    
        public boolean isRightButtonPress() {
            return rightButtonPress;
        }
    
        public boolean isLeftButtonPress() {
            return leftButtonPress;
        }
    
        public Vector2f getDisplsVec() {
            return displsVec;
        }
    
        public double getScrollOffset() {
            return scrollOffset;
        }
    
        public void resetScrollOffset() {
            scrollOffset = 0.0;
        }
    
    }
