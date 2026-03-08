package game;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {
    private static KeyListener instance;

    public static KeyListener get() {
        if(instance == null)
            instance = new KeyListener();

        return instance;
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if(action == GLFW_PRESS) {
            get().mKeyPressed[key] = true;
        } else if (action == GLFW_RELEASE) {
            get().mKeyPressed[key] = false;
        }
    }

    public static boolean isKeyPressed(int keyCode) {
        return get().mKeyPressed[keyCode];
    }

    private final boolean[] mKeyPressed = new boolean[350];

    private KeyListener() {

    }
}
