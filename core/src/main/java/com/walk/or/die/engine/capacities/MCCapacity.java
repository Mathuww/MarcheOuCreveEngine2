package com.walk.or.die.engine.capacities;

import com.walk.or.die.engine.entities.MCAttack;
import com.walk.or.die.engine.entities.MCCharacter;

public abstract class MCCapacity {
    private MCCharacter parent;
    private String name;

    public MCCapacity(MCCharacter parent, String displayName) {
        this.parent = parent;
        this.name = displayName;
    }

    public void onUse() {
        System.out.println(name + " used on " + parent.getId());
    }

}
