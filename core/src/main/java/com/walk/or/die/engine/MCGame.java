package com.walk.or.die.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.walk.or.die.engine.exceptions.DataException;
import com.walk.or.die.engine.screens.MCGameScreen;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.game.MCGameState;
import com.walk.or.die.engine.sm.game.states.MCGSCombat;
import com.walk.or.die.engine.sm.game.states.MCGSExploration;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MCGame extends Game {
    public SpriteBatch batch;
    public FitViewport viewport;
    private MCStateMachine<MCGameState, MCGame> stateManager;

    public MCGame() {
        stateManager = new MCStateMachine<MCGameState,MCGame>(this);
        stateManager.addState(new MCGSCombat(this));
        stateManager.addState(new MCGSExploration(this));
    }

    @Override // commence pas je vais t'attraper
    // cast me if you can ;)
    // MCGaia sale_terrorist = (MCGaia) anothercoderterrorist
    
    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(12, 8);
        try {
            setScreen(new MCGameScreen(this));
        } catch (DataException e) {
            e.printStackTrace();
        }
    }

    public MCStateMachine getStateManager() {
        return this.stateManager;
    }

    @Override
    public void render() {
        super.render();
    }
    
    @Override
    public void dispose() {
        batch.dispose();
    }
}