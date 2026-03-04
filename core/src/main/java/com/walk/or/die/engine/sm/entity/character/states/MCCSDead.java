package com.walk.or.die.engine.sm.entity.character.states;

import com.walk.or.die.engine.entities.MCAnimation;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntityManager;
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

    /** The duration for which the character stays dead if no animation is available. */
    private float DEAD_DURATION = 2f; // si jamais pas d'anim
    /** The current time elapsed within this state. */
    private float stateTime = 0f;

    /**
     * Constructs a new dead state for the character.
     * @param parent The parent character to which this state belongs.
     */
    public MCCSDead(MCCharacter parent) {
        super(parent);
        this.name = "dead";
    }

    /**
     * Called at state entrance.
     * @param args The arguments required for entering the dead state.
     */
    @Override
    public void enter(DeadStateArgs args) {
        super.enter(args);
        parent.getHudCustomization().canShow = false;
        stateTime = 0f;
        MCAnimation deadAnim = parent.getAnimation("dead");
        if (deadAnim != null) {
            DEAD_DURATION = Math.min(deadAnim.getDuration(), 2f); // faut pas déconner quand meme
            parent.playAnimation("dead");
        }
        parent.setDead();
    }

    /**
     * Called on each frame.
     * @param delta The time elapsed since the last frame in seconds.
     */
    @Override
    public void update(float delta) {
        stateTime += delta;
        if (stateTime >= DEAD_DURATION) { // c'est bon un peu de discipline, releve toi non ? va pas chialer 1000 ans lui
            MCEntityManager.get().killAndKeepCorpse(parent);
        }
    }

    /**
     * Called at state exit.
     */
    @Override
    public void exit() {
        //parent.display = true;
    }

}