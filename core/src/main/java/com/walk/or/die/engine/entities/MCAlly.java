package com.walk.or.die.engine.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapProperties;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.capacities.MCSpeedShoot;
import com.walk.or.die.engine.exceptions.MissingDataException;
import com.walk.or.die.engine.shared.MCUtils;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.entity.character.states.MCCSAim;
import com.walk.or.die.engine.sm.entity.character.states.MCCSCapacityChoose;
import com.walk.or.die.engine.sm.entity.character.states.MCCSClickMove;
import com.walk.or.die.engine.sm.entity.character.states.MCCSDead;
import com.walk.or.die.engine.sm.entity.character.states.MCCSHurt;
import com.walk.or.die.engine.sm.entity.character.states.MCCSIdle;
import com.walk.or.die.engine.sm.entity.character.states.MCCSReady;
import com.walk.or.die.engine.sm.entity.character.states.MCCSShoot;
import com.walk.or.die.engine.sm.entity.character.states.MCCSSpeedShoot;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;

/**
 * Represents a controllable character entity (an ally).
 */
public class MCAlly extends MCCharacter {
    /**
     * Manages the turn-based action states for this ally.
     */
    public class AllyTurnState {
        /**
         * Did it already move?
         */
        public boolean hasMoved = false;
        /**
         * Did it already attack?
         */
        public boolean hasAttacked = false;
        /**
         * Did it already use one of its capacities?
         */
        public boolean hasUsedCapacity = false;
        
        public AllyTurnState() {}

        /**
         * Resets to the default turn state.
         */
        public void reset() {
            hasMoved = false;
            hasAttacked = false;
            // Capacité = une seule fois par game
        }

        /**
         * Checks if the ally can move.
         * @return True if it can move, false otherwise.
         */
        public boolean canMove() {return !hasMoved;}
        /**
         * Checks if the ally can attack.
         * @return True if it can attack, false otherwise.
         */
        public boolean canAttack() {return !hasAttacked;}
        /**
         * Checks if the ally can use a capacity.
         * @return True if it can use a capacity, false otherwise.
         */
        public boolean canUseCapacity() {return !hasUsedCapacity;}

        /**
         * Called when the ally moved during this turn.
         */
        public void moved() {hasMoved = true;}
        /**
         * Called when the ally attacked during this turn.
         */
        public void attacked() {hasAttacked = true;}
        /**
         * Called when the ally used a capacity during this turn.
         */
        public void capacityUsed() {hasUsedCapacity = true;}
    }   

    private int priorityLevel;

    /**
     * Stores info about what the ally did during the current turn.
     */
    private AllyTurnState turnState = new AllyTurnState();

    /**
     * Constructs a new ally instance.
     * @param parent The parent game instance.
     * @param map The terrain map associated with this entity.
     * @param entityGenericName The unique identifier or name for this entity type.
     */
    public MCAlly(MCGame parent, MCTerrainMap map, String entityGenericName) {
        super(parent, map, entityGenericName);

        MCStateMachine stateManager = getStateManager();
        stateManager.addState(new MCCSClickMove(this));
        stateManager.addState(new MCCSIdle(this));
        stateManager.addState(new MCCSAim(this));
        stateManager.addState(new MCCSShoot(this));
        stateManager.addState(new MCCSReady(this));
        stateManager.addState(new MCCSHurt(this));
        stateManager.addState(new MCCSDead(this));
        stateManager.addState(new MCCSSpeedShoot(this));
        stateManager.addState(new MCCSCapacityChoose(this));
    }

    /**
     * Initializes from map properties.
     *
     * @param props the MapProperties instance
     * @throws MissingDataException When the map properties are missing or invalid
     */
    @Override
    public void initFromMapProperties(MapProperties props) throws MissingDataException {
        priorityLevel = MCUtils.getIntProperty(props, "priorityLevel", 0);
    }

    /**
     * Called when a new turn begins.
     * Resets the current turn state for this ally so it can move/attack again.
     */
    @Override
    public void newTurn() {
        super.newTurn();
        getTurnState().reset();
    }

    /**
     * Gets the current turn state object for this ally.
     * @return The state object tracking movement and actions for the current turn.
     * @see AllyTurnState
     */
    public AllyTurnState getTurnState() {
        return turnState;
    }

    /**
     * Gets the priority level
     * @return priorityLevel
     */
    public int getPriorityLevel() {
        return this.priorityLevel;
    }
}