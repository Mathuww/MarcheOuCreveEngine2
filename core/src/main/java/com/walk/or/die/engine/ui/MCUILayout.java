package com.walk.or.die.engine.ui;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class MCUILayout {
    private Map<String, Zone> zones = new HashMap<>();

    public MCUILayout() {}

    public Zone addZone(String name, Rectangle rect) {
        Zone z = new Zone(null, rect);
        zones.put(name, z);
        return z;
    }

    public Zone zone(String name) {
        Zone z = zones.get(name);
        if (z == null)
            throw new IllegalStateException(name + " not found in ui layout zones map");
        return z;
    }

    public void splitY(String name, float topRatio, String topName, String bottomName) {
        if (topRatio > 1)
            throw new IllegalArgumentException("cant split zone " + name + " vertically if top and bottom ratios add to more than 1");
        Zone z = zones.get(name);
        if (z == null)
            throw new IllegalStateException("cant split zone vertically : zone " + name + " not found in ui layout zones map");
        
        float effectiveHeight = z.realHeight() - z.padY;
        float topHeight = effectiveHeight * topRatio;
        float bottomHeight = effectiveHeight * (1 - topRatio);

        Rectangle top = new Rectangle(
            z.realX() + z.padX, 
            z.realY() + z.padY + bottomHeight, 
            z.realWidth() - z.padX, 
            topHeight
        );
        Rectangle bottom = new Rectangle(
            z.realX() + z.padX, 
            z.realY() + z.padY, 
            z.realWidth() - z.padX, 
            bottomHeight
        );
        
        zones.put(topName, new Zone(z, top));
        zones.put(bottomName, new Zone(z, bottom));
    }  

    public void splitX(String name, float leftRatio, String leftName, String rightName) {
        if (leftRatio > 1)
            throw new IllegalArgumentException("cant split zone " + name + " horizontally if left and right ratios add to more than 1");
        Zone z = zones.get(name);
        if (z == null)
            throw new IllegalStateException("cant split zone horizontally : zone " + name + " not found in ui layout zones map");
        
        float effectiveWidth = z.realWidth() - z.padX;
        float leftWidth = effectiveWidth * leftRatio;
        float rightWidth = effectiveWidth * (1 - leftRatio);

        Rectangle left = new Rectangle(
            z.realX() + z.padX, 
            z.realY() + z.padY, 
            leftWidth, 
            z.realHeight() - z.padY
        );
        Rectangle right = new Rectangle(
            z.realX() + z.padX + leftWidth, 
            z.realY() + z.padY, 
            rightWidth, 
            z.realHeight() - z.padY
        );
        
        zones.put(leftName, new Zone(z, left));
        zones.put(rightName, new Zone(z, right));
    }

    public static class Zone {
        private Zone parent;
        private Rectangle rect;
        private float padX = 0f, padY = 0f;
        private float offsetX = 0f, offsetY = 0f;

        public Zone(Zone parent, Rectangle rect) {
            this.parent = parent;
            this.rect = rect;
        }

        public void pad(float padX, float padY) {
            this.padX = padX;
            this.padY = padY;
        }

        public void setOffset(float offsetX, float offsetY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        public Vector2 center(float width, float height) {
            float x = displayX() + (displayWidth() - width) / 2f;
            float y = displayY() + (displayHeight() - height) / 2f;
            return new Vector2(x, y);
        }

        public Vector2 centerY(float height) {
            float x = displayX();
            float y = displayY() + (displayHeight() - height) / 2f;
            return new Vector2(x, y);
        }

        // "real" dimensions (no padding, no offset - useful to calculate splits)
        public float realX() { return rect.x; }
        public float realY() { return rect.y; }
        public float realWidth() { return rect.width; }
        public float realHeight() { return rect.height; }

        private float totalOffsetX() {
            return offsetX + (parent != null ? parent.totalOffsetX() : 0f);
        }

        private float totalOffsetY() {
            return offsetY + (parent != null ? parent.totalOffsetY() : 0f);
        }

        // rectangle w/ cumulative offsets
        public Rectangle effectiveRect() {
            return new Rectangle(
                rect.x + totalOffsetX(),
                rect.y + totalOffsetY(),
                rect.width,
                rect.height
            );
        }

        // rectangle w/ offsets + padding
        public Rectangle paddedRect() {
            Rectangle effRect = effectiveRect();
            effRect.x += padX;
            effRect.y += padY;
            effRect.width -= padX;
            effRect.height -= padY;
            return effRect;
        }

        // effective (padded + w/ offset) dimensions
        public float displayX() { return effectiveRect().x + padX; }
        public float displayY() { return effectiveRect().y + padY; }
        public float displayWidth() { return effectiveRect().width - padX; }
        public float displayHeight() { return effectiveRect().height - padY; }
    }
}
