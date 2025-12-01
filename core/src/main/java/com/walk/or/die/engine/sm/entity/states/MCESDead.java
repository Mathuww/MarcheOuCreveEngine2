package com.walk.or.die.engine.sm.entity.states;

import com.walk.or.die.engine.entities.MCAnimation;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.sm.entity.MCEntityState;

public class MCESDead extends MCEntityState<MCESDead.DeadStateArgs> {
    public static class DeadStateArgs extends MCEntityState.StateArgs {}

    private float DEAD_DURATION = 2f; // si jamais pas d'anim
    private float stateTime = 0f;

    public MCESDead(MCCharacter parent) {
        super(parent);
        this.name = "dead";
    }

    @Override
    public void enter(DeadStateArgs args) {
        super.enter(args);
        stateTime = 0f;
        MCAnimation deadAnim = parent.getAnimation("dead");
        if (deadAnim != null) {
            DEAD_DURATION = Math.min(deadAnim.getDuration(), 2f); // faut pas déconner quand meme
            parent.playAnimation("dead");
        }
        parent.setDead();
    }  

    @Override
    public void update(float delta) {
        stateTime += delta;
        if (stateTime >= DEAD_DURATION) { // c'est bon un peu de discipline, releve toi non ? va pas chialer 1000 ans lui
            MCEntityManager.get().killAndKeepCorpse(parent);
        }
    }

    @Override
    public void exit() {
        //parent.display = true;
    }

    @Override
    public boolean isBlocking() {
        return true;
    }
}
