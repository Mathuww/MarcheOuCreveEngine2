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

public class MCUIScrollingText {
    private final float SCROLL_SPEED = 50f; // pixels/s
    private final float SCROLL_GAP = 50f;
    private final float FADE_WIDTH = 25f; 

    private Rectangle initialZone;
    private Rectangle currentZone;
    private MCAbstractHUD parent;
    private SpriteBatch currentBatch;
    private TextureRegion gradientTexture;
    private String currentText = "AAAHH";
    private float scrollX = 0f;
    private BitmapFont font;
    private Vector2 dimensions;
    private Color color;
    private float scale;
    private float spacing;

    private ShapeRenderer debugRenderer = new ShapeRenderer();

    public MCUIScrollingText(MCAbstractHUD parent, BitmapFont font, Rectangle zone, Color color, float scale, float spacing) {
        this.parent = parent;
        this.font = font;
        this.initialZone = zone;
        this.currentZone = new Rectangle(initialZone);
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

    private Vector2 textDimensions(String text) {
        GlyphLayout layout = new GlyphLayout(font, text);
        float realWidth = layout.width + spacing * (text.length() - 1);
        return new Vector2(realWidth, layout.height);
    }

    private void edgeGradient(SpriteBatch batch) {
        batch.draw(gradientTexture, currentZone.x, currentZone.y, FADE_WIDTH, currentZone.height);
        if (!gradientTexture.isFlipX())
            gradientTexture.flip(true, false); // flip x
    
        batch.draw(gradientTexture, currentZone.x + currentZone.width - FADE_WIDTH, currentZone.y, FADE_WIDTH, currentZone.height);

        gradientTexture.flip(true, false);
    }

    public void render(SpriteBatch batch) {
        currentBatch = batch;


        System.out.println(currentZone.x + " " + currentZone.y + " " + currentZone.width + " " + currentZone.height);

        //debugRenderer.rect(currentZone.x, currentZone.y, currentZone.width, currentZone.height);


        float y = currentZone.y + (currentZone.height + dimensions.y) / 2f;
        if (dimensions.x <= currentZone.width) {
                //System.out.println("not too large");
                float x = currentZone.x + FADE_WIDTH / 2f  + (currentZone.width - dimensions.x) / 2f;
                drawSpacedText(currentText, x, y);
                return;
        }

        Camera hudCamera = MCHUDManager.get().getCamera();
        Viewport hudViewport = MCHUDManager.get().getViewport();

        
        Vector3 ll = new Vector3(currentZone.x, currentZone.y, 0f);
        //hudCamera.project(ll, hudViewport.getScreenX(), hudViewport.getScreenY(),
        //                    hudViewport.getWorldWidth(), hudViewport.getScreenHeight());
        hudViewport.project(ll);

        Vector3 ur = new Vector3(
            currentZone.x + currentZone.width,
            currentZone.y + currentZone.height,
            0f
        );
        //hudCamera.project(ur, hudViewport.getScreenX(), hudViewport.getScreenY(),
        //                    hudViewport.getWorldWidth(), hudViewport.getScreenHeight());
        hudViewport.project(ur);

        int scissorWidth = (int) (ur.x - ll.x);
        int scissorHeight = (int) (ur.y - ll.y);
        int scissorX = (int) ll.x;
        int scissorY = (int) ll.y;
        
        //ScissorStack.calculateScissors(hudCamera, batch.getTransformMatrix(), currentZone, scissors);
        Rectangle scissors = new Rectangle(scissorX, scissorY, scissorWidth, scissorHeight);
        System.out.println(scissors.x + " " + scissors.y + " " + scissors.width + " " + scissors.height);

    
        //Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        //Gdx.gl.glScissor((int)scissors.x, (int)scissors.y, (int)scissors.width, (int)scissors.height);
        
        currentBatch.flush();
        if (ScissorStack.pushScissors(scissors)) {
            
            /* debugRenderer.begin(ShapeType.Filled);
            debugRenderer.setProjectionMatrix(hudCamera.combined);
            debugRenderer.rect(0, 0, hudViewport.getWorldWidth(), hudViewport.getWorldHeight());
            //Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST); 

            debugRenderer.end(); */
            
            float x1 = currentZone.x + scrollX;
            float x2 = x1 + dimensions.x + SCROLL_GAP;

            drawSpacedText(currentText, x1, y);
            drawSpacedText(currentText, x2, y);
            currentBatch.flush();
            ScissorStack.popScissors();
        }
        edgeGradient(currentBatch);

        //System.out.println("too large");
        /*
        float x1 = currentZone.x + scrollX;
        float x2 = x1 + dimensions.x + SCROLL_GAP;

        drawSpacedText(currentText, x1, y);
        drawSpacedText(currentText, x2, y);

        currentBatch.flush();
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
        */
        //edgeGradient(batch);

        /*
        System.out.println("zone = " + drawingZone);
        System.out.println("cam pos = " + MCHUDManager.get().getCamera().position);
        System.out.println("viewport size = " + MCHUDManager.get().getCamera().viewportWidth + " x " + MCHUDManager.get().getCamera().viewportHeight);
        */
        System.out.println("Viewport: " + hudViewport.getWorldWidth() + " x " + hudViewport.getWorldHeight());
        System.out.println("Camera pos: " + hudCamera.position);

    }

    public void setText(String text) {
        if (text.equals(currentText))
            return;
        currentText = text;
        dimensions = textDimensions(text);
        scrollX = 0f;
    }

    public void update(float delta) {
        System.out.println("offset y : " + parent.getOffsetY());
        currentZone.y = initialZone.y + parent.getOffsetY();

        if (dimensions.x <= currentZone.width) {
            scrollX = 0f;
            return;
        }

        scrollX -= SCROLL_SPEED * delta;

        if (scrollX < -(dimensions.x + SCROLL_GAP)) {
            scrollX = 0f;
        }
    }
}
