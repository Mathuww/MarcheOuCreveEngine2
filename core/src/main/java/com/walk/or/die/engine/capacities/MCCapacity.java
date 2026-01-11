package com.walk.or.die.engine.capacities;

import com.walk.or.die.engine.entities.MCAttack;
import com.walk.or.die.engine.entities.MCCharacter;

public abstract class MCCapacity {
    private MCCharacter parent;
    private String name;

    /**
     * Instantiates a new MC capacity.
     *
     * @param parent The parent {@link MCCharacter}.
     * @param displayName The display name of the capacity.
     */
    public MCCapacity(MCCharacter parent, String displayName) {
        this.parent = parent;
        this.name = displayName;
    }

    /**
     * Called when the capacity is used.
     */
    public void onUse() {
        System.out.println(name + " used on " + parent.getId());
    }

}