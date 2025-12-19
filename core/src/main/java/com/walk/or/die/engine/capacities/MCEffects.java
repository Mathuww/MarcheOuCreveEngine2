package com.walk.or.die.engine.capacities;

import com.walk.or.die.engine.entities.MCAttack;
import com.walk.or.die.engine.entities.MCCharacter;

/**
 * An abstract class to build effects who affects the characters.<br>
 * Override the methods to build your own effects.
 */
public abstract class MCEffects {
    /**
     * The name of the effects. It's need to be a unique name.
     */
    public String name;
    protected MCCharacter parent;
    private boolean dispose = false;
    private int turn = 0;

    /**
     * The constructor.
     * @param parent
     * @param displayName
     */
    public MCEffects(MCCharacter parent, String displayName) {
        this.parent = parent;
        this.name = displayName;
    }

    /**
     * Call each frame.
     * @param delta
     */
    public void update(float delta) {}

    /**
     * Render (call each frame).
     */
    public void render() {}
    /**
     * Call each turn.
     */
    public void onNewTurn() {
        turn += 1;
    }

    /**
     * Call when we check the character's base attack. Modify the attack.
     * @param attack
     */
    public void onAttack(MCAttack attack) {}

    /**
     * Call when the character get hurt.
     * @param damage taken
     * @return the new ammount of damage
     */
    public int onHurt(int damage) {
        return damage;
    }

    /**
     * Call when we ask the character's max move.
     * @param move - the basic value
     * @return the new value
     */
    public int getMaxMoves(int move) {
        return move;
    }

    /**
     * Set if the effect need to be disposed.
     * @param bool
     */
    public void setDispose(boolean bool) {
        dispose = bool;
    }

    /**
     * Check if the effect need to be disposed.
     * @return
     */
    public boolean isDisposable() {
        return dispose;
    }

    @Override
    public String toString() {
        return "Effect " + name + " : dispose = " + dispose + " |  turn = " + turn ;
    }
}
