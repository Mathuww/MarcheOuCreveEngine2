package com.walk.or.die.engine.sm.entity;

import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.sm.MCState;
import com.walk.or.die.engine.sm.MCStateMachine;

/**
 * Abstract class for entity's state.
 */
public abstract class MCEntityState<T extends MCEntityState.StateArgs, U extends MCEntity> extends MCState<T> {
    protected U parent;

    /**
     * The constructor.
     * @param parent The parent entity.
     */
    public MCEntityState(U parent) {
        super();
        this.parent = parent;
    }

    /**
     * Gets the parent (owner of the stateMachine).
     * @return The parent entity.
     */
    public U getParent() {
        return parent;
    }

    @Override
    public void enter(T args) {
        listen("InputPressed", this::inputPressed);
        // Je rentre dans tes MC en bus
    }

    @Override
    public void exit() {
        bus.off(this, "InputPressed");
    }

    /**
     * Determines if it's a blocking state (if it needs to end normally or if it can be interrupt). By default, returns true.
     * @return True if the state is blocking, false otherwise.
     */
    public boolean isBlocking() {
        // à override pour tous les états non-bloquants !
        return true;
    }
    
    /**
     * Called each time an input is pressed/released.
     * @param data The input data.
     */
    protected void inputPressed(MCInputManager.Command data) {
        if (!(data instanceof MCInputManager.Command)) return;
        
        if (data instanceof MCInputManager.ClickTileCommand) {
            //System.out.println("Oh, on a clické");
        }
        else if (data instanceof MCInputManager.DirectionalCommand) {
            //System.out.println("Oh on a pressé les touches du clavier");
        }
    }

    /**
     * Changes the state, and calls a new one with its needed argument.
     * @param newState The name of the new state.
     * @param args The arguments for the new state.
     */
    protected void changeState(String newState, MCEntityState.StateArgs args) {
       parent.getStateManager().stateTransitionCheck(new MCStateMachine.TransitionArgs(getName(), newState, args));
    }

}