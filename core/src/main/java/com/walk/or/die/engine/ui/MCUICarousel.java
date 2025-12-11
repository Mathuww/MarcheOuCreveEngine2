package com.walk.or.die.engine.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.walk.or.die.engine.ui.MCUILayout.Zone;

public class MCUICarousel {
    private final float SCROLL_LERP = 100f;

    private Set<String> items = new HashSet<>();
    private Map<String, Runnable> actions = new HashMap<>();
    private int currentIndex;

    private float offsetX = 0f;
    private float targetOffsetX = 0f;

    private MCAbstractHUD parent;
    private Zone zone;
    private MCUITypingText text;

    public MCUICarousel(MCAbstractHUD parent, BitmapFont font, Zone zone) {
        this.parent = parent;
        this.zone = zone;
        this.text = new MCUITypingText(
            parent,
            font,
            zone,
            Color.BLACK,
            0.25f,
            5f
        );
    }

    public void addAction(String item, Runnable action) {
        items.add(item);
        actions.put(item, action);
    }
}
