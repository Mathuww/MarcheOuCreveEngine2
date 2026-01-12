package com.walk.or.die.engine.capacities;

import com.walk.or.die.engine.entities.MCAttack;
import com.walk.or.die.engine.entities.MCCharacter;

/**
 * Doubles the damage inflicted by the character.
 */
public class MCStrengthEffect extends MCEffects{
    
    /**
     * The constructor.
     * @param parent The parent character.
     * @param displayName The display name.
     */
    public MCStrengthEffect(MCCharacter parent) {
        super(parent, "STRENGTH");
    }

    @Override
    public String getSummary() {
        return "This ally will be twice as strong for one turn.";
    }

    @Override
    public MCEffects copy(MCCharacter target) {
        return new MCStrengthEffect(target);
    }

    @Override
    /**
     * Applies the strength effect on attack.
     * @param attack The attack.
     */
    public void onAttack(MCAttack attack) {
        getAttack(attack);
    }

    /**
     * Gets the attack and applies the strength effect.
     * @param attack The attack.
     */
    @Override
    public void getAttack(MCAttack attack) {
        attack.setPower(attack.getPower()*2);
    }

    /**
     * Called on a new turn.
     */
    @Override
    public void onNewTurn() {
        setDispose(true);
    }

}