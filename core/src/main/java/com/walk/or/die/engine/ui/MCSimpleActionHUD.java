package com.walk.or.die.engine.ui;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCCharacter.HudCustomization;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.ui.MCUILayout.Zone;

public class MCSimpleActionHUD extends MCAbstractHUD {
    private final String FONT_FAMILY = "ariBlackAlpha";

    private final float HUD_HEIGHT = MCGame.WINDOW_DEFAULT_HEIGHT * 0.095f;
    private final float HUD_TOP_MARGIN = MCGame.WINDOW_DEFAULT_HEIGHT * 0.05f;
    private final float HUD_RECT_BORDER = MCGame.WINDOW_DEFAULT_HEIGHT * 0.0125f;
    private final float HUD_PADDING_HEIGHT  = MCGame.WINDOW_DEFAULT_HEIGHT * 0.025f;
    private final float HUD_PADDING_WIDTH  = MCGame.WINDOW_DEFAULT_HEIGHT * 0.05f;

    private final float HUD_RIGHT_PANEL_RATIO = 0.25f;

    private final float RIGHT_PANEL_PADDING_WIDTH = HUD_RECT_BORDER * 2f;
    private final float RIGHT_PANEL_PADDING_HEIGHT = HUD_RECT_BORDER * 2f;
    private final float FONT_SCALE = 0.35f;
    private final float FONT_SPACING = 3f;

    private final float SCROLL_LERP = 16f;
    private final float SCROLL_Y = HUD_TOP_MARGIN + HUD_HEIGHT;

    private BitmapFont font;
    private MCUILayout layout = new MCUILayout();

    private MCUISimpleText text;

    private float offsetY = SCROLL_Y;
    private float targetOffsetY = SCROLL_Y;
    private boolean scrolling = false;
    private boolean shown = false;

    private boolean hovered = false;

    private Runnable callback;

    private final MCEventBus bus = MCEventBus.get();

    public MCSimpleActionHUD() {
        try {
            font = sharedAssets.getSavedFont(FONT_FAMILY);
        } catch (Exception e) {
            System.err.println("cant load character hud assets");
            e.printStackTrace();
        }
        
        layout.addZone(
            "nextTurnZone",
            new Rectangle(
                0f,
                MCGame.WINDOW_DEFAULT_HEIGHT - HUD_TOP_MARGIN - HUD_HEIGHT,
                MCGame.WINDOW_DEFAULT_WIDTH,
                HUD_HEIGHT
            )
        );
        layout.zone("nextTurnZone").pad(HUD_PADDING_WIDTH, 0f);

        layout.splitX("nextTurnZone", 1 - HUD_RIGHT_PANEL_RATIO, 0f, "uselessQuantumVoidSpace", "nextTurnPanel");
        layout.zone("nextTurnPanel").pad(RIGHT_PANEL_PADDING_WIDTH, RIGHT_PANEL_PADDING_HEIGHT);

        text = new MCUISimpleText(
            this,
            font,
            layout.zone("nextTurnPanel"),
            Color.BLACK,
            FONT_SCALE,
            FONT_SPACING
        );
        text.setText("END TURN");

        enable(); // pour tester
    }

    public void setText(String newText) {
        text.setText(newText);
    }

    private void scrollTo(float targetOffsetY) {
        if (this.targetOffsetY == targetOffsetY)
            return;
        scrolling = true;
        this.targetOffsetY = targetOffsetY;
    }

    public void disable() {
        scrollTo(SCROLL_Y);
        shown = false;
    }

    public void enable() {
        scrollTo(0f);
        shown = true;
    }

    @Override
    public void update(float delta) {
        if (scrolling) {
            boolean arrived = Math.abs(targetOffsetY - offsetY) <= 0.05f; // tolerance de fou je sais
            if (arrived) {
                System.out.println("arrived");
                scrolling = false;
                offsetY = targetOffsetY; // pour isFullyShown
            } else 
                offsetY += (targetOffsetY - offsetY) * delta * SCROLL_LERP;
            offsetY = MathUtils.clamp(offsetY, 0f, SCROLL_Y);
        }

        layout.zone("nextTurnZone").setOffset(0f, offsetY);
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);

        TextureRegion innerPanelTexture = hovered ? greyTexture : whiteTexture;
        drawCornerlessRectangle(layout.zone("nextTurnPanel"), HUD_RECT_BORDER, innerPanelTexture);

        text.render(batch);
    }

    public void renderDebug() {
        layout.renderDebug();
    }

    public boolean isFullyShown() {
        return shown && !scrolling && (offsetY == targetOffsetY);
    }

    public boolean posBelongsToHudComponent(Vector2 mousePos) {
        boolean belongs = layout.zone("nextTurnPanel").posBelongsToZone(mousePos);
        return belongs;
    }
    
    public void handleHover(Vector2 hoverPos) {
        hovered = true;
    }

    public void handleHoverGone() {
        hovered = false;
    }

    public void handleClick(Vector2 clickPos) {
        if (!isFullyShown() || callback == null)
            return;
        callback.run();
    }

    public void setAction(Runnable callback) {
        this.callback = callback;
    }
}
