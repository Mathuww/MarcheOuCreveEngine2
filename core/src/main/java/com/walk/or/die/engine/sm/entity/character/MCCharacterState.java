package com.walk.or.die.engine.sm.entity.character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.sm.entity.MCEntityState;
import com.walk.or.die.engine.ui.MCHUDManager;

/**
 * Abstract class for character's state.
 */
public abstract class MCCharacterState<T extends MCCharacterState.StateArgs> extends MCEntityState<T, MCCharacter> {

    /**
     * Manages the Heads-Up Display.
     */
    protected final MCHUDManager hudManager = MCHUDManager.get();

    /**
     * Constructs a new character state.
     * @param parent The parent character.
     */
    public MCCharacterState(MCCharacter parent) {
        super(parent);
    }

    /**
     * Gets the parent (a character).
     * @see MCCharacter
     * @return The parent character.
     */
    public MCCharacter getParent() {
        return parent;
    }

    /**
     * Called on each frame to update the state logic.
     * @param delta The time elapsed since the last frame, in seconds.
     */
    @Override
    public void update(float delta) {}

    /**
     * Called on each frame to render the state visuals.
     * @param batch The sprite batch used for rendering.
     */
    @Override
    public void render(SpriteBatch batch) {}

    /**
     * Renders specific effects related to the state.
     * @param batch The sprite batch used for rendering effects.
     */
    @Override
    public void renderEffects(SpriteBatch batch) {}

    /**
     * Called when HUD visibility is lost.
     */
    public void onHudVisibilityLost() {}
}