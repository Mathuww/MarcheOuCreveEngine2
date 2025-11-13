package com.walk.or.die.engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    public SpriteBatch batch;
    public FitViewport viewport;

    @Override
    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(8, 5);
        setScreen(new FirstScreen(this));
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