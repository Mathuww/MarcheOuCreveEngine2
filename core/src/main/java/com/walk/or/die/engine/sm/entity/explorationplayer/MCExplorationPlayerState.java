package com.walk.or.die.engine.sm.entity.explorationplayer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.walk.or.die.engine.entities.MCExplorationPlayer;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.sm.entity.MCEntityState;

/**
 * Abstract base class for exploration player states.
 *
 * @param <T> Type of state arguments.
 */
public abstract class MCExplorationPlayerState<T extends MCExplorationPlayerState.StateArgs> extends MCEntityState<T, MCExplorationPlayer>  {

    /**
     * Initializes a new exploration player state.
     *
     * @param parent The parent MCExplorationPlayer entity.
     */
    public MCExplorationPlayerState(MCExplorationPlayer parent) {
        super(parent);
    }

    /**
     * Gets the parent.
     * @return The parent.
     */
    public MCExplorationPlayer getParent() {
        return parent;
    }

    /**
     * Called on each frame.
     * @param delta The time delta.
     */
    @Override
    public void update(float delta) {}

    /**
     * Called on each frame.
     * @param batch The sprite batch.
     */
    @Override
    public void render(SpriteBatch batch) {}

    /**
     * Called on each frame.
     * @param batch The sprite batch.
     */
    @Override
    public void renderEffects(SpriteBatch batch) {}

    /**
     * Checks if it is blocking.
     *
     * @return True if blocking, false otherwise.
     */
    public boolean isBlocking() {
        return false;
    }
    
    /**
     * Handles the input pressed.
     *
     * @param data The command data.
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

}