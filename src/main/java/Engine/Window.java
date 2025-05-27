package Engine;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.util.Timer;
import java.util.TimerTask;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private int width, height;
    private String  title;
    private long glfwWindow;

    private float r, g, b, a;
    private boolean fadetoBlack = false;
    private boolean fadeTimerRunning = false;

    private static Window window  = null;

    private Window() {
        this.width = 1280;
        this.height = 720;
        this.title = "Engine";
        r = 0;
        g = 1;
        b = 1;
        a = 1;
    }
    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }

        return  Window.window;
    }

    public void run() {
        System.out.println("Hello LWJGL" + Version.getVersion() + "!");

        init();

        loop();

        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if(!glfwInit()) {
            throw new IllegalStateException("unable to init GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw  new IllegalStateException("Failed to create GLFW window");
        }

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        glfwMakeContextCurrent(glfwWindow);
        glfwSwapInterval(1);

        //make window visible
        glfwShowWindow(glfwWindow);

        GL.createCapabilities();
    }

    public void loop() {
        while(!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents();

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if(fadetoBlack) {
                r = Math.max(r - 0.01f, 0);
                g = Math.max(g - 0.01f, 0);
                b = Math.max(b - 0.01f, 0);
            }
            if(!fadetoBlack){
                r = Math.min(r + 0.01f, 0);
                g = Math.min(g + 0.01f, 1);
                b = Math.min(b + 0.01f, 1);
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
                fadetoBlack = true;

                Timer timer = new Timer(true);

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        fadetoBlack = false;
                        fadeTimerRunning = false;
                    }
                }, 5000);
            }

            glfwSwapBuffers(glfwWindow);
        }
    }
}
