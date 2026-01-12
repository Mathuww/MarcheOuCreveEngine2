package com.walk.or.die.engine.ui;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * A class used to easily design our user interface without using any absolute positioning. Very inspired by the way CSS works (especially for padding).
 */
public class MCUILayout {
    private Map<String, Zone> zones = new HashMap<>();
    private ShapeRenderer debugRenderer = new ShapeRenderer();

    public MCUILayout() {}

    /**
     * Adds a zone to the layout.
     *
     * @param name The name of the zone.
     * @param rect The rectangle representing the zone's dimensions.
     * @return The newly created zone.
     */
    public Zone addZone(String name, Rectangle rect) {
        Zone z = new Zone(null, rect);
        zones.put(name, z);
        return z;
    }

    /**
     * Gets a zone by its name.
     *
     * @param name The name of the zone to retrieve.
     * @return The zone associated with the given name.
     * @throws IllegalStateException If the zone is not found.
     */
    public Zone zone(String name) {
        Zone z = zones.get(name);
        if (z == null)
            throw new IllegalStateException(name + " not found in ui layout zones map");
        return z;
    }

    /**
     * Splits a zone vertically into two sub-zones.
     *
     * @param name The name of the zone to split.
     * @param topRatio The ratio of the top sub-zone's height to the original zone's height.
     * @param gap The gap between the top and bottom sub-zones.
     * @param topName The name of the top sub-zone.
     * @param bottomName The name of the bottom sub-zone.
     * @throws IllegalArgumentException If the top ratio is greater than 1.
     * @throws IllegalStateException If the zone to split is not found.
     */
    public void splitY(String name, float topRatio, float gap, String topName, String bottomName) {
        if (topRatio > 1)
            throw new IllegalArgumentException("cant split zone " + name + " vertically if top and bottom ratios add to more than 1");
        Zone z = zones.get(name);
        if (z == null)
            throw new IllegalStateException("cant split zone vertically : zone " + name + " not found in ui layout zones map");
        
        float effectiveHeight = z.logicHeight() - 2f * z.padY - gap;
        float topHeight = effectiveHeight * topRatio;
        float bottomHeight = effectiveHeight * (1 - topRatio);

        Rectangle top = new Rectangle(
            z.logicX() + z.padX, 
            z.logicY() + z.padY + gap + bottomHeight, 
            z.logicWidth() - 2f * z.padX, 
            topHeight
        );
        Rectangle bottom = new Rectangle(
            z.logicX() + z.padX, 
            z.logicY() + z.padY, 
            z.logicWidth() - 2f * z.padX, 
            bottomHeight
        );
        
        zones.put(topName, new Zone(z, top));
        zones.put(bottomName, new Zone(z, bottom));
    }  

    /**
     * Splits a zone horizontally into two sub-zones.
     *
     * @param name The name of the zone to split.
     * @param leftRatio The ratio of the left sub-zone's width to the original zone's width.
     * @param gap The gap between the left and right sub-zones.
     * @param leftName The name of the left sub-zone.
     * @param rightName The name of the right sub-zone.
     * @throws IllegalArgumentException If the left ratio is greater than 1.
     * @throws IllegalStateException If the zone to split is not found.
     */
    public void splitX(String name, float leftRatio, float gap, String leftName, String rightName) {
        if (leftRatio > 1)
            throw new IllegalArgumentException("cant split zone " + name + " horizontally if left and right ratios add to more than 1");
        Zone z = zones.get(name);
        if (z == null)
            throw new IllegalStateException("cant split zone horizontally : zone " + name + " not found in ui layout zones map");
        
        float effectiveWidth = z.logicWidth() - 2f * z.padX - gap;
        float leftWidth = effectiveWidth * leftRatio;
        float rightWidth = effectiveWidth * (1 - leftRatio);

        Rectangle left = new Rectangle(
            z.logicX() + z.padX, 
            z.logicY() + z.padY, 
            leftWidth, 
            z.logicHeight() - 2f * z.padY
        );
        Rectangle right = new Rectangle(
            z.logicX() + z.padX + leftWidth + gap, 
            z.logicY() + z.padY, 
            rightWidth, 
            z.logicHeight() - 2f * z.padY
        );
        
        zones.put(leftName, new Zone(z, left));
        zones.put(rightName, new Zone(z, right));
    }

    /**
     * Renders debug information for the UI layout.
     */
    public void renderDebug() {
        debugRenderer.begin(ShapeType.Line);
        debugRenderer.setProjectionMatrix(MCHUDManager.get().getCamera().combined);
        for (Zone z : zones.values()) {
            z.renderDebug(debugRenderer);
        }
        debugRenderer.end();
    }

    /**
     * Contains info. about a layout's zone. <br>
     * Especially wraps padding and alignment (left, center, right) computation.
     */
    public static class Zone {
        private Zone parent;
        private Rectangle logicRect;
        private float padX = 0f, padY = 0f;
        private float offsetX = 0f, offsetY = 0f;

        /**
         * Constructs a new Zone.
         *
         * @param parent The parent zone, or null if this is the root zone.
         * @param rect The rectangle representing the zone's dimensions.
         */
        public Zone(Zone parent, Rectangle rect) {
            this.parent = parent;
            this.logicRect = rect;
        }

        /**
         * Moves the zone to a new position.
         *
         * @param x The new x-coordinate of the zone.
         * @param y The new y-coordinate of the zone.
         */
        public void moveTo(float x, float y) {
            logicRect.x = x;
            logicRect.y = y;
        }

        /**
         * Sets the padding for the zone.
         *
         * @param padX The horizontal padding.
         * @param padY The vertical padding.
         */
        public void pad(float padX, float padY) {
            this.padX = padX;
            this.padY = padY;
        }

        /**
         * Sets the offset for the zone.
         *
         * @param offsetX The horizontal offset.
         * @param offsetY The vertical offset.
         */
        public void setOffset(float offsetX, float offsetY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        /**
         * Calculates the center position within the zone for a given width and height.
         *
         * @param width The width of the object to be centered.
         * @param height The height of the object to be centered.
         * @return A Vector2 representing the center position.
         */
        public Vector2 center(float width, float height) {
            float x = inX() + (inWidth() - width) / 2f;
            float y = inY() + (inHeight() - height) / 2f;
            return new Vector2(x, y);
        }

        /**
         * Aligns an object to the left side of the zone.
         *
         * @param height The height of the object to align.
         * @return A Vector2 representing the aligned position.
         */
        public Vector2 alignLeft(float height) {
            float x = inX();
            float y = inY() + (inHeight() - height) / 2f;
            return new Vector2(x, y);
        }

        /**
         * Aligns an object to the right side of the zone.
         *
         * @param width The width of the object to align.
         * @param height The height of the object to align.
         * @return A Vector2 representing the aligned position.
         */
        public Vector2 alignRight(float width, float height) {
            float x = inX() + inWidth() - width;
            float y = inY() + (inHeight() - height) / 2f;
            return new Vector2(x, y);
        }

        /**
         * Gets the logical x-coordinate of the zone (without padding or offset).
         *
         * @return The logical x-coordinate.
         */
        public float logicX() { return logicRect.x; }

        /**
         * Gets the logical y-coordinate of the zone (without padding or offset).
         *
         * @return The logical y-coordinate.
         */
        public float logicY() { return logicRect.y; }

        /**
         * Gets the logical width of the zone (without padding or offset).
         *
         * @return The logical width.
         */
        public float logicWidth() { return logicRect.width; }

        /**
         * Gets the logical height of the zone (without padding or offset).
         *
         * @return The logical height.
         */
        public float logicHeight() { return logicRect.height; }

        /**
         * Calculates the total horizontal offset of the zone, including parent offsets.
         *
         * @return The total horizontal offset.
         */
        private float totalOffsetX() {
            return offsetX + (parent != null ? parent.totalOffsetX() : 0f);
        }

        /**
         * Calculates the total vertical offset of the zone, including parent offsets.
         *
         * @return The total vertical offset.
         */
        private float totalOffsetY() {
            return offsetY + (parent != null ? parent.totalOffsetY() : 0f);
        }

        /**
         * Gets the outside rectangle of the zone, including cumulative offsets.
         *
         * @return A Rectangle representing the outside dimensions of the zone.
         */
        public Rectangle outside() {
            return new Rectangle(
                logicRect.x + totalOffsetX(),
                logicRect.y + totalOffsetY(),
                logicRect.width,
                logicRect.height
            );
        }

        /**
         * Gets the inside rectangle of the zone, including offsets and padding.
         *
         * @return A Rectangle representing the inside dimensions of the zone.
         */
        public Rectangle inside() {
            Rectangle effRect = outside();
            effRect.x += padX;
            effRect.y += padY;
            // sécurité
            effRect.width = Math.max(effRect.width - padX * 2f, 0f);
            effRect.height = Math.max(effRect.height - padY * 2f, 0f);
            return effRect;
        }

        /**
         * Gets the effective x-coordinate of the zone (with padding and offset).
         *
         * @return The effective x-coordinate.
         */
        public float inX() { return inside().x; }

        /**
         * Gets the effective y-coordinate of the zone (with padding and offset).
         *
         * @return The effective y-coordinate.
         */
        public float inY() { return inside().y; }

        /**
         * Gets the effective width of the zone (with padding and offset).
         *
         * @return The effective width.
         */
        public float inWidth() { return inside().width; }

        /**
         * Gets the effective height of the zone (with padding and offset).
         *
         * @return The effective height.
         */
        public float inHeight() { return inside().height; }

        /**
         * Gets the minimum dimension (width or height) of the inside rectangle.
         *
         * @return The minimum dimension.
         */
        public float size() {
            return Math.min(inWidth(), inHeight());
        }

        /**
         * Renders debug information for the zone using a ShapeRenderer.
         *
         * @param debugRenderer The ShapeRenderer to use for rendering.
         */
        public void renderDebug(ShapeRenderer debugRenderer) {
            // censé être déjà begin et setProjectionMatrix à cet endroit
            // extérieur (sans padding)
            debugRenderer.setColor(Color.RED);
            Rectangle out = outside();
            debugRenderer.rect(out.x, out.y, out.width, out.height);
            // intérieur (padding)
            debugRenderer.setColor(Color.MAGENTA);
            Rectangle in = inside();
            debugRenderer.rect(in.x, in.y, in.width, in.height);
        }

        /**
         * Checks if a given position belongs to the zone.
         *
         * @param pos The position to check.
         * @return True if the position is within the zone, false otherwise.
         */
        public boolean posBelongsToZone(Vector2 pos) {
            //System.out.println("testing if " + pos.x + "," + pos.y + "is contained in " + outside().x + ", " + outside().y + " - " + outside().width + "x" + outside().height);
            return outside().contains(pos);
        }
    }
}