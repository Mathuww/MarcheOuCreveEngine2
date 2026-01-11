package com.walk.or.die.engine.capacities;

import com.walk.or.die.engine.entities.MCAttack;
import com.walk.or.die.engine.entities.MCCharacter;

/**
 * An abstract class to build effects that affect the characters.<br>
 * Override the methods to build your own effects.<br>
 */
public abstract class MCEffects {
    /**
     * The name of the effect. It needs to be a unique name.
     */
    public String name;
    protected MCCharacter parent;
    private boolean dispose = false;
    private int turn = 0;

    /**
     * The constructor.
     * @param parent The parent MCCharacter.
     * @param displayName The effect's display name.
     */
    public MCEffects(MCCharacter parent, String displayName) {
        this.parent = parent;
        this.name = displayName;
    }

    /**
     * Called on each frame.
     * @param delta The time delta.
     */
    public void update(float delta) {}

    /**
     * Called on each frame.
     */
    public void render() {}
    
    /**
     * Called each turn.
     */
    public void onNewTurn() {
        turn += 1;
    }

    /**
     * Called when attack. Modifies the attack currently.
     * @param attack The MCAttack instance.
     */
    public void onAttack(MCAttack attack) {}

    /**
     * Called when we check the character's attack. Modifies the attack currently.
     * @param attack The MCAttack instance.
     */
    public void getAttack(MCAttack attack) {}

    /**
     * Called when the character gets hurt.
     * @param damage The amount of damage taken.
     * @return The new amount of damage.
     */
    public int onHurt(int damage) {
        return damage;
    }

    /**
     * Called when we ask how much damage the character should take.
     * @param damage The damage amount.
     * @return The damage amount.
     */
    public int getHurtDamage(int damage) {
        return damage;
    }

    /**
     * Called when we ask the character's max move.
     * @param move The basic value.
     * @return The new value.
     */
    public int getMaxMoves(int move) {
        return move;
    }

    /**
     * Sets if the effect needs to be disposed.
     * @param bool The boolean value.
     */
    public void setDispose(boolean bool) {
        dispose = bool;
    }

    /**
     * Checks if the effect needs to be disposed.
     * @return True if the effect is disposable, false otherwise.
     */
    public boolean isDisposable() {
        return dispose;
    }

    @Override
    public String toString() {
        return "Effect " + name + " : dispose = " + dispose + " |  turn = " + turn ;
    }
}