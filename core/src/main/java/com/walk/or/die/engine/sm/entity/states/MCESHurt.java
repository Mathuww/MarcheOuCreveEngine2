package com.walk.or.die.engine.sm.entity.states;

import com.badlogic.gdx.math.MathUtils;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.entities.MCAnimation;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.sm.entity.MCEntityState;

public class MCESHurt extends MCEntityState<MCESHurt.HurtStateArgs> {
    public static class HurtStateArgs extends MCEntityState.StateArgs {
        private int damage;
        private String targetAnim;

        public HurtStateArgs(int damage, String targetAnim) {
            this.damage = damage;
            this.targetAnim = targetAnim;
        }
    }

    private float HURT_DURATION = 2f; 

    private final float BLINKING_INTERVAL = 0.1f;
    private float stateTime = 0f;
    private float blinkingTime = 0f;

    public MCESHurt(MCCharacter parent) {
        super(parent);
        this.name = "hurt";
    }

    @Override
    public void enter(HurtStateArgs args) {
        super.enter(args);
        stateTime = 0f;
        blinkingTime = 0f;
        parent.playAnimation(args.targetAnim);
        MCCameraManager.get().shake(
            MathUtils.clamp(((float)args.damage / (float)parent.getMaxHp()), 0f, 0.05f), 
            0.3f
        );
    }  

    @Override
    public void update(float delta) {
        stateTime += delta;
        blinkingTime += delta;
        if (stateTime >= HURT_DURATION) { // c'est bon un peu de discipline, releve toi non ? va pas chialer 1000 ans lui
            if (parent.getHealth() <= 0)
                changeState("dead", new MCESDead.DeadStateArgs());
            else
                changeState("idle", new MCESIdle.IdleStateArgs());
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

    @Override
    public boolean isBlocking() {
        return true;
    }
}
