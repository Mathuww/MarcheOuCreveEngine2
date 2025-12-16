package com.capacities;

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

    public void onAttack(MCAttack attack) {
    }

    public void onHurt(int damage) {
        parent.getHurt(damage * 10); // j'avoue je l'utiliserais pas cette capacité là
    }

    public int getMaxMoves() {
        return parent.getMaxMoves();
    }
}
