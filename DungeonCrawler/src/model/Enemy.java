package model;

import global.GLCamera;
import map.Textures;
import org.lwjgl.opengl.GL11;
import transforms.Vec3D;

import static org.lwjgl.opengl.GL11.*;

public class Enemy {
    private float[] position = new float[3];

    private int health;

    public Enemy(float x, float y, float z) {
        position[0] = x;
        position[1] = y;
        position[2] = z;
        this.health = 100;
    }

    public void draw(GLCamera camera) {

        if (health > 0) {
            double azimut = Math.PI * 2 - camera.getAzimuth();
            Vec3D dir = new Vec3D(position[0], position[1], position[2]).add(camera.getPosition().mul(-1));
            double atan = Math.atan2(dir.getZ(), dir.getX());
            double delta = (atan - Math.PI / 2) + azimut;



            glPushMatrix();
            glTranslatef(position[0], position[1], position[2]);
            glRotated(Math.toDegrees(azimut - delta), 0, 1, 0);

            glBegin(GL_QUADS);
            glTexCoord2f(0, 1);
            glVertex3f(-0.25f, -0.5f, 0);
            glTexCoord2f(1, 1);
            glVertex3f(0.25f, -0.5f, 0);
            glTexCoord2f(1, 0);
            glVertex3f(0.25f, 0.25f, 0);
            glTexCoord2f(0,0);
            glVertex3f(-0.25f, 0.25f, 0);
            glEnd();
            glPopMatrix();
        }
    }

    public int checkHealth() {
        return health;
    }

    public void decreaseHealth() {
        this.health = this.health - 50;
    }

    public double getPositionX() {
        return position[0];
    }

    public double getPositionY() {
        return position[1];
    }

    public double getPositionZ() {
        return position[2];
    }
}
