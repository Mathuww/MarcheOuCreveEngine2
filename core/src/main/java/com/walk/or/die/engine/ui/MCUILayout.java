package com.walk.or.die.engine.ui;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class MCUILayout {
    private Map<String, Zone> zones = new HashMap<>();
    private ShapeRenderer debugRenderer = new ShapeRenderer();

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

    public void renderDebug() {
        debugRenderer.begin(ShapeType.Line);
        debugRenderer.setProjectionMatrix(MCHUDManager.get().getCamera().combined);
        for (Zone z : zones.values()) {
            z.renderDebug(debugRenderer);
        }
        debugRenderer.end();
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
            float x = inX() + (inWidth() - width) / 2f;
            float y = inY() + (inHeight() - height) / 2f;
            return new Vector2(x, y);
        }

        public Vector2 alignLeft(float height) {
            float x = inX();
            float y = inY() + (inHeight() - height) / 2f;
            return new Vector2(x, y);
        }

        public Vector2 alignRight(float width, float height) {
            float x = inX() + inWidth() - width;
            float y = inY() + (inHeight() - height) / 2f;
            return new Vector2(x, y);
        }

        // "real" dimensions (no padding, no offset - useful to calculate splits)
        public float logicX() { return rect.x; }
        public float logicY() { return rect.y; }
        public float logicWidth() { return rect.width; }
        public float logicHeight() { return rect.height; }

        private float totalOffsetX() {
            return offsetX + (parent != null ? parent.totalOffsetX() : 0f);
        }

        private float totalOffsetY() {
            return offsetY + (parent != null ? parent.totalOffsetY() : 0f);
        }

        // rectangle w/ cumulative offsets
        public Rectangle outside() {
            return new Rectangle(
                rect.x + totalOffsetX(),
                rect.y + totalOffsetY(),
                rect.width,
                rect.height
            );
        }

        // rectangle w/ offsets + padding
        public Rectangle inside() {
            Rectangle effRect = outside();
            effRect.x += padX;
            effRect.y += padY;
            // sécurité
            effRect.width = Math.min(effRect.width - padX * 2f, 0f);
            effRect.height = Math.min(effRect.height - padY * 2f, 0f);
            return effRect;
        }

        // effective (padded + w/ offset) dimensions
        public float inX() { return inside().x; }
        public float inY() { return inside().y; }
        public float inWidth() { return inside().width; }
        public float inHeight() { return inside().height; }

        public float size() {
            return Math.min(inWidth(), inHeight());
        }

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
    }
}
