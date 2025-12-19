package com.walk.or.die.engine.entities;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Text;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

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

    public MCAnimation getFlippedAnim() {
        Array<TextureRegion> flippedFrames = new Array<>();

        Object[] keyFrames = animation.getKeyFrames();
        for (Object frameObj : keyFrames) {
            if (!(frameObj instanceof TextureRegion)) 
                throw new IllegalStateException("not a TextureRegion: ");

            TextureRegion srcFrame = (TextureRegion) frameObj;

            // Clonage et flip
            TextureRegion flipped = new TextureRegion(srcFrame);
            flipped.flip(true, false);
            flippedFrames.add(flipped);
        }

        // Création de l'animation flippée
        Animation<TextureRegion> flippedAnim = new Animation<>(
            animation.getFrameDuration(),
            flippedFrames,
            animation.getPlayMode()
        );

        return new MCAnimation(flippedAnim);
    }
}
