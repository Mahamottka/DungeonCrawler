package model;

import lwjglutils.OGLTextRenderer;

public class Button {

    private OGLTextRenderer textRenderer;
    private int width, height;
    private int x,y;
    private String text;

    public Button(OGLTextRenderer textRenderer, int x, int y, String text) {
        this.textRenderer = textRenderer;
        this.x = x;
        this.y = y;
        this.text = text;
        int[] size = textRenderer.getStr2DSize(text);
        width = size[0];
        height = size[1];
    }

    public void draw(){
        textRenderer.addStr2D(x-width/2,y+height/2,text);
    }
    public boolean checkColision(int mX, int mY) {
        //Check if cursor in box
        return (mX > x - (width / 2f) && mX < x + (width / 2f)) && (mY > y - (height / 2f) && mY < y + (height / 2f));
    }


}
