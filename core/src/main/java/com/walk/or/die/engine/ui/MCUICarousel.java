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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.input.MCInputManager.HudCommand;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.ui.MCUILayout.Zone;

public class MCUICarousel {
    public static class CarouselItem {
        public String name;
        public Runnable onValidate;
        public Runnable onFocus;
        public float offsetX;
        public float width;

        public CarouselItem(String name, Runnable onValidate, Runnable onFocus) {
            this.name = name;
            this.onValidate = onValidate;
            this.onFocus = onFocus;
        }
    }

    private final float INTERACT_OFFSET_TOLERANCE = 15f;
    private final float SCROLL_LERP = 8f;
    private final int SPACES_BETWEEN_ITEMS = 5;
    private final float FADE_WIDTH = 60f; 
    private final float CORNER_SIZE = MCGame.WINDOW_DEFAULT_HEIGHT * 0.015f;
    private final float CORNER_PADDING = CORNER_SIZE * 3f;

    private String textIfEmpty = "(NOTHING TO DO)";
    private List<CarouselItem> items = new ArrayList<>();
    private float totalWidth = 0f;
    private int currentIndex = 0;
    private Rectangle focusedItemRect;
    private boolean focusedItemHovered = false;

    private float offsetX = 0f;
    private float targetOffsetX = 0f;

    private MCAbstractHUD parent;
    private Zone zone;
    private String totalText;
    private MCUISimpleText textComponent;

    private float blinkingTime = 0f;
    private final float BLINKING_INTERVAL = 0.95f;
    private boolean displayHighlight = true;

    private TextureRegion gradientTexture;
    private TextureRegion greyTexture;

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

        focusedItemRect = new Rectangle(zone.outside());

        try {
            gradientTexture = MCSharedAssets.get().getSavedTexture("whiteFade");
            greyTexture = MCSharedAssets.get().getSavedTexture("grey");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearActions() {
        this.items.clear();

        currentIndex = 0;
        targetOffsetX = 0f;
        offsetX = 0f;
        textComponent.setText(textIfEmpty);
    }

    public void setEmptyText(String t) {
        textIfEmpty = t;
    }

    public void loadItems(List<CarouselItem> items, int firstIndex) {
        clearActions();
        if (items.size() == 0) {
            updateGeometry();
            return;
        }

        this.items.addAll(items);
        float cursor = 0f;
        String totalText = "";

        for (int i = 0; i < items.size(); i++) {
            CarouselItem item = items.get(i);

            item.offsetX = cursor;

            float visualItemWidth = textComponent.textDimensions(item.name).x;
            item.width = visualItemWidth;

            String itemWithSpaces = item.name;
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

        currentIndex = firstIndex;
        if (items.get(firstIndex).onFocus != null)
            items.get(firstIndex).onFocus.run();

        updateGeometry();
    }

    public void next() {
        if (items.isEmpty())
            return;

        currentIndex++;
        if (currentIndex >= items.size())
            currentIndex = 0;

        CarouselItem item = items.get(currentIndex);
        if (item.onFocus != null)
            item.onFocus.run();

        updateGeometry();
    }

    public void previous() {
        if (items.isEmpty())
            return;
        
        currentIndex--;
        if (currentIndex < 0)
            currentIndex = items.size() - 1;

        CarouselItem item = items.get(currentIndex);
        if (item.onFocus != null)
            item.onFocus.run();

        updateGeometry();
    }

    public void validate() {
        if (items.isEmpty())
            return;
        if (items.get(currentIndex).onValidate != null)
            items.get(currentIndex).onValidate.run();
    }

    public void updateGeometry() {
        if (items.isEmpty()) {
            targetOffsetX = 0f;
            textComponent.centered = true;
            return;
        } else
            textComponent.centered = false;

        CarouselItem item = items.get(currentIndex);
        float itemCenter = item.offsetX + item.width / 2f;

        float zoneCenter = zone.inWidth() / 2f;
        targetOffsetX = zoneCenter - itemCenter;

        focusedItemRect.width = item.width + 2f * CORNER_PADDING;
        focusedItemRect.x = zone.outside().x + (zone.outside().width - focusedItemRect.width) / 2f;
        // y et height bougent pas, on reste centrés verticalement
    }

    public void update(float delta) {
        blinkingTime += delta;
        if (blinkingTime >= BLINKING_INTERVAL) {
            blinkingTime = 0f;
            displayHighlight = !displayHighlight;
        }

        if (Math.abs(targetOffsetX - offsetX) > 0.5f) // pour rendre le truc un peu "snappy"
            offsetX += (targetOffsetX - offsetX) * SCROLL_LERP * delta;
        else
            offsetX = targetOffsetX; // pour pas rater la cible pendant le lerp, comme d'hab
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

    private boolean closeEnoughToCenter() {
        return (Math.abs(targetOffsetX - offsetX) < INTERACT_OFFSET_TOLERANCE);
    }

    public void render(SpriteBatch batch) {
        if (closeEnoughToCenter() && !items.isEmpty() && parent.isFullyShown()) {
            if (focusedItemHovered)
                parent.drawFilledRectangle(focusedItemRect, greyTexture);
            if (displayHighlight)
                parent.drawFourCorners(focusedItemRect, CORNER_SIZE);
        }
        textComponent.render(batch);
        edgeGradient(batch);
    }

    public boolean posBelongsToHudComponent(Vector2 pos) {
        return zone.posBelongsToZone(pos);
    }

    public void handleHover(Vector2 pos) {
        if (focusedItemRect.contains(pos))
            focusedItemHovered = true;
        else
            focusedItemHovered = false;
    }

    public void handleHoverGone() {
        focusedItemHovered = false;
    }

    public void handleClick(Vector2 pos) {
        if (!closeEnoughToCenter())
            return;
        if (focusedItemRect.contains(pos))
            validate();
        else {
            float zoneCenterX = zone.inX() + zone.inWidth() / 2f;
            // j'avoue je m'embete pas trop,
            // mais en vrai je vois pas le besoin de faire plus compliqué.
            if (pos.x >= zoneCenterX)
                next();
            else
                previous();
        }
    }

    public void handleScroll(float dy) {
        if (targetOffsetX != offsetX)
            return;
        if (dy > 0)
            next();
        else if (dy < 0)
            previous();
    }
}
