package com.walk.or.die.engine.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.walk.or.die.engine.shared.MCSharedAssets;

public class MCUIScrollingText {
    private final float SCROLL_SPEED = 50f; // pixels/s
    private final float SCROLL_GAP = 50f;
    private final float FADE_WIDTH = 25f; 

    public Rectangle drawingZone;

    private SpriteBatch currentBatch;
    private TextureRegion gradientTexture;
    private String currentText = "AAAHH";
    private float scrollX = 0f;
    private BitmapFont font;
    private Color color;
    private float scale;
    private float spacing;

    private ShapeRenderer debugRenderer = new ShapeRenderer();

    public MCUIScrollingText(BitmapFont font, Rectangle zone, Color color, float scale, float spacing) {
        this.font = font;
        this.drawingZone = zone;
        this.color = color;
        this.scale = scale;
        this.spacing = spacing;

        try {
            gradientTexture = MCSharedAssets.get().getSavedTexture("whiteFade");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawSpacedText(String text, float x, float y) {
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

    private float textWidth(String text) {
        GlyphLayout layout = new GlyphLayout(font, text);
        float realWidth = layout.width + spacing * (text.length() - 1);
        return realWidth;
    }

    private void edgeGradient(SpriteBatch batch) {
        batch.draw(gradientTexture, drawingZone.x, drawingZone.y, FADE_WIDTH, drawingZone.height);
        if (!gradientTexture.isFlipX())
            gradientTexture.flip(true, false); // flip x
    
        batch.draw(gradientTexture, drawingZone.x + drawingZone.width - FADE_WIDTH, drawingZone.y, FADE_WIDTH, drawingZone.height);

        gradientTexture.flip(true, false);
    }

    public void render(SpriteBatch batch) {
        currentBatch = batch;

        /*
        Color previousColor = batch.getColor().cpy();
        batch.setColor(Color.PURPLE);
        if (gradientTexture != null) {
            batch.draw(
                gradientTexture,
                drawingZone.x,
                drawingZone.y,
                drawingZone.width,
                drawingZone.height
            );
        }
        batch.setColor(previousColor);
        */

        font.setColor(color);
        font.getData().setScale(scale);

        GlyphLayout layout = new GlyphLayout(font, currentText);
        float textWidth = textWidth(currentText);
        float y = drawingZone.y + (drawingZone.height + layout.height) / 2f;
        
        /*
        Vector3 tmp = new Vector3(drawingZone.x, drawingZone.y, 0);
        MCHUDManager.get().getCamera().project(tmp);

        Vector3 tmp2 = new Vector3(drawingZone.x + drawingZone.width,
                                drawingZone.y + drawingZone.height,
                                0);
        MCHUDManager.get().getCamera().project(tmp2);

        Rectangle scissors = new Rectangle(
                tmp.x,
                tmp.y,
                tmp2.x - tmp.x,
                tmp2.y - tmp.y
        );
        */
        /*
        Rectangle scissors = new Rectangle(
                drawingZone.x,
                drawingZone.y,
                drawingZone.width,
                drawingZone.height
        );
        */
       Rectangle scissors = new Rectangle();
        batch.flush();
        ScissorStack.calculateScissors(MCHUDManager.get().getCamera(), batch.getTransformMatrix(), drawingZone, scissors);

        /*
        debugRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.RED);
        debugRenderer.rect(scissors.x, scissors.y, scissors.width, scissors.height);
        debugRenderer.end();
        */
        
        if (ScissorStack.pushScissors(scissors)) {
            if (textWidth <= drawingZone.width) {
                //System.out.println("not too large");
                float x = drawingZone.x + FADE_WIDTH / 2f  + (drawingZone.width - textWidth) / 2f;
                drawSpacedText(currentText, x, y);
            } else {
                //System.out.println("too large");
                float x1 = drawingZone.x + scrollX;
                float x2 = x1 + textWidth + 50f;

                drawSpacedText(currentText, x1, y);
                drawSpacedText(currentText, x2, y);
            }

            batch.flush();

            ScissorStack.popScissors();
        }

        edgeGradient(batch);

        /*
        System.out.println("zone = " + drawingZone);
        System.out.println("cam pos = " + MCHUDManager.get().getCamera().position);
        System.out.println("viewport size = " + MCHUDManager.get().getCamera().viewportWidth + " x " + MCHUDManager.get().getCamera().viewportHeight);
        */
    }

    public void setText(String text) {
        if (text.equals(currentText))
            return;
        currentText = text;
        scrollX = 0f;
    }

    public void update(float delta) {
        float textWidth = textWidth(currentText);

        if (textWidth <= drawingZone.width) {
            scrollX = 0f;
            return;
        }

        scrollX -= SCROLL_SPEED * delta;

        if (scrollX < -(textWidth + SCROLL_GAP)) {
            scrollX = 0f;
        }
    }
}
