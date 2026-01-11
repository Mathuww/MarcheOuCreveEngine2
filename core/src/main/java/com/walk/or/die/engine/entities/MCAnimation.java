package com.walk.or.die.engine.entities;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Text;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Wraps a libGDX animation in an easier-to-use interface.
 * @see Animation
 */
public class MCAnimation {
    /**
     * libGDX animation.
     */
    private Animation<TextureRegion> animation;
    /**
     * Time elapsed since the animation started.
     */
    private float stateTime;

    /**
     * The constructor.
     */
    public MCAnimation() {
        stateTime = 0f;
    }

    /**
     * The constructor.
     * @param anim A libGDX animation.
     */
    public MCAnimation(Animation<TextureRegion> anim) {
        this.animation = anim;
        stateTime = 0f;
    }

    /**
     * Sets another libGDX animation to wrap into this object.
     * @param anim The animation to set.
     */
    public void setRawAnim(Animation<TextureRegion> anim) {
        this.animation = anim;
    }

    /**
     * @param delta The time elapsed since last frame.
     * @return The current key frame.
     */
    public TextureRegion update(float delta) {
        stateTime += delta;
        int frame = animation.getKeyFrameIndex(stateTime);

        return animation.getKeyFrame(stateTime);
    }

    /**
     * Gets the animation's duration.
     * @return The duration of the animation.
     */
    public float getDuration() {
        return animation.getAnimationDuration();
    }

    /**
     * Checks whether the animation finished.
     * @return True if it is finished, false otherwise.
     */
    public boolean isOver() {
        return stateTime >= getDuration();
        //return stateTime >= animation.getAnimationDuration();
    }

    /**
     * Resets the animation.
     */
    public void reset() {
        stateTime = 0f;
    }

    /**
     * Creates a horizontally flipped copy of the animation.
     * Useful if a spritesheets only contains a walk to the left animation for instance.
     * @return A flipped copy of the animation.
     */
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