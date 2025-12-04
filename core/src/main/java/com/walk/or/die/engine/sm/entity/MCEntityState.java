package com.walk.or.die.engine.sm.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.sm.MCState;
import com.walk.or.die.engine.sm.MCStateMachine;

public abstract class MCEntityState<T extends MCEntityState.StateArgs> extends MCState<T> {
    protected MCCharacter parent;

    public MCEntityState(MCCharacter parent) {
        super();
        this.parent = parent;
    }

    public MCCharacter getParent() {
        return parent;
    }

    @Override
    public void update(float delta) {}

    @Override
    public void render(SpriteBatch batch) {}

    @Override
    public void renderOnGridOverlay(SpriteBatch batch) {}

    @Override
    public void enter(T args) {
        listen("InputPressed", this::inputPressed);
        // Je rentre dans tes MC en bus
    }

    @Override
    public void exit() {
        bus.off(this, "InputPressed");
    }

    // à override pour tous les états bloquants !
    public boolean isBlocking() {
        return false;
    }
    
    protected void inputPressed(MCInputManager.Command data) {
        if (!(data instanceof MCInputManager.Command)) return;
        
        if (data instanceof MCInputManager.ClickTileCommand) {
            //System.out.println("Oh, on a clické");
        }
        else if (data instanceof MCInputManager.DirectionalCommand) {
            //System.out.println("Oh on a pressé les touches du clavier");
        }
    }

    protected void changeState(String newState, MCEntityState.StateArgs args) {
       parent.getStateManager().stateTransitionCheck(new MCStateMachine.TransitionArgs(getName(), newState, args));
    }

}