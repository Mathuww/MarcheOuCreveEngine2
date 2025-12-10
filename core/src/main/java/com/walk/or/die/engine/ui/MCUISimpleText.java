package com.walk.or.die.engine.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.ui.MCUILayout.Zone;

public class MCUISimpleText {
    protected Zone zone;
    protected MCAbstractHUD parent;
    protected SpriteBatch currentBatch;
    protected String currentText = "AAAHH";
    protected BitmapFont font;
    protected Vector2 dimensions;
    protected Color color;
    protected float scale;
    protected float spacing;

    public MCUISimpleText(
        MCAbstractHUD parent, 
        BitmapFont font, 
        Zone zone, 
        Color color, 
        float scale, 
        float spacing
    ) {
        this.parent = parent;
        this.font = font;
        this.zone = zone;
        this.color = color;
        this.scale = scale;
        this.spacing = spacing;
    }

    protected void drawSpacedText(String text, float x, float y) {
        font.setColor(color);
        font.getData().setScale(scale);
        float cursor = x;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            GlyphLayout layout = new GlyphLayout(font, String.valueOf(c));
            font.draw(currentBatch, String.valueOf(c), cursor, y);
            cursor += layout.width + spacing;
            if (c == ' ')
                cursor += 2f * spacing;
        }
    }

    protected Vector2 textDimensions(String text) {
        GlyphLayout layout = new GlyphLayout(font, text);
        float realWidth = layout.width + spacing * (text.length() - 1);
        return new Vector2(realWidth, layout.height);
    }

    public void render(SpriteBatch batch) {
        currentBatch = batch;
        float y = zone.inY() + (zone.inHeight() - dimensions.y) / 2f;
        float x = zone.inX() + (zone.inWidth() - dimensions.x) / 2f;
        if (dimensions.x <= zone.inWidth()) {
                //System.out.println("not too large");
                drawSpacedText(currentText, x, y);
                return;
        } 

        Viewport hudViewport = MCHUDManager.get().getViewport();
        
        Vector3 ll = new Vector3(zone.inX(), zone.inY(), 0f);
        hudViewport.project(ll);

        Vector3 ur = new Vector3(
            zone.inX() + zone.inWidth(),
            zone.inY() + zone.inHeight(),
            0f
        );
        hudViewport.project(ur);

        int scissorWidth = (int) (ur.x - ll.x);
        int scissorHeight = (int) (ur.y - ll.y);
        int scissorX = (int) ll.x;
        int scissorY = (int) ll.y;
        
        Rectangle scissors = new Rectangle(scissorX, scissorY, scissorWidth, scissorHeight);
        
        currentBatch.flush();
        if (ScissorStack.pushScissors(scissors)) {
            drawSpacedText(currentText, x, y);
            currentBatch.flush();
            ScissorStack.popScissors();
        }
    }

    public void setText(String text) {
        if (text.equals(currentText))
            return;
        currentText = text;
        font.setColor(color);
        font.getData().setScale(scale);
        dimensions = textDimensions(text);
    }

    public void update(float delta) {

    }
}
