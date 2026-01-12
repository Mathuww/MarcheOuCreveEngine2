package com.walk.or.die.engine.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.walk.or.die.engine.ui.MCUILayout.Zone;

/**
 * Like MCUISimpleText, but with a typewriter effect.
 * @see MCUISimpleText
 */
public class MCUITypingText extends MCUISimpleText { 
    private float stateTime = 0f;
    private boolean typing;
    private float typingSpeed = 15f; // caracteres/s
    private int typedChars = 0;
    private String fullText = "";

    /**
     * Creates a new MCUITypingText.
     * @param parent The parent HUD.
     * @param font The bitmap font.
     * @param zone The zone.
     * @param color The color.
     * @param scale The scale.
     * @param spacing The spacing.
     */
    public MCUITypingText(
        MCAbstractHUD parent, 
        BitmapFont font, 
        Zone zone, 
        Color color, 
        float scale, 
        float spacing
    ) {
        super(parent, font, zone, color, scale, spacing);
    }

    /**
     * Sets the typing speed.
     * @param speed The typing speed to set.
     */
    public void setSpeed(float speed) {
        typingSpeed = speed;
    }

    /**
     * Starts the typing animation.
     */
    public void startTyping() {
        stateTime = 0f;
        typedChars = 0;
        typing = true;
        super.setText("");
    }

    /**
     * Ends the typing animation.
     */
    public void endTyping() {
        typedChars = fullText.length();
        typing = false;
    }

    /**
     * Checks if the text is still typing.
     * @return True if the text is still typing, false otherwise.
     */
    public boolean stillTyping() {
        return typing;
    }

    /**
     * Sets the full text.
     * @param text The text to set.
     */
    @Override
    public void setText(String text) {
        fullText = text;
        super.setText("");
    }

    /**
     * Called on each frame
     * @param delta The time in seconds since the last frame.
     */
    @Override
    public void update(float delta) {
        super.update(delta);

        if (!typing) {
            //System.out.println("not typing");
            return;
        }

        //System.out.println("new target chars is " + typedChars);

        stateTime += delta;
        int targetChars = Math.min(
            MathUtils.floor(typingSpeed * stateTime),
            fullText.length()
        );

        if (typedChars != targetChars) {
            typedChars = targetChars;
            super.setText(fullText.substring(0, targetChars));
        }

        if (typedChars >= fullText.length())
            endTyping();
    }
}