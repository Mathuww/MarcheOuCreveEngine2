package com.walk.or.die.engine.capacities;

import com.walk.or.die.engine.entities.MCCharacter;

/**
 * Builds a shield over the character, which protects him from one hit.
 */
public class MCShieldEffect extends MCEffects{
    
    /**
     * The constructor.
     * @param parent The parent character.
     * @param displayName The display name.
     */
    public MCShieldEffect(MCCharacter parent, String displayName) {
        super(parent, displayName);
    }

    @Override
    /**
     * Handles the hurt event.
     * @param damage The amount of damage.
     * @return The modified damage amount.
     */
    public int onHurt(int damage) {
        setDispose(true);
        parent.removeEffect(name);
        return 0;
    }

    @Override
    /**
     * Gets the hurt damage.
     * @param damage The initial damage.
     * @return The modified damage.
     */
    public int getHurtDamage(int damage) {
        return 0;
    }

    @Override
    /**
     * Called when a new turn begins.
     */
    public void onNewTurn() {
        setDispose(true);
    }
}