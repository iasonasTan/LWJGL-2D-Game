package game;

import game.util.Time;
import org.lwjgl.Version;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.lang.reflect.InvocationTargetException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Window {
    private static Window instance;
    private static Scene sCurrentScene;

    public static Window get() {
        if(instance==null)
            instance = new Window();

        return instance;
    }

    private final int WIDTH, HEIGHT;
    private final String TITLE;
    private long glfwWindow;
    private float r, g, b, a;

    private Window() {
        WIDTH = 1920;
        HEIGHT = 1080;
        TITLE = "Game";
        r = g = b = a = 1f;
    }

    public static void changeScene(Class<? extends Scene> type) {
        try {
            sCurrentScene = type.getConstructor().newInstance().init();
        } catch (InstantiationException | IllegalAccessException |
                 InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("all")
    public void run() {
        IO.println("Hello LWJGL "+ Version.getVersion() + "!");

        init();
        loop();

        // Free memory
        Callbacks.glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    @SuppressWarnings("all")
    public void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        glfwWindow = glfwCreateWindow(WIDTH, HEIGHT, TITLE, MemoryUtil.NULL, MemoryUtil.NULL);
        if (glfwWindow == MemoryUtil.NULL) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // Critical Line
        GL.createCapabilities();

        changeScene(LevelEditorScene.class);
    }

    public void loop() {
        float beginTime = Time.getTime();
        float endTime;
        float dt = -1f;

        while(!glfwWindowShouldClose(glfwWindow)) {
            // Poll events
            glfwPollEvents();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if(KeyListener.isKeyPressed(GLFW.GLFW_KEY_ENTER)){
                System.out.println("Space is pressed...");
                r = 1f;
            } else if(!KeyListener.isKeyPressed(GLFW.GLFW_KEY_ENTER)){
                System.out.println("Space is not longer pressed...");
                r = 0f;
            }

            if(dt >= 0) {
                sCurrentScene.update(dt);
            }

            glfwSwapBuffers(glfwWindow);

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    public void setBg(float r, float g, float b) {
        if(r>=0) this.r = r;
        if(g>=0) this.g = g;
        if(b>=0) this.b = b;
    }
}
