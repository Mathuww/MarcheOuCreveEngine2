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
        attack.setPower(attack.getPower()*2);
    }

}
