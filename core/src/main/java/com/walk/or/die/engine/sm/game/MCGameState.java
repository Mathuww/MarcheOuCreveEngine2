package com.walk.or.die.engine.sm.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.screens.MCGameScreen;
import com.walk.or.die.engine.shared.MCEmpty;
import com.walk.or.die.engine.sm.MCState;
import com.walk.or.die.engine.sm.MCStateMachine;

public abstract class MCGameState<T extends MCGameState.StateArgs> extends MCState<T> {
    protected MCGame parent;

    public MCGameState(MCGame parent) {
        super();
        this.parent = parent;
    }

    /**
     * Called at state entrance.
     * @param args The arguments for the state.
     */
    @Override
    public void enter(T args) {
        System.out.println("entering game state " + this.name);
        bus.emit("GameStateChanged", this);
    }

    /**
     * Called at state exit.
     */
    @Override
    public void exit() {

    }

    /**
     * Called on each frame.
     * @param delta The time delta.
     */
    @Override
    public void update(float delta) {

    }

    /**
     * Called on each frame.
     * @param batch The sprite batch.
     */
    @Override
    public void render(SpriteBatch batch) {

    }

    /**
     * Renders the effects.
     *
     * @param batch The sprite batch.
     */
    @Override
    public void renderEffects(SpriteBatch batch) {
        
    }

    /**
     * Changes the state.
     *
     * @param newState The new state name.
     * @param args     The state arguments.
     */
    @Override
    protected void changeState(String newState, MCGameState.StateArgs args) {
        parent.getStateManager().stateTransitionCheck(new MCStateMachine.TransitionArgs(getName(), newState, args));
    }

    /**
     * Handles the input pressed event.
     *
     * @param data The input command data.
     */
    protected void inputPressed(MCInputManager.Command data) {
        if (!(data instanceof MCInputManager.Command)) return;
        
        if (parent.getCurrentScreen() instanceof MCGameScreen
            && data instanceof MCInputManager.PauseCommand) {
            if (parent.isPaused())
                parent.resumeGame(new MCEmpty());
            else
                parent.pauseGame(new MCEmpty());
        }
    }
}