package com.walk.or.die.engine.sm.entity.character;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.sm.entity.MCEntityState;

/**
 * Abstract class for character's state. 
 */
public abstract class MCCharacterState<T extends MCCharacterState.StateArgs> extends MCEntityState<T, MCCharacter> {

    /**
     * The constructor.
     * @param parent
     */
    public MCCharacterState(MCCharacter parent) {
        super(parent);
    }

    /**
     * Get the parent (a character).
     * @see MCCharacter
     */
    public MCCharacter getParent() {
        return parent;
    }

    @Override
    public void update(float delta) {}

    @Override
    public void render(SpriteBatch batch) {}

    @Override
    public void renderOnGridOverlay(SpriteBatch batch) {}

}