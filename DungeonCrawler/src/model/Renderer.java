package model;

import global.AbstractRenderer;
import global.GLCamera;
import lwjglutils.OGLTextRenderer;
import lwjglutils.OGLTexture2D;
import map.Convertion;
import map.Labyrinth;
import map.Textures;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import transforms.Camera;
import transforms.Vec3D;
import utils.CameraAnimation;

import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.Arrays;

import static global.GluUtils.gluPerspective;
import static global.GlutUtils.glutSolidCube;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class Renderer extends AbstractRenderer {
    private float dx, dy, ox, oy;
    private float zenit, azimut;
    private OGLTexture2D wall, hallway;
    private Labyrinth labyrinth;
    private Convertion data;
    private CameraAnimation cameraAnimation;

    private float trans = 1f; //camera speed

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
                    int[] movementVec = {0,0};
                    switch (key) {
                        case GLFW_KEY_W:
                            movementVec[0] = 1;
                            break;
                        case GLFW_KEY_S:
                            movementVec[0] = -1;
                            break;
                        case GLFW_KEY_A:
                            movementVec[1] = -1;
                            break;
                        case GLFW_KEY_D:
                            movementVec[1] = 1;
                            break;
                        case GLFW_KEY_E:
                            cameraAnimation.rotate(1);
                            break;
                        case GLFW_KEY_Q:
                            cameraAnimation.rotate(-1);
                            break;
                    }
                    Vec3D res = move(movementVec);
                    if (!res.eEquals(new Vec3D())){
                        cameraAnimation.move(res, camera);
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
        cameraAnimation = new CameraAnimation();

        textRenderer = new OGLTextRenderer(width, height);

        glEnable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glFrontFace(GL_CCW);
        glPolygonMode(GL_FRONT, GL_FILL);
        glPolygonMode(GL_BACK, GL_FILL);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        Textures.getInstance().loadTextures();

        camera = new GLCamera();
        camera.setPosition(new Vec3D(3f, 1, 3)); //z = -y, y = z // 0,5,0 -> pocatek s vyskou 5
        camera.setFirstPerson(true);

        scene();
    }

    private void scene() {
        glNewList(1, GL_COMPILE);
        glEnable(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_NEAREST);
        data = new Convertion();
        labyrinth = new Labyrinth(data.getIdArray());
        labyrinth.render();
        glEndList();
    }


    @Override
    public void display() {
        cameraAnimation.step(camera);
        glEnable(GL_DEPTH_TEST);
        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(90, width / (float) height, 0.1f, 500.0f);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        //skybox?


        //Tady se nastavuje kamera, nešahat
        glPushMatrix();
        camera.setMatrix();
        glCallList(1);
        glPopMatrix();

        //enemy
        glEnable(GL_TEXTURE_2D);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);

        /*
        for (Ghost g : ghosts) {
            g.render(360 - (float) Math.toDegrees(player.getAzimuth()));
        }
        */

        glDisable(GL_TEXTURE_2D);
        glPopMatrix();

    }

    private Vec3D move(int[] vec){
        //Player movement
        Vec3D newPosition = camera.getPosition();
        double azimuth = camera.getAzimuth();

        //Dopředu dozadu
        newPosition = newPosition.add(new Vec3D(Math.sin(azimuth), 0, -Math.cos(azimuth)).mul(vec[0]));
        //Pravo lev
        newPosition = newPosition.add(new Vec3D(-Math.sin(azimuth - Math.PI / 2), 0, Math.cos(azimuth - Math.PI / 2)).mul(vec[1]));

        System.out.println(newPosition);
        if (!data.isCollision((int)Math.round(newPosition.getX()),(int)Math.round(newPosition.getZ()))){
            return newPosition;
        }
        return new Vec3D();
    }
}

