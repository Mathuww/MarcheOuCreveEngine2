package com.walk.or.die.engine.capacities;

import com.walk.or.die.engine.entities.MCAttack;
import com.walk.or.die.engine.entities.MCCharacter;

/**
 * Double the damage inflicted by the character.
 */
public class MCStrengthEffect extends MCEffects{
    
    /**
     * The constructor.
     * @param parent
     * @param displayName
     */
    public MCStrengthEffect(MCCharacter parent, String displayName) {
        super(parent, displayName);
    }

    @Override
    public void onAttack(MCAttack attack) {
        getAttack(attack);
    }

    @Override
    public void getAttack(MCAttack attack) {
        attack.setPower(attack.getPower()*2);
    }

    @Override
    public void onNewTurn() {
        setDispose(true);
    }

}
