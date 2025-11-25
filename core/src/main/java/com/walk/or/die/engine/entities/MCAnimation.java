package com.walk.or.die.engine.entities;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MCAnimation {
    private Animation<TextureRegion> animation;
    private Map<Integer, Runnable> triggers;
    private float stateTime;

    public MCAnimation() {
        this.triggers = new HashMap<>();
        stateTime = 0f;
    }

    public MCAnimation(Animation<TextureRegion> anim) {
        this.animation = anim;
        this.triggers = new HashMap<>();
        stateTime = 0f;
    }

    public void setRawAnim(Animation<TextureRegion> anim) {
        this.animation = anim;
    }

    public void addTrigger(int index, Runnable trigger) {
        triggers.put(index, trigger);
    }

    public TextureRegion update(float delta) {
        stateTime += delta;
        int frame = animation.getKeyFrameIndex(stateTime);

        if (triggers.containsKey(frame)) {
            triggers.get(frame).run();
        }

        return animation.getKeyFrame(stateTime);
    }

    public void reset() {
        stateTime = 0f;
    }
}
