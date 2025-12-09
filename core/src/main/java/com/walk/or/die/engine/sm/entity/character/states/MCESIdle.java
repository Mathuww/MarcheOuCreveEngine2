package com.walk.or.die.engine.sm.entity.character.states;

import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.sm.entity.character.MCCharacterState;


public class MCESIdle extends MCCharacterState<MCESIdle.IdleStateArgs> {

    public static class IdleStateArgs extends MCCharacterState.StateArgs {}

    public MCESIdle(MCCharacter parent) {
        super(parent);
        this.name = "idle";
    }

    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    @Override
    public void enter(IdleStateArgs args) {
        parent.keep = false;
        parent.playAnimation("idle");
        super.enter(args);
    }

    @Override
    public void exit() {
        parent.keep = true;
        super.exit();
    }
    
    @Override
    protected void inputPressed(MCInputManager.Command data) {
        if (!parent.focus)
            return;
        if (!parent.getParent().getStateManager().isIn("AlliesPlaying"))
            return;
        if (parent instanceof MCAlly ally) {
            if (MCEntityManager.get().isAnyoneBusy())
                return;
            if (data instanceof MCInputManager.ReadyCommand) {
                if (ally.getTurnState().canMove())
                    changeState("ready", new MCESReady.ReadyStateArgs());
            } else if (data instanceof MCInputManager.AimCommand) {
                if (ally.getTurnState().canAttack())
                    changeState("aim", new MCESAim.AimStateArgs(parent.getAttack()));
            }
        }

    }
}
