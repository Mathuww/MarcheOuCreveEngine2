package com.walk.or.die.engine.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.ui.MCUILayout.Zone;

/**
 * A text element that will make the text scroll if it overflows the zone.
 * @see MCUISimpleText
 */
public class MCUIScrollingText extends MCUISimpleText {
    private final float SCROLL_SPEED = 50f; // pixels/s
    private final float SCROLL_GAP = 30f;
    private final float FADE_WIDTH = 25f; 

    private TextureRegion gradientTexture;
    private float scrollX = 0f;

    /**
     * Constructs a scrolling text object.
     *
     * @param parent The parent HUD.
     * @param font The font to use.
     * @param zone The zone to render in.
     * @param color The color of the text.
     * @param scale The scale of the text.
     * @param spacing The spacing between characters.
     */
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

    /**
     * Draws the edge gradient for the scrolling text effect.
     */
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

    /**
     * Renders the scrolling text.
     *
     * @param batch The sprite batch to render with.
     */
    @Override
    public void render(SpriteBatch batch) {
        if (currentText == null || currentText == "")
            return;
        
        currentBatch = batch;

        //float ascent = -font.getData().ascent * font.getScaleY(); 
        float y = zone.inY() + (zone.inHeight() + dimensions.y) / 2f; // + ascent;
        
        Vector3 ll = new Vector3(zone.inX(), zone.inY(), 0f);
        viewport.project(ll);

        Vector3 ur = new Vector3(
            zone.inX() + zone.inWidth(),
            zone.inY() + zone.inHeight(),
            0f
        );
        viewport.project(ur);

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

    /**
     * Sets the text to display.
     *
     * @param text The text to set.
     */
    @Override
    public void setText(String text) {
        if (text.equals(currentText))
            return;
        super.setText(text);
        scrollX = 0f;
    }

    /**
     * Called on each frame
     *
     * @param delta The time in seconds since the last frame.
     */
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