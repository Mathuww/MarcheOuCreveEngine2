package com.walk.or.die.engine.entities;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MCAnimation {
    private Animation<TextureRegion> animation;
    private float stateTime;

    public MCAnimation() {
        stateTime = 0f;
    }

    public MCAnimation(Animation<TextureRegion> anim) {
        this.animation = anim;
        stateTime = 0f;
    }

    public void setRawAnim(Animation<TextureRegion> anim) {
        this.animation = anim;
    }

    public TextureRegion update(float delta) {
        stateTime += delta;
        int frame = animation.getKeyFrameIndex(stateTime);

        return animation.getKeyFrame(stateTime);
    }

    public float getDuration() {
        return animation.getAnimationDuration();
    }

    public boolean isOver() {
        return stateTime >= animation.getAnimationDuration();
    }

    public void reset() {
        stateTime = 0f;
    }
}
