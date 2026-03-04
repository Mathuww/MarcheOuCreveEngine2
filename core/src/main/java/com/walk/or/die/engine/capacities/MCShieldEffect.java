package com.walk.or.die.engine.capacities;

import com.walk.or.die.engine.entities.MCCharacter;

/**
 * Builds a shield over the character, which protects him from one hit.
 */
public class MCShieldEffect extends MCEffects{
    
    /**
     * Constructs a new shield effect.
     * @param parent The parent character.
     */
    public MCShieldEffect(MCCharacter parent) {
        super(parent, "TOTAL SHIELD");
    }

    @Override
    /**
     * Gets a summary description of the effect.
     * @return A summary string describing the effect.
     */
    public String getSummary() {
        return "This ally will be insensitive to all attacks for one turn.";
    }

    @Override
    /**
     * Creates a copy of this effect for a specified target character.
     * @param target The character to apply the effect to.
     * @return A new instance of {@code MCShieldEffect} for the target.
     */
    public MCEffects copy(MCCharacter target) {
        return new MCShieldEffect(target);
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