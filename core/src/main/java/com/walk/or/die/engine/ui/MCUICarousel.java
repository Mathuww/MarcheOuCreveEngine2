package com.walk.or.die.engine.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.ui.MCUILayout.Zone;

/**
 * Represents a choice carousel constructed using text elements.
 * Scrolling past the limits wraps around to the other side.
 * Executes a callback when an element is focused (if specified) and another when validated (if specified).
 * @see MCUISimpleText
 */
public class MCUICarousel {
    /**
     * Represents a single item within the carousel.
     */
    public static class CarouselItem {
        public String name;
        public Runnable onValidate;
        public Runnable onFocus;
        public float offsetX;
        public float width;

        /**
         * Constructs a new CarouselItem.
         * @param name The name of the item.
         * @param onValidate The Runnable to execute on validation.
         * @param onFocus The Runnable to execute on focus.
         */
        public CarouselItem(String name, Runnable onValidate, Runnable onFocus) {
            this.name = name;
            this.onValidate = onValidate;
            this.onFocus = onFocus;
        }
    }

    /**
     * The tolerance for interaction based on offset from the center.
     */
    private final float INTERACT_OFFSET_TOLERANCE = 15f;
    /**
     * The linear interpolation factor for scrolling.
     */
    private final float SCROLL_LERP = 8f;
    /**
     * The number of spaces between carousel items.
     */
    private final int SPACES_BETWEEN_ITEMS = 5;
    /**
     * The width of the fade gradient at the edges.
     */
    private final float FADE_WIDTH = 60f;
    /**
     * The size of the corners for the focused item highlight.
     */
    private final float CORNER_SIZE = MCGame.WINDOW_DEFAULT_HEIGHT * 0.015f;
    /**
     * The padding around the focused item for the highlight.
     */
    private final float CORNER_PADDING = CORNER_SIZE * 3f;

    /**
     * The text displayed when the carousel has no items.
     */
    private String textIfEmpty = "(NOTHING TO DO)";
    /**
     * The list of items currently in the carousel.
     */
    private List<CarouselItem> items = new ArrayList<>();
    /**
     * The total calculated width of the combined text for all items.
     */
    private float totalWidth = 0f;
    /**
     * The index of the currently focused item.
     */
    private int currentIndex = 0;
    /**
     * The rectangle representing the focused item's highlight area.
     */
    private Rectangle focusedItemRect;
    /**
     * Indicates whether the focused item is currently being hovered over.
     */
    private boolean focusedItemHovered = false;

    /**
     * The current horizontal offset of the carousel content.
     */
    private float offsetX = 0f;
    /**
     * The target horizontal offset for the carousel content, determined by the focused item.
     */
    private float targetOffsetX = 0f;

    /**
     * The parent HUD component.
     */
    private MCAbstractHUD parent;
    /**
     * The layout zone for this carousel.
     */
    private Zone zone;
    /**
     * The combined text of all carousel items, including spaces.
     */
    private String totalText;
    /**
     * The simple text component used to render the carousel items.
     */
    private MCUISimpleText textComponent;

    /**
     * The current time accumulator for the blinking effect.
     */
    private float blinkingTime = 0f;
    /**
     * The interval at which the highlight blinks.
     */
    private final float BLINKING_INTERVAL = 0.95f;
    /**
     * Indicates whether the focused item highlight should currently be displayed (for blinking effect).
     */
    private boolean displayHighlight = true;

    /**
     * The texture used for the edge fade gradient.
     */
    private TextureRegion gradientTexture;
    /**
     * The alpha transparency for the gradient textures.
     */
    private float gradientAlpha = 1f;
    /**
     * The texture used for the background of the hovered focused item.
     */
    private TextureRegion greyTexture;

    /**
     * Constructs a new MCUICarousel.
     * @param parent The parent HUD.
     * @param font The font used for text rendering.
     * @param zone The layout zone.
     */
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

        focusedItemRect = new Rectangle(zone.inside());

        try {
            gradientTexture = MCSharedAssets.get().getSavedTexture("whiteFade");
            greyTexture = MCSharedAssets.get().getSavedTexture("grey");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears all actions and items from the carousel.
     */
    public void clearActions() {
        this.items.clear();

        currentIndex = 0;
        targetOffsetX = 0f;
        offsetX = 0f;
        textComponent.setText(textIfEmpty);
    }

    /**
     * Sets the alpha transparency for the gradient textures.
     * @param a The alpha value to set.
     */
    public void setGradientAlpha(float a) {
        gradientAlpha = a;
    }

    /**
     * Sets the text displayed when the carousel is empty.
     * @param t The text to display.
     */
    public void setEmptyText(String t) {
        textIfEmpty = t;
    }

    /**
     * Loads a list of items into the carousel.
     * @param items The list of carousel items to load.
     * @param firstIndex The index of the initially selected item.
     */
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
            //System.out.println("adding carousel item w/sp " + itemWithSpaces);

            float itemBlockWidth = textComponent.textDimensions(itemWithSpaces).x;
            cursor += itemBlockWidth;
        }

        //System.out.println("all carousel text is " + totalText);
        textComponent.setText(totalText);

        currentIndex = firstIndex;
        if (items.get(firstIndex).onFocus != null)
            items.get(firstIndex).onFocus.run();

        updateGeometry();
    }

    /**
     * Selects the next item in the carousel.
     */
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

    /**
     * Selects the previous item in the carousel.
     */
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

    /**
     * Validates (activates) the currently selected item.
     */
    public void validate() {
        if (items.isEmpty())
            return;
        if (items.get(currentIndex).onValidate != null) {
            items.get(currentIndex).onValidate.run();
        }
    }

    /**
     * Updates the geometric positioning of the carousel items.
     */
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
        focusedItemRect.x = zone.inside().x + (zone.inside().width - focusedItemRect.width) / 2f;
        // y et height bougent pas, on reste centrés verticalement
    }

    /**
     * Updates the carousel logic. Called on each frame.
     * @param delta The time elapsed since the last frame.
     */
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

    /**
     * Draws the edge fade gradients on both sides of the carousel.
     * @param batch The sprite batch used for drawing.
     */
    private void edgeGradient(SpriteBatch batch) {
        batch.setColor(1f, 1f, 1f, gradientAlpha - 0.2f);
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
        batch.setColor(1f, 1f, 1f, 1f);
    }

    /**
     * Checks if the carousel's current offset is close enough to its target offset for interaction.
     * @return True if the carousel is close enough to the center, false otherwise.
     */
    private boolean closeEnoughToCenter() {
        return (Math.abs(targetOffsetX - offsetX) < INTERACT_OFFSET_TOLERANCE);
    }

    /**
     * Renders the carousel. Called on each frame.
     * @param batch The sprite batch used for drawing.
     */
    public void render(SpriteBatch batch) {
        if (closeEnoughToCenter() && !items.isEmpty() && parent.isFullyShown()) {
            if (focusedItemHovered) {
                batch.setColor(1f, 1f, 1f, gradientAlpha);
                parent.drawFilledRectangle(focusedItemRect, greyTexture);
                batch.setColor(1f, 1f, 1f, 1f);
            }
            if (displayHighlight)
                parent.drawFourCorners(focusedItemRect, CORNER_SIZE);
        }
        textComponent.render(batch);
        edgeGradient(batch);
    }

    /**
     * Checks if a position belongs to the HUD component.
     * @param pos The position to check.
     * @return True if the position belongs to the HUD component, false otherwise.
     */
    public boolean posBelongsToHudComponent(Vector2 pos) {
        return zone.posBelongsToZone(pos);
    }

    /**
     * Handles hover events.
     * @param pos The position of the cursor.
     */
    public void handleHover(Vector2 pos) {
        if (focusedItemRect.contains(pos))
            focusedItemHovered = true;
        else
            focusedItemHovered = false;
    }

    /**
     * Handles the event when the mouse stops hovering over the component.
     */
    public void handleHoverGone() {
        focusedItemHovered = false;
    }

    /**
     * Handles mouse click events.
     * @param pos The position of the click.
     */
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

    /**
     * Handles scroll events.
     * @param dy The vertical scroll amount.
     */
    public void handleScroll(float dy) {
        if (targetOffsetX != offsetX)
            return;
        if (dy > 0)
            next();
        else if (dy < 0)
            previous();
    }
}