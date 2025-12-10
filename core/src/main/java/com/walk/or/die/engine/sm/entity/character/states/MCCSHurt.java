package com.walk.or.die.engine.sm.entity.character.states;

import com.badlogic.gdx.math.MathUtils;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCAnimation;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.sm.entity.character.MCCharacterState;

/**
 * The state to take damages.<br>
 * Name = "hurt"
 */
public class MCCSHurt extends MCCharacterState<MCCSHurt.HurtStateArgs> {
    /**
     * The class which represents args needed by hurt state to start.
     */
    public static class HurtStateArgs extends MCCharacterState.StateArgs {
        private int damage;
        private String targetAnim;

        /**
         * The constructor.
         * @param damage - taken by the character
         * @param targetAnim - to play
         */
        public HurtStateArgs(int damage, String targetAnim) {
            this.damage = damage;
            this.targetAnim = targetAnim;
        }
    }

    private float HURT_DURATION = 2f; 

    private final float BLINKING_INTERVAL = 0.1f;
    private float stateTime = 0f;
    private float blinkingTime = 0f;

    /**
     * The constructor.
     * @param parent
     */
    public MCCSHurt(MCCharacter parent) {
        super(parent);
        this.name = "hurt";
    }

    @Override
    public void enter(HurtStateArgs args) {
        super.enter(args);
        stateTime = 0f;
        blinkingTime = 0f;
        parent.playAnimation(args.targetAnim);
        if (parent instanceof MCAlly) {
            MCCameraManager.get().shake(
                MathUtils.clamp(((float)args.damage / (float)parent.getMaxHp()), 0f, 0.6f), 
                0.25f
            );
        }
    }  

    @Override
    public void update(float delta) {
        stateTime += delta;
        blinkingTime += delta;
        if (stateTime >= HURT_DURATION) { // c'est bon un peu de discipline, releve toi non ? va pas chialer 1000 ans lui
            if (parent.getHealth() <= 0)
                changeState("dead", new MCCSDead.DeadStateArgs());
            else
                changeState("idle", new MCCSIdle.IdleStateArgs());
            return;
        }
        if (blinkingTime >= BLINKING_INTERVAL) {
            parent.display = !parent.display; // on inverse la visiblité
            blinkingTime = 0f;
        }   
    }

    @Override
    public void exit() {
        parent.display = true;
    }
    
}
