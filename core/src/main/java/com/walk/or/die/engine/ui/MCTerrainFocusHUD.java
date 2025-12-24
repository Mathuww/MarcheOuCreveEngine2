package com.walk.or.die.engine.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.entities.MCCharacter;

public class MCTerrainFocusHUD extends MCAbstractHUD {
    private final float CORNER_SIZE = 0.2f; // tile (unit viewport monde pas viewport HUD)

    private MCCharacter target;

    private Rectangle rect = new Rectangle();

    private final float BLINKING_INTERVAL = 0.65f;
    private float blinkingTime = 0f;

    private boolean display = true;

    public MCTerrainFocusHUD() {}

    public void setTarget(MCCharacter target) {
        //if (target != null)
            //System.out.println("focus hud target is now " + target.getId());
        this.target = target;
    }

    @Override
    public void update(float delta) {
        if (target == null)
            return;

        blinkingTime += delta;

        if (blinkingTime >= BLINKING_INTERVAL) {
            display = !display;
            blinkingTime = 0f;
        }   

        rect.x = target.getX() - CORNER_SIZE;
        rect.y = target.getY() - CORNER_SIZE;
        rect.width = target.getSize() + 2f * CORNER_SIZE;
        rect.height = target.getSize() + 2f * CORNER_SIZE;
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);

        if (target == null)
            return;

        if (!display)
            return;

        fillWhiteFourCorners(rect, CORNER_SIZE);
        drawFourCorners(rect, CORNER_SIZE);
    }

    public boolean isFullyShown() { return display; }
    public boolean posBelongsToHudComponent(Vector2 pos) { return false; }
    public void handleHover(Vector2 pos) {}
    public void handleHoverGone() {}
    public void handleClick(Vector2 pos) {}
}
