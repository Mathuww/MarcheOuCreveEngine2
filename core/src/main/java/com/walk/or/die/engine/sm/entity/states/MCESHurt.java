package com.walk.or.die.engine.sm.entity.states;

import com.walk.or.die.engine.entities.MCAnimation;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.sm.entity.MCEntityState;

public class MCESHurt extends MCEntityState<MCESHurt.HurtStateArgs> {
    public static class HurtStateArgs extends MCEntityState.StateArgs {
        private float damage;
        private String targetAnim;

        public HurtStateArgs(float damage, String targetAnim) {
            this.damage = damage;
            this.targetAnim = targetAnim;
        }
    }

    private float HURT_DURATION = 0.85f; // si jamais pas d'anim on clignote 1,25s

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
        MCAnimation hurtAnim = parent.getAnimation(args.targetAnim);
        if (hurtAnim != null) {
            HURT_DURATION = Math.max(hurtAnim.getDuration(), 3f); // faut pas déconner quand meme
            parent.playAnimation(args.targetAnim);
        }
    }  

    @Override
    public void update(float delta) {
        stateTime += delta;
        blinkingTime += delta;
        if (stateTime >= HURT_DURATION) { // c'est bon un peu de discipline, releve toi non ? va pas chialer 1000 ans lui
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
}
