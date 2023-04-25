package map;

import lwjglutils.OGLTexture2D;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LINEAR;

public class Textures {
    private static Textures instance = null;
    private OGLTexture2D wall, hallway, enemy;
    private Textures() {

    }
    public static Textures getInstance(){
        if (instance == null) {
            instance = new Textures();
        }
        return instance;
    }

    public OGLTexture2D getWall() {
        return wall;
    }

    public OGLTexture2D getHallway() {
        return hallway;
    }

    public OGLTexture2D getEnemy(){
        return enemy;
    }

    public void loadTextures(){
        System.out.println("Loading textures...");
        try {
            wall = new OGLTexture2D("textures/wall.png"); // vzhledem k adresari res v projektu
            hallway = new OGLTexture2D("textures/floor.png"); // vzhledem k adresari res v projektu
            enemy = new OGLTexture2D("textures/enemy.png");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
