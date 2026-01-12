package com.walk.or.die.engine.capacities;

import com.walk.or.die.engine.entities.MCCharacter;

public class MCDecreaseShieldEffect extends MCEffects {

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

    @Override
    public String getSummary() {
        return "Everyone in " + affectDistance + " tiles will get " + percentage + " % less damage for one turn.";
    }

    public void setAffectDistance(int d) {
        affectDistance = d;
    }

    public void setPercentage(float p) {
        percentage = p;
    }

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