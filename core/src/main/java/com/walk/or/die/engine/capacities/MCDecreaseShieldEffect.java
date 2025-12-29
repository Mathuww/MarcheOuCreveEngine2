package com.walk.or.die.engine.capacities;

import com.walk.or.die.engine.entities.MCCharacter;

public class MCDecreaseShieldEffect extends MCEffects {

    float percentage;

    public MCDecreaseShieldEffect(MCCharacter parent, String displayName, float percentage) {
        super(parent, displayName);
        this.percentage = percentage;
    }

    @Override
    public int getHurtDamage(int damage) {
        return (int) ((float) damage * percentage);
    }

    @Override
    public void onNewTurn() {
        setDispose(true);
    }
    
}
