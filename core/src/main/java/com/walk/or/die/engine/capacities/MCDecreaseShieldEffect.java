package com.walk.or.die.engine.capacities;

import com.walk.or.die.engine.entities.MCCharacter;

public class MCDecreaseShieldEffect extends MCEffects {

    /**
     * The percentage of damage reduction applied by this effect.
     */
    float percentage;

    /**
     * Constructs a MCDecreaseShieldEffect.
     * @param parent The parent character.
     */
    public MCDecreaseShieldEffect(MCCharacter parent) {
        super(parent, "DECREASE SHIELD");
        this.percentage = 50f;
        this.affectDistance = 4;
    }

    /**
     * Gets a summary description of the effect.
     *
     * @return A string summarizing the effect's properties.
     */
    @Override
    public String getSummary() {
        return "Everyone in " + affectDistance + " tiles will get " + percentage + " % less damage for one turn.";
    }

    /**
     * Sets the affect distance of the effect.
     *
     * @param d The new affect distance in tiles.
     */
    public void setAffectDistance(int d) {
        affectDistance = d;
    }

    /**
     * Sets the percentage of damage reduction.
     *
     * @param p The new percentage of damage reduction.
     */
    public void setPercentage(float p) {
        percentage = p;
    }

    /**
     * Creates a copy of this effect for a new target character.
     *
     * @param target The new target character for the copied effect.
     * @return A new instance of this effect, configured for the target.
     */
    @Override
    public MCEffects copy(MCCharacter target) {
        MCDecreaseShieldEffect e = new MCDecreaseShieldEffect(target);
        e.setPercentage(percentage);
        return e;
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