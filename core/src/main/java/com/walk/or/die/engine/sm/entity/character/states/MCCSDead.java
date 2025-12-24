package com.walk.or.die.engine.sm.entity.character.states;

import com.walk.or.die.engine.entities.MCAnimation;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.entities.MCCharacter.HudCustomization;
import com.walk.or.die.engine.sm.entity.character.MCCharacterState;

/**
 * The state to kill a character.<br>
 * Name = "dead"
 */
public class MCCSDead extends MCCharacterState<MCCSDead.DeadStateArgs> {
    /**
     * Class which represents args needed by the dead state to start.
     */
    public static class DeadStateArgs extends MCCharacterState.StateArgs {}

    private float DEAD_DURATION = 2f; // si jamais pas d'anim
    private float stateTime = 0f;

    /**
     * The constructor.
     * @param parent
     */
    public MCCSDead(MCCharacter parent) {
        super(parent);
        this.name = "dead";
    }

    @Override
    public void enter(DeadStateArgs args) {
        super.enter(args);
        parent.getHudCustomization().canShow = false;
        stateTime = 0f;
        MCAnimation deadAnim = parent.getAnimation("dead");
        System.out.println("Im here " + deadAnim);
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

}
