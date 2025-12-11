package com.walk.or.die.engine.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.walk.or.die.engine.ui.MCUILayout.Zone;

public class MCUITypingText extends MCUISimpleText { 
    private float stateTime = 0f;
    private boolean typing;
    private float typingSpeed = 15f; // caracteres/s
    private int typedChars = 0;
    private String fullText = "";

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

    public void setSpeed(float speed) {
        typingSpeed = speed;
    }

    public void startTyping() {
        stateTime = 0f;
        typedChars = 0;
        typing = true;
        super.setText("");
    }

    public void endTyping() {
        typedChars = fullText.length();
        typing = false;
    }

    public boolean stillTyping() {
        return typing;
    }

    @Override
    public void setText(String text) {
        fullText = text;
        super.setText("");
    }

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
