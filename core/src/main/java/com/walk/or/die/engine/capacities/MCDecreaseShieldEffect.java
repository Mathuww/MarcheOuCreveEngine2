package com.walk.or.die.engine.capacities;

import com.walk.or.die.engine.entities.MCCharacter;

public class MCDecreaseShieldEffect extends MCEffects {

    float percentage;

    /**
     * Constructs a MCDecreaseShieldEffect.
     * @param parent The parent character.
     * @param displayName The display name.
     * @param percentage The percentage of shield to decrease.
     */
    public MCDecreaseShieldEffect(MCCharacter parent, String displayName, float percentage) {
        super(parent, displayName);
        this.percentage = percentage;
    }

    /**
     * Gets the hurt damage after shield reduction.
     * @param damage The initial damage.
     * @return The calculated damage after shield reduction.
     */
    @Override
    public int getHurtDamage(int damage) {
        return (int) ((float) damage * percentage);
    }

    /**
     * Called when a new turn starts.
     */
    @Override
    public void onNewTurn() {
        setDispose(true);
    }
    
}