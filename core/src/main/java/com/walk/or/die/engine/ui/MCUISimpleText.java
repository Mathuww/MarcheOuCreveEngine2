package com.walk.or.die.engine.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.walk.or.die.engine.ui.MCUILayout.Zone;

public class MCUISimpleText {
    private final float SPACE_FACTOR = 2.5f;

    protected Zone zone;
    protected Viewport viewport;
    protected MCAbstractHUD parent;
    protected SpriteBatch currentBatch;
    protected String currentText = "AAAHH";
    protected BitmapFont font;
    protected Vector2 dimensions;
    protected Color color;
    protected float scale;
    protected float spacing;
    protected float alpha = 1f;

    protected float offsetX = 0f;
    public boolean centered = true;

    /**
     * Constructs a MCUISimpleText.
     *
     * @param parent The parent HUD.
     * @param font The bitmap font.
     * @param zone The zone.
     * @param color The color.
     * @param scale The scale.
     * @param spacing The spacing.
     */
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

        viewport = MCHUDManager.get().getViewport();
    }

    /**
     * Draws spaced text.
     *
     * @param text The text to draw.
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    protected void drawSpacedText(String text, float x, float y) {
        font.setColor(color);
        font.getData().setScale(scale);
        float cursor = x + offsetX;
        for (char c : text.toCharArray()) {
            GlyphLayout layout = new GlyphLayout(font, String.valueOf(c));
            font.draw(currentBatch, String.valueOf(c), cursor, y);
            if (c == ' ')
                cursor += SPACE_FACTOR * spacing;
            else
                cursor += layout.width + spacing;  
        }
    }

    /**
     * Calculates the dimensions of the text.
     *
     * @param text The text to measure.
     * @return A Vector2 containing the width and height of the text.
     */
    public Vector2 textDimensions(String text) {
        font.getData().setScale(scale);
        float realWidth = 0f;
        for (char c : text.toCharArray()) {
            GlyphLayout layout = new GlyphLayout(font, String.valueOf(c));
            if (c == ' ')
                realWidth += SPACE_FACTOR * spacing;
            else
                realWidth += layout.width + spacing;  
        }
        GlyphLayout totalLayout = new GlyphLayout(font, text);
        return new Vector2(realWidth, totalLayout.height);
    }

    /**
     * Called on each frame.
     *
     * @param batch The sprite batch.
     */
    public void render(SpriteBatch batch) {
        if (currentText == null || currentText == "")
            return;

        currentBatch = batch;
        float y = zone.inY() + (zone.inHeight() + dimensions.y) / 2f;
        float x;
        if (centered)
            x = zone.inX() + (zone.inWidth() - dimensions.x) / 2f;
        else 
            x = zone.inX();
        
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
            drawSpacedText(currentText, x, y);
            currentBatch.flush();
            ScissorStack.popScissors();
        }
    }

    /**
     * Sets the text.
     *
     * @param text The text to set.
     */
    public void setText(String text) {
        if (text.equals(currentText))
            return;
        currentText = text.trim();
        font.setColor(color);
        font.getData().setScale(scale);
        dimensions = textDimensions(currentText);
    }

    /**
     * Sets the offset X.
     *
     * @param offsetX The offset X to set.
     */
    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    /**
     * Called on each frame.
     *
     * @param delta The time delta.
     */
    public void update(float delta) {

    }
}