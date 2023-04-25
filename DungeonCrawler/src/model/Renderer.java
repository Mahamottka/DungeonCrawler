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
import java.awt.*;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static global.GluUtils.gluPerspective;
import static global.GlutUtils.glutSolidCube;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class Renderer extends AbstractRenderer {

    private int[] mPos = {0,0};
    private Button startButton, exitButton, restartButton, menuButton;
    private Labyrinth labyrinth;
    private GameState gameState;
    private Convertion data;
    private CameraAnimation cameraAnimation;

    private float trans = 1f; //camera speed

    private float uhel = 0;

    private boolean mouseButton1 = false;
    private boolean per = true, move = false;
    private int sky = 0;
    private List<Enemy> enemyList;
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

                if (action == GLFW_PRESS && gameState == GameState.GAME) {
                    int[] movementVec = {0, 0};
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
                        case GLFW_KEY_P:
                            checkForEnemy(enemyList);
                            break;
                    }
                    Vec3D res = move(movementVec);
                    if (!res.eEquals(new Vec3D())) {
                        cameraAnimation.move(res, camera);
                    }
                }
            }
        };

        glfwMouseButtonCallback = new GLFWMouseButtonCallback() {

            @Override
            public void invoke(long window, int button, int action, int mods) {
                if (button == 0 && action == 1) {
                    if (gameState == GameState.MAIN_MENU) { //pro main menu
                        if (startButton.checkColision(mPos[0],mPos[1])) {
                            gameState = GameState.GAME;
                            newGame();
                        } else if (exitButton.checkColision(mPos[0], mPos[1])){
                            glfwSetWindowShouldClose(window, true);
                        }
                    } else if (gameState == GameState.END_SCREEN){
                        if (restartButton.checkColision(mPos[0], mPos[1])){
                            gameState = GameState.GAME;
                            newGame();
                        } else if (menuButton.checkColision(mPos[0], mPos[1])) {
                            gameState = GameState.MAIN_MENU;
                        }
                    }
                }
            }

        };
        glfwCursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                mPos[0] = (int)xpos;
                mPos[1] = (int)ypos;
            }
        };
    }

    @Override
    public void init() {
        super.init();
        gameState = GameState.MAIN_MENU;
        cameraAnimation = new CameraAnimation();
        textRenderer = new OGLTextRenderer(width, height, new Font("SanSerif", Font.PLAIN,20));
        startButton = new Button(textRenderer, width / 2, height / 2, "Start Game");
        exitButton = new Button(textRenderer, width / 2, height / 2 + 40, "Exit Game");
        restartButton = new Button(textRenderer, width / 2, height / 2, "Restart Game");
        menuButton = new Button(textRenderer, width / 2, height / 2 + 40, "Back To Menu");

        glEnable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glFrontFace(GL_CCW);
        glPolygonMode(GL_FRONT, GL_FILL);
        glPolygonMode(GL_BACK, GL_FILL);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        Textures.getInstance().loadTextures();
    }

    private void newGame() {
        //tohle chci dělat víckrat
        camera = new GLCamera();
        camera.setPosition(new Vec3D(3f, 1, 3f)); //z = -y, y = z // 0,5,0 -> pocatek s vyskou 5
        camera.setFirstPerson(true);

        //vyčistění a spawnovani po restartu again
        enemyList = new ArrayList<>();
        scene();
    }

    private void scene() {
        glNewList(1, GL_COMPILE);
        glEnable(GL_TEXTURE_2D);
        data = new Convertion();
        labyrinth = new Labyrinth(data.getIdArray());
        labyrinth.render();
        glEndList();


        Enemy enemy = new Enemy(4, 1, 4);
        enemyList.add(enemy);

        Enemy enemy1 = new Enemy(9,1,7);
        enemyList.add(enemy1);

    }

    private void checkForEnemy(List<Enemy> enemyList) {
        List<Enemy> eTemp = new ArrayList<>();
        GLCamera temp = new GLCamera(camera);
        temp.forward(1f);
        for (Enemy e : enemyList) {
            if (Math.round(e.getPositionX()) == Math.round(temp.getPosition().getX())
                    && Math.round(e.getPositionZ()) == Math.round(temp.getPosition().getZ())) {
                e.decreaseHealth();
                System.out.println("Zásah!");
                System.out.println("Aktuální zdraví:" + e.checkHealth());
            } if (e.checkHealth() <= 0){
                eTemp.add(e);
            }
            System.out.println("Utok pozice" + temp.getPosition());
        }
        for (int i = 0; i < eTemp.size(); i++) {
            enemyList.remove(eTemp.get(i));
        }
        if (enemyList.size() == 0){
            gameState = GameState.END_SCREEN;
        }
    }

    @Override
    public void display() {
        switch (gameState) {

            case MAIN_MENU -> {
                menuRender();
            }
            case GAME -> {
                gameRender();
            }
            case DEATH_SCREEN -> {
                //deathRender();
            }
            case END_SCREEN -> {
                endRender();
            }
        }
    }

    private void menuRender() {
        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, 0, height, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        textRenderer.clear();
        startButton.draw();
        exitButton.draw();
    }

    private void endRender(){
        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, 0, height, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        textRenderer.clear();
        menuButton.draw();
        restartButton.draw();
        textRenderer.addStr2D(width/3, height/2 - 60, "Amazing job, you did it!");
    }

    private void gameRender() {
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

        //enemy
        glEnable(GL_TEXTURE_2D);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
        for (Enemy e : enemyList) {
            e.draw(camera);
        }
        glDisable(GL_TEXTURE_2D);
        glPopMatrix();

    }

    private Vec3D move(int[] vec) {
        //Player movement
        Vec3D newPosition = camera.getPosition();
        double azimuth = camera.getAzimuth();

        //Dopředu dozadu
        newPosition = newPosition.add(new Vec3D(Math.sin(azimuth), 0, -Math.cos(azimuth)).mul(vec[0]));
        //Pravo lev
        newPosition = newPosition.add(new Vec3D(-Math.sin(azimuth - Math.PI / 2), 0, Math.cos(azimuth - Math.PI / 2)).mul(vec[1]));

        System.out.println("Pozice kamery: " + camera.getPosition());
        if (!data.isCollision((int) Math.round(newPosition.getX()), (int) Math.round(newPosition.getZ()))) {
            if (!npcCollision((int) Math.round(newPosition.getX()), (int) Math.round(newPosition.getZ()))) {
                return newPosition;
            }
        }
        return new Vec3D();
    }

    private boolean npcCollision(int x, int z) {
        for (Enemy e : enemyList) {
            if (x == Math.round(e.getPositionX()) && z == Math.round(e.getPositionZ())) {
                return true;
            }
        }
        return false;
    }
}

