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

/**
 * The basic text element of our user interface.
 */
public class MCUISimpleText {
    /**
     * Specifies the factor by which spaces between words need to be amplified to achieve an aesthetic appearance.
     */
    private final float SPACE_FACTOR = 2.5f;

    /**
     * The layout zone for this text element.
     */
    protected Zone zone;
    /**
     * The viewport used for coordinate transformations.
     */
    protected Viewport viewport;
    /**
     * The parent HUD component.
     */
    protected MCAbstractHUD parent;
    /**
     * The current SpriteBatch used for drawing.
     */
    protected SpriteBatch currentBatch;
    /**
     * The current text displayed by this element.
     */
    protected String currentText = "AAAHH";
    /**
     * The bitmap font used for rendering the text.
     */
    protected BitmapFont font;
    /**
     * The calculated dimensions (width and height) of the current text.
     */
    protected Vector2 dimensions;
    /**
     * The color of the text.
     */
    protected Color color;
    /**
     * The scale of the text.
     */
    protected float scale;
    /**
     * The spacing between characters.
     */
    protected float spacing;
    /**
     * The alpha (transparency) of the text.
     */
    protected float alpha = 1f;

    /**
     * The horizontal offset applied to the text.
     */
    protected float offsetX = 0f;
    /**
     * Indicates whether the text should be centered within its zone.
     */
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
     * Draws text with spacing between the characters.
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
     * Calculates the X and Y dimensions of the text.
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
     * Called on each frame. It uses manually computed scissors to ensure that the text does not overflow its boundaries.
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
     * Sets the scale of the text.
     *
     * @param scale The new scale for the text.
     */
    public void setScale(float scale) {
        this.scale = scale;
        setText(currentText); // maj des dimensions
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
     * Sets the X offset.
     *
     * @param offsetX The X offset to set.
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