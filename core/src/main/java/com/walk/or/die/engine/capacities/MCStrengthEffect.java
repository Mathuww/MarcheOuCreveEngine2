package com.walk.or.die.engine.capacities;

import com.walk.or.die.engine.entities.MCAttack;
import com.walk.or.die.engine.entities.MCCharacter;

/**
 * Doubles the damage inflicted by the character.
 */
public class MCStrengthEffect extends MCEffects{
    
    /**
     * Constructs a new strength effect.
     * @param parent The character to which this effect is applied.
     */
    public MCStrengthEffect(MCCharacter parent) {
        super(parent, "STRENGTH");
    }

    /**
     * Gets a summary of this strength effect.
     * @return A string representing the summary of the effect.
     */
    @Override
    public String getSummary() {
        return "This ally will be twice as strong for one turn.";
    }

    /**
     * Creates a new copy of this strength effect for the specified target.
     * @param target The character to which the new effect will be applied.
     * @return A new instance of {@code MCStrengthEffect} applied to the target.
     */
    @Override
    public MCEffects copy(MCCharacter target) {
        return new MCStrengthEffect(target);
    }

    /**
     * Applies the strength effect during an attack.
     * @param attack The attack object.
     */
    @Override
    public void onAttack(MCAttack attack) {
        getAttack(attack);
    }

    /**
     * Gets the attack and applies the strength effect.
     * @param attack The attack object.
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