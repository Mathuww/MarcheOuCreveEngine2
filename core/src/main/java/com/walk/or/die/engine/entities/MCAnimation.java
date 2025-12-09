package com.walk.or.die.engine.entities;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A class that plays an animation.
 * @see Animation
 */
public class MCAnimation {
    private Animation<TextureRegion> animation;
    private float stateTime;

    /**
     * The constructor.
     */
    public MCAnimation() {
        stateTime = 0f;
    }

    /**
     * The constructor.
     * @param anim
     */
    public MCAnimation(Animation<TextureRegion> anim) {
        this.animation = anim;
        stateTime = 0f;
    }

    /**
     * Set the animation.
     * @param anim
     */
    public void setRawAnim(Animation<TextureRegion> anim) {
        this.animation = anim;
    }

    /**
     * Call each frame.
     * @param delta
     * @return the current frame.
     */
    public TextureRegion update(float delta) {
        stateTime += delta;
        int frame = animation.getKeyFrameIndex(stateTime);

        return animation.getKeyFrame(stateTime);
    }

    /**
     * Get the animation's duration.
     * @return
     */
    public float getDuration() {
        return animation.getAnimationDuration();
    }

    /**
     * Check if the animation is finished.
     * @return
     */
    public boolean isOver() {
        return stateTime >= getDuration();
        //return stateTime >= animation.getAnimationDuration();
    }

    /**
     * Reset the animation.
     */
    public void reset() {
        stateTime = 0f;
    }

}
