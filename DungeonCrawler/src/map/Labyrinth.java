package map;

import lwjglutils.OGLTexture2D;

import static global.GlutUtils.glutSolidCube;
import static org.lwjgl.opengl.GL11.*;

public class Labyrinth {
    private final int[][] cells;

    public Labyrinth(int[][] cells) {
        this.cells = cells;
    }

    public void render() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                int id = cells[i][j];
                glPushMatrix();
                glTranslatef(j, 0, i);
                if (id == 1 || id == 2) {
                    Textures.getInstance().getHallway().bind();
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                    glutSolidCube(1);
                } else if (id == 3) {
                    Textures.getInstance().getWall().bind();
                    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                    glPushMatrix();
                    glTranslatef(0, 1f, 0); // moving the wall upwards to make it 3D
                    glutSolidCube(1);
                    glPopMatrix();
                }
                glPopMatrix();
            }
        }
    }
}
