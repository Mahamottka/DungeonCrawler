package model;

import global.AbstractRenderer;
import global.GLCamera;
import map.Convertion;
import map.Labyrinth;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import transforms.Vec3D;

import java.nio.DoubleBuffer;

import static global.GluUtils.gluPerspective;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class Renderer extends AbstractRenderer {
    private float dx, dy, ox, oy;
    private float zenit, azimut;

    private Labyrinth labyrinth;
    private Convertion data;

    private float trans = 0.3f; //camera speed

    private float uhel = 0;

    private boolean mouseButton1 = false;
    private boolean per = true, move = false;
    private int sky = 0;
    private GLCamera camera;

    public Renderer() {
        super();

        /*used default glfwWindowSizeCallback see AbstractRenderer*/

        glfwKeyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    // We will detect this in our rendering loop
                    glfwSetWindowShouldClose(window, true);

                if (action == GLFW_PRESS) {
                    switch (key) {
                        case GLFW_KEY_W:
                            camera.forward(trans);
                            break;

                        case GLFW_KEY_S:
                            camera.backward(trans);
                            break;

                        case GLFW_KEY_A:
                            camera.left(trans);
                            break;

                        case GLFW_KEY_D:
                            camera.right(trans);
                            break;
                    }
                }
            }
        };

        glfwMouseButtonCallback = new GLFWMouseButtonCallback() {

            @Override
            public void invoke(long window, int button, int action, int mods) {
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                double x = xBuffer.get(0);
                double y = yBuffer.get(0);

                mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;

                if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                    ox = (float) x;
                    oy = (float) y;
                }
            }

        };

        glfwCursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) {
                if (mouseButton1) {
                    dx = (float) x - ox;
                    dy = (float) y - oy;
                    ox = (float) x;
                    oy = (float) y;
                    azimut += dx / height * 180;
                    azimut = azimut % 360;
                    camera.setAzimuth(Math.toRadians(azimut));
                    dx = 0;
                    dy = 0;
                }
            }
        };

        glfwScrollCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double dx, double dy) {
                //do nothing
            }
        };
    }


    @Override
    public void init() {
        super.init();

        camera = new GLCamera();
        camera.setPosition(new Vec3D(3,1,3)); //z = -y, y = z // 0,5,0 -> pocatek s vyskou 5
        camera.setFirstPerson(true);

        scene();
    }

    private void scene(){
        glNewList(1,GL_COMPILE);
        data = new Convertion();
        labyrinth = new Labyrinth(data.getIdArray());
        labyrinth.render();
        glEndList();
    }

    @Override
    public void display() {
        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(90, width / (float) height, 0.1f, 500.0f);

        if (move) {
            uhel++;
        }

        //Tady se nastavuje kamera, nešahat
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glPushMatrix();
        camera.setMatrix();
        glRotatef(uhel, 0, 1, 0);
        glCallList(1);
        glPopMatrix();

    }
}
