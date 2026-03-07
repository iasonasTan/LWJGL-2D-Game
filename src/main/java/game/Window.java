package game;

import game.util.Time;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

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
    public float r, g, b, a;

    private Window() {
        WIDTH = 1920;
        HEIGHT = 1080;
        TITLE = "Game";
        r = 1f;
        g = 1f;
        b = 1f;
        a = 1f;
    }

    public static void changeScene(int newS) {
        sCurrentScene = switch(newS) {
            case 0 -> new LevelEditorScene();
            case 1 -> new LevelScene();
            default -> throw new IllegalArgumentException("Unknown scene '"+newS+"'");
        };
    }

    @SuppressWarnings("all")
    public void run() {
        IO.println("Hello LWJGL "+ Version.getVersion() + "!");

        init();
        loop();

        // Free memory
        glfwFreeCallbacks(glfwWindow);
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
        glfwWindow = glfwCreateWindow(WIDTH, HEIGHT, TITLE, NULL, NULL);
        if (glfwWindow == NULL) {
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

        Window.changeScene(0);
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

            /*if(KeyListener.isKeyPressed(GLFW_KEY_SPACE)){
                System.out.println("Space is pressed...");
                r = 1f;
            } else if(!KeyListener.isKeyPressed(GLFW_KEY_SPACE)){
                System.out.println("Space is not longer pressed...");
                r = 0f;
            }*/

            if(dt >= 0) {
                sCurrentScene.update(dt);
            }

            glfwSwapBuffers(glfwWindow);

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }
}
