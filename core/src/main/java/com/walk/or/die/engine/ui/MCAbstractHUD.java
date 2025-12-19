package com.walk.or.die.engine.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.ui.MCUILayout.Zone;

public abstract class MCAbstractHUD {
    protected SpriteBatch currentBatch;
    protected final MCSharedAssets sharedAssets = MCSharedAssets.get();
    protected TextureRegion blackTexture;
    protected TextureRegion whiteTexture;
    protected TextureRegion greyTexture;

    public MCAbstractHUD() {
        try {
            blackTexture = sharedAssets.getSavedTexture("black");
            whiteTexture = sharedAssets.getSavedTexture("white");
            greyTexture = sharedAssets.getSavedTexture("grey");
        } catch (Exception e) {
            System.err.println("cant load character hud assets");
            e.printStackTrace();
        }
    }

    public abstract void update(float delta);

    public void inputPressed(Command cmd) {
        // jouer un petit son quand on interagit avec le hud
    }

    public void render(SpriteBatch batch) {
        currentBatch = batch;
    }
    
    public void drawFilledRectangle(Rectangle rect, TextureRegion innerTexture) {
        currentBatch.draw(innerTexture, rect.x, rect.y, rect.width, rect.height);
    }

    protected void drawCorner(float x, float y, float cornerSize) {
        float tiersCorner = cornerSize / 3f;
        currentBatch.draw(blackTexture, x + tiersCorner, y, tiersCorner, tiersCorner);
        currentBatch.draw(blackTexture, x, y + tiersCorner, tiersCorner, tiersCorner);
        currentBatch.draw(blackTexture, x + tiersCorner * 2f, y + tiersCorner, tiersCorner, tiersCorner);
        currentBatch.draw(blackTexture, x + tiersCorner, y + tiersCorner * 2f, tiersCorner, tiersCorner);
    }    

    public void fillWhiteFourCorners(Rectangle rect, float borderSize) {
        currentBatch.draw(whiteTexture, rect.x, rect.y, borderSize, borderSize);
        currentBatch.draw(whiteTexture, rect.x, rect.y + rect.height - borderSize, borderSize, borderSize);
        currentBatch.draw(whiteTexture, rect.x + rect.width - borderSize, rect.y, borderSize, borderSize);
        currentBatch.draw(whiteTexture, rect.x + rect.width - borderSize, rect.y + rect.height - borderSize, borderSize, borderSize);
    }

    public void drawFourCorners(Rectangle rect, float borderSize) {
        drawCorner(rect.x, rect.y, borderSize);
        drawCorner(rect.x, rect.y + rect.height - borderSize, borderSize);
        drawCorner(rect.x + rect.width - borderSize, rect.y, borderSize);
        drawCorner(rect.x + rect.width - borderSize, rect.y + rect.height - borderSize, borderSize);
    }

    public void drawCornerlessRectangle(Zone zone, float borderSize) {
        drawCornerlessRectangle(zone, borderSize, whiteTexture);
    }

    public void drawCornerlessRectangle(Zone zone, float borderSize, TextureRegion innerTexture) {
        Rectangle rect = zone.outside();
        drawFilledRectangle(rect, innerTexture);

        float tiersBorder = borderSize / 3f;
        // bord inférieur
        currentBatch.draw(blackTexture, rect.x + borderSize, rect.y, rect.width - borderSize * 2f, tiersBorder);
        currentBatch.draw(blackTexture, rect.x + borderSize, rect.y + tiersBorder * 2f, rect.width - borderSize * 2f, tiersBorder);
        // bord supérieur
        currentBatch.draw(blackTexture, rect.x + borderSize, rect.y + rect.height - borderSize, rect.width - borderSize * 2f, tiersBorder);
        currentBatch.draw(blackTexture, rect.x + borderSize, rect.y + rect.height - tiersBorder, rect.width - borderSize * 2f, tiersBorder);
        // bord gauche
        currentBatch.draw(blackTexture, rect.x, rect.y + borderSize, tiersBorder, rect.height - borderSize * 2f);
        currentBatch.draw(blackTexture, rect.x + tiersBorder * 2f, rect.y + borderSize, tiersBorder, rect.height - borderSize * 2f);
        // bord droit
        currentBatch.draw(blackTexture, rect.x + rect.width - borderSize, rect.y + borderSize, tiersBorder, rect.height - borderSize * 2f);
        currentBatch.draw(blackTexture, rect.x + rect.width - tiersBorder, rect.y + borderSize, tiersBorder, rect.height - borderSize * 2f);

        drawFourCorners(rect, borderSize);
    }

    public abstract boolean isFullyShown();
    public abstract boolean posBelongsToHudComponent(Vector2 pos);
    public abstract void handleHover(Vector2 pos);
    public abstract void handleHoverGone();
    public abstract void handleClick(Vector2 pos);
}
