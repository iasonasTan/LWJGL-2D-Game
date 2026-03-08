package game;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

@SuppressWarnings("unused")
public class MouseListener {
    private static MouseListener instance;

    public static MouseListener get() {
        if(instance == null)
            instance = new MouseListener();

        return instance;
    }

    public static void mousePosCallback(long window, double posX, double posY) {
        get().mLastX = get().mPosX;
        get().mLastY = get().mPosY;
        get().mPosX = posX;
        get().mPosY = posY;
        get().mIsDragging = get().mMouseButtonPressed[0] ||
                get().mMouseButtonPressed[1] ||
                get().mMouseButtonPressed[2];
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if(action == GLFW_PRESS) {
            if(button < get().mMouseButtonPressed.length) {
                get().mMouseButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE) {
            if(button < get().mMouseButtonPressed.length) {
                get().mMouseButtonPressed[button] = false;
                get().mIsDragging = false;
            }
        }
    }

    public static void mouseScrollCallback(long window, double offsetX, double offsetY) {
        get().mScrollX = offsetX;
        get().mScrollY = offsetY;
    }

    public static void endFrame() {
        get().mScrollX = 0;
        get().mScrollY = 0;
        get().mLastX = get().mPosX;
        get().mLastY = get().mPosY;
    }

    public static float getX() {
        return (float)get().mPosX;
    }

    public static float getY() {
        return (float)get().mPosY;
    }

    public static float getDx() {
        return (float)(get().mLastX - get().mPosX);
    }

    public static float getDy() {
        return (float)(get().mLastY - get().mPosY);
    }

    public static boolean isDragging() {
        return get().mIsDragging;
    }

    public static boolean isMouseButtonDown(int button) {
        if(button< get().mMouseButtonPressed.length) {
            return get().mMouseButtonPressed[button];
        } else {
            return false;
        }
    }

    private double mScrollX, mScrollY;
    private double mPosX, mPosY, mLastY, mLastX;
    private boolean mIsDragging;
    private final boolean[] mMouseButtonPressed = new boolean[3];

    private MouseListener() {
        mScrollX = mScrollY = 0f;
        mPosX = mPosY = 0f;
        mLastX = mLastY = 0f;
    }
}
