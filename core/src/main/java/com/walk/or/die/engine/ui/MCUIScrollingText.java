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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.ui.MCUILayout.Zone;

public class MCUIScrollingText extends MCUISimpleText {
    private final float SCROLL_SPEED = 50f; // pixels/s
    private final float SCROLL_GAP = 30f;
    private final float FADE_WIDTH = 25f; 

    private TextureRegion gradientTexture;
    private float scrollX = 0f;

    public MCUIScrollingText(
        MCAbstractHUD parent, 
        BitmapFont font, 
        Zone zone, 
        Color color, 
        float scale, 
        float spacing
    ) {
        super(parent, font, zone, color, scale, spacing);

        try {
            gradientTexture = MCSharedAssets.get().getSavedTexture("whiteFade");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void edgeGradient() {
        currentBatch.draw(
            gradientTexture, 
            zone.inX(), 
            zone.inY(), 
            FADE_WIDTH, 
            zone.inHeight()
        );
        if (!gradientTexture.isFlipX())
            gradientTexture.flip(true, false); // flip x
    
        currentBatch.draw(
            gradientTexture, 
            zone.inX() + zone.inWidth() - FADE_WIDTH, 
            zone.inY(), 
            FADE_WIDTH, 
            zone.inHeight()
        );

        gradientTexture.flip(true, false);
    }

    @Override
    public void render(SpriteBatch batch) {
        currentBatch = batch;

        //float ascent = -font.getData().ascent * font.getScaleY(); 
        float y = zone.inY() + (zone.inHeight() + dimensions.y) / 2f; // + ascent;

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
            if (dimensions.x <= zone.inWidth()) {
                //System.out.println("not too large");
                float x = zone.inX() + (zone.inWidth() - dimensions.x) / 2f;
                drawSpacedText(currentText, x, y);
            } else {
                float x1 = zone.inX() + scrollX;
                float x2 = x1 + dimensions.x + SCROLL_GAP;
                drawSpacedText(currentText, x1, y);
                drawSpacedText(currentText, x2, y);
            }
            
            currentBatch.flush();
            ScissorStack.popScissors();
        }
        if (dimensions.x > zone.inWidth())
            edgeGradient();
    }

    @Override
    public void setText(String text) {
        if (text.equals(currentText))
            return;
        super.setText(text);
        scrollX = 0f;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (dimensions.x <= zone.inWidth()) {
            scrollX = 0f;
            return;
        }

        scrollX -= SCROLL_SPEED * delta;

        if (scrollX < -(dimensions.x + SCROLL_GAP)) {
            scrollX = 0f;
        }
    }
}
