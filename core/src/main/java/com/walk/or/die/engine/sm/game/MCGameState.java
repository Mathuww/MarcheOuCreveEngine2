package com.walk.or.die.engine.sm.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.sm.MCState;
import com.walk.or.die.engine.sm.MCStateMachine;

public abstract class MCGameState<T extends MCGameState.StateArgs> extends MCState<T> {
    protected MCGame parent;

    public MCGameState(MCGame parent) {
        super();
        this.parent = parent;
    }

    @Override
    public void enter(T args) {
        System.out.println("entering game state " + this.name);
    }

    @Override
    public void exit() {

    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void render(SpriteBatch batch) {

    }

    @Override
    public void renderOnGridOverlay(SpriteBatch batch) {
        
    }

    @Override
    protected void changeState(String newState, MCGameState.StateArgs args) {
        parent.getStateManager().stateTransitionCheck(new MCStateMachine.TransitionArgs(getName(), newState, args));
    }

    protected void inputPressed(MCInputManager.Command data) {
        if (!(data instanceof MCInputManager.Command)) return;
        
        if (data instanceof MCInputManager.ClickTileCommand) {
            System.out.println("Oh, on a clické");
        }
        else if (data instanceof MCInputManager.DirectionalCommand) {
            System.out.println("Oh on a pressé les touches du clavier");
        }
    }
}
