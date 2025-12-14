package com.walk.or.die.engine.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.stream.MemoryCacheImageOutputStream;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.input.MCInputManager.HudCommand;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.ui.MCUILayout.Zone;

public class MCUICarousel {
    private final float SCROLL_LERP = 8f;
    private final int SPACES_BETWEEN_ITEMS = 5;
    private final float FADE_WIDTH = 60f; 

    private final String textIfEmpty = "(NOTHING TO DO)";
    private List<String> items = new ArrayList<>();
    private List<Runnable> actions = new ArrayList<>();
    private List<Float> itemsOffsetX = new ArrayList<>();
    private List<Float> itemsWidth = new ArrayList<>();
    private float totalWidth = 0f;
    private int currentIndex = 0;

    private float offsetX = 0f;
    private float targetOffsetX = 0f;

    private MCAbstractHUD parent;
    private Zone zone;
    private String totalText;
    private MCUISimpleText textComponent;

    private TextureRegion gradientTexture;

    public MCUICarousel(MCAbstractHUD parent, BitmapFont font, Zone zone) {
        this.parent = parent;
        this.zone = zone;
        this.textComponent = new MCUISimpleText(
            parent,
            font,
            zone,
            Color.BLACK,
            0.35f,
            5f
        );
        this.textComponent.centered = false; // sinon on double centre mdr

        
        try {
            gradientTexture = MCSharedAssets.get().getSavedTexture("whiteFade");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearActions() {
        this.items.clear();
        this.actions.clear();
        this.itemsOffsetX.clear();
        this.itemsWidth.clear();

        currentIndex = 0;
        targetOffsetX = 0f;
        offsetX = 0f;
        textComponent.setText(textIfEmpty);
    }

    public void loadActions(Map<String, Runnable> actions) {
        clearActions();
        if (actions.size() == 0)
            return;

        this.items.addAll(actions.keySet());
        float cursor = 0f;
        String totalText = "";

        for (int i = 0; i < items.size(); i++) {
            String item = items.get(i);
            this.actions.add(actions.get(item));
            itemsOffsetX.add(cursor);

            float visualItemWidth = textComponent.textDimensions(item).x;
            itemsWidth.add(visualItemWidth);

            String itemWithSpaces = item;
            if (i < items.size() - 1) {// pas le dernier 
                for (int j = 0; j < SPACES_BETWEEN_ITEMS; j++)
                    itemWithSpaces += " ";
            }
            totalText += itemWithSpaces;
            System.out.println("adding carousel item w/sp " + itemWithSpaces);

            float itemBlockWidth = textComponent.textDimensions(itemWithSpaces).x;
            cursor += itemBlockWidth;
        }

        System.out.println("all carousel text is " + totalText);
        textComponent.setText(totalText);

        updateTargetOffset();
    }

    public void next() {
        currentIndex++;
        if (currentIndex >= items.size())
            currentIndex = 0;
        updateTargetOffset();
    }

    public void previous() {
        currentIndex--;
        if (currentIndex < 0)
            currentIndex = items.size() - 1;
        updateTargetOffset();
    }

    public void validate() {
        actions.get(currentIndex).run();
    }

    public void updateTargetOffset() {
        float itemStart = itemsOffsetX.get(currentIndex);
        float itemWidth = itemsWidth.get(currentIndex);
        float itemCenter = itemStart + itemWidth / 2f;

        float zoneCenter = zone.inWidth() / 2f;
        targetOffsetX = zoneCenter - itemCenter;
    }

    public void update(float delta) {
        offsetX += (targetOffsetX - offsetX) * SCROLL_LERP * delta;
        //System.out.println("je suis le carousel et mon target offset x est : " + targetOffsetX);
        textComponent.setOffsetX(offsetX);
    }

    private void edgeGradient(SpriteBatch batch) {
        batch.draw(
            gradientTexture, 
            zone.inX(), 
            zone.inY(), 
            FADE_WIDTH, 
            zone.inHeight()
        );
        if (!gradientTexture.isFlipX())
            gradientTexture.flip(true, false); // flip x
    
        batch.draw(
            gradientTexture, 
            zone.inX() + zone.inWidth() - FADE_WIDTH, 
            zone.inY(), 
            FADE_WIDTH, 
            zone.inHeight()
        );

        gradientTexture.flip(true, false);
    }

    public void render(SpriteBatch batch) {
        if (items.size() == 0) // not loaded
            return;
        textComponent.render(batch);
        edgeGradient(batch);
    }
}
