package utils;

import global.GLCamera;
import transforms.Vec3D;

public class CameraAnimation {
    private boolean inProgress;
    private int step;
    private long lastRun;
    private AnimState state;
    private int rotateDir;
    private Vec3D moveDir, moveTarget;
    private final float rotateTime = 400;
    private final float moveTime = 350;

    public CameraAnimation(){}

    public void rotate(int i){
        if (inProgress)return;
        inProgress = true;
        step = 0;
        lastRun = System.currentTimeMillis();
        state = AnimState.ROTATE;
        rotateDir = i;
    }

    public void move(Vec3D direction, GLCamera camera){
        if (inProgress)return;
        inProgress = true;
        step = 0;
        lastRun = System.currentTimeMillis();
        state = AnimState.MOVE;
        moveTarget = direction;
        moveDir = direction.add(camera.getPosition().mul(-1));
    }

    public void step(GLCamera camera){
        if (!inProgress)return;
        if (lastRun == 0){return;}
        long delta = System.currentTimeMillis()- lastRun;
        lastRun = System.currentTimeMillis();
        switch (state){
            case ROTATE -> {
                step += delta;
                camera.addAzimuth(((Math.PI/2.)*(delta/ rotateTime))* rotateDir);
                if (step > rotateTime){
                    inProgress = false;
                    camera.setAzimuth(Math.round(camera.getAzimuth()/(Math.PI/2.))*(Math.PI/2.));
                }

            }
            case MOVE -> {
                step += delta;
                camera.setPosition(camera.getPosition().add(moveDir.mul(delta/moveTime)));
                if (step > moveTime){
                    inProgress = false; //zabranuje animaci v animaci
                    camera.setPosition(moveTarget);
                }
            }
        }

    }
}

enum AnimState{
    ROTATE,
    MOVE
}
