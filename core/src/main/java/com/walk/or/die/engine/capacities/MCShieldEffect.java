package com.walk.or.die.engine.capacities;

import com.walk.or.die.engine.entities.MCCharacter;

/**
 * Build a shield over the character, which protects him from one hit.
 */
public class MCShieldEffect extends MCEffects{
    
    /**
     * The constructor.
     * @param parent
     * @param displayName
     */
    public MCShieldEffect(MCCharacter parent, String displayName) {
        super(parent, displayName);
    }

    @Override
    public int onHurt(int damage) {
        setDispose(true);
        parent.removeEffect(name);
        return 0;
    }

    @Override
    public void onNewTurn() {
        setDispose(true);
    }
}
