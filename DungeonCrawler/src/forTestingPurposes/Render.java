package forTestingPurposes;

import global.AbstractRenderer;
import global.GLCamera;
import lwjglutils.OGLTexture2D;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import java.nio.DoubleBuffer;

import static global.GluUtils.gluLookAt;
import static global.GluUtils.gluPerspective;
import static global.GlutUtils.glutSolidSphere;
import static global.GlutUtils.glutWireSphere;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;

public class Render extends AbstractRenderer {

    private float uhel = 0;
    private int mode = 0;
    private float[] modelMatrix = new float[16];

    private boolean mouseButton1 = false;

    private float dx, dy, ox, oy;
    private float zenit, azimut;

    private float trans, deltaTrans = 0;


    private boolean per = true, move = false;
    private int sky = 0;

    private OGLTexture2D texture;
    private OGLTexture2D[] textureCube;
    private OGLTexture2D.Viewer textureViewer;
    private GLCamera camera;

    public Render() {
        super();

        /*used default glfwWindowSizeCallback see AbstractRenderer*/

        glfwKeyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    // We will detect this in our rendering loop
                    glfwSetWindowShouldClose(window, true);
                if (action == GLFW_RELEASE) {
                    trans = 0;
                    deltaTrans = 0;
                }

                if (action == GLFW_PRESS) {
                    switch (key) {
                        case GLFW_KEY_P:
                            per = !per;
                            break;
                        case GLFW_KEY_M:
                            move = !move;
                            break;
                        case GLFW_KEY_K:
                            sky = (sky + 1) % 3;
                            break;
                        case GLFW_KEY_W:
                        case GLFW_KEY_S:
                        case GLFW_KEY_A:
                        case GLFW_KEY_D:
                            deltaTrans = 0.001f;
                            break;
                    }
                }
                switch (key) {
                    case GLFW_KEY_W:
                        camera.forward(trans);
                        if (deltaTrans < 0.001f)
                            deltaTrans = 0.001f;
                        else
                            deltaTrans *= 1.02;
                        break;

                    case GLFW_KEY_S:
                        camera.backward(trans);
                        if (deltaTrans < 0.001f)
                            deltaTrans = 0.001f;
                        else
                            deltaTrans *= 1.02;
                        break;

                    case GLFW_KEY_A:
                        camera.left(trans);
                        if (deltaTrans < 0.001f)
                            deltaTrans = 0.001f;
                        else
                            deltaTrans *= 1.02;
                        break;

                    case GLFW_KEY_D:
                        camera.right(trans);
                        if (deltaTrans < 0.001f)
                            deltaTrans = 0.001f;
                        else
                            deltaTrans *= 1.02;
                        break;
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
                    zenit -= dy / width * 180;
                    if (zenit > 90)
                        zenit = 90;
                    if (zenit <= -90)
                        zenit = -90;
                    azimut += dx / height * 180;
                    azimut = azimut % 360;
                    camera.setAzimuth(Math.toRadians(azimut));
                    camera.setZenith(Math.toRadians(zenit));
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
    public void display() {
        glViewport(0, 0, width, height);

        // mazeme image buffer i z-buffer
        glClearColor(0f, 0f, 0f, 1f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        uhel++;
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glRotatef(uhel, 1, 0, 0);
        glTranslatef(0,5,5);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        // nastaveni transformace zobrazovaciho objemu
        gluPerspective(45, width / (float) height, 0.1f, 100.0f);

        // pohledova transformace
        // divame se do sceny z kladne osy x, osa z je svisla
        //prvni tri param pozice kamery, druhy tri BOD, kam se koukam, poslední tři řeší up vektor
        gluLookAt(5, 5, 15, 0, 0, 0, 0, 0, 1);

        glColor3f(0,0,1f);
        glutSolidSphere(1,30,30);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glRotatef(uhel, 0, 1, 0);
        glTranslatef(5,0,5);

        glColor3f(0,1f,0);
        glutWireSphere(1,30,30);

        glLoadIdentity();

        glBegin(GL_LINES);
        glColor3f(1f, 0f, 0f);
        glVertex3f(0f, 0f, 0f);
        glVertex3f(100f, 0f, 0f);
        glColor3f(0f, 1f, 0f);
        glVertex3f(0f, 0f, 0f);
        glVertex3f(0f, 100f, 0f);
        glColor3f(0f, 0f, 1f);
        glVertex3f(0f, 0f, 0f);
        glVertex3f(0f, 0f, 100f);
        glEnd();

        float[] color = {1.0f, 1.0f, 1.0f};
        glColor3fv(color);
        glDisable(GL_DEPTH_TEST);

        String text = this.getClass().getName() + ": [Mouse] [M]ode: " + mode + " ";
        if (per)
            text += "[P]ersp, ";
        else
            text += "[p]ersp, ";




        //create and draw text
        textRenderer.clear();
        textRenderer.addStr2D(3, 20, text);
        textRenderer.addStr2D(width - 90, height - 3, " (c) PGRF UHK");
        textRenderer.draw();
    }

}
