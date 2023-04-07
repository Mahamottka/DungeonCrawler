package map;

import static global.GlutUtils.glutSolidCube;
import static org.lwjgl.opengl.GL11.*;

public class Labyrinth {
    private int[][] cells;

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
                    glColor3f(1, 1, 1);
                    glutSolidCube(1);
                } else if (id == 3) {
                    glPushMatrix();
                    glColor3f(0, 0, 139);
                    glTranslatef(0, 1f, 0); // moving the wall upwards to make it 3D
                    glutSolidCube(1);
                    glPopMatrix();
                }
                glPopMatrix();
            }
        }
    }


}
