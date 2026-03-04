package com.walk.or.die.engine.capacities;

import com.walk.or.die.engine.entities.MCCharacter;

/**
 * Doubles the character's max move.
 */
public class MCSpeedUpEffect extends MCEffects {
    
    /**
     * Instantiates a new speed up effect.
     * @param parent The parent MCCharacter.
     */
    public MCSpeedUpEffect(MCCharacter parent) {
        super(parent, "SPEED UP");
        this.affectDistance = 4;
    }

    /**
     * Gets the summary of the effect.
     * @return Returns the summary string.
     */
    @Override
    public String getSummary() {
        return "Everyone in " + affectDistance + " tiles will be able to move twice as far for one turn.";
    }

    /**
     * Sets the affect distance for the effect.
     * @param d Sets the new affect distance.
     */
    public void setAffectDistance(int d) {
        affectDistance = d;
    }

    /**
     * Creates a copy of this effect for a target character.
     * @param target The target character for the new effect.
     * @return Returns a new {@link MCSpeedUpEffect} instance for the target.
     */
    @Override
    public MCEffects copy(MCCharacter target) {
        return new MCSpeedUpEffect(target);
    }

    /**
     * Gets the maximum moves with the speed up effect applied.
     * @param move The base move value.
     * @return The modified maximum moves.
     */
    @Override
    public int getMaxMoves(int move) {
        return move *2;
    }

}