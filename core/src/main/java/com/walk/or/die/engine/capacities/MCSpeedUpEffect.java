package com.walk.or.die.engine.capacities;

import com.walk.or.die.engine.entities.MCCharacter;

/**
 * Double the character's max move.
 */
public class MCSpeedUpEffect extends MCEffects {
    
    /**
     * The constructor.
     * @param parent
     * @param displayName
     */
    public MCSpeedUpEffect(MCCharacter parent, String displayName) {
        super(parent, displayName);
    }

    @Override
    public int getMaxMoves(int move) {
        return move *2;
    }

}
