package com.walk.or.die.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.walk.or.die.engine.exceptions.DataException;
import com.walk.or.die.engine.screens.MCFirstScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MCGame extends Game {
    public SpriteBatch batch;
    public FitViewport viewport;

    @Override // commence pas je vais t'attraper
    // cast me if you can ;)
    // MCGaia sale_terrorist = (MCGaia) anothercoderterrorist
    
    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(8, 5);
        try {
            setScreen(new MCFirstScreen(this));
        } catch (DataException e) {}

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