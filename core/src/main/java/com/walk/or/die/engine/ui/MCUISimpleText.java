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

    public void setText(String text) {
        if (text.equals(currentText))
            return;
        currentText = text.trim();
        font.setColor(color);
        font.getData().setScale(scale);
        dimensions = textDimensions(currentText);
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public void update(float delta) {

    }
}
