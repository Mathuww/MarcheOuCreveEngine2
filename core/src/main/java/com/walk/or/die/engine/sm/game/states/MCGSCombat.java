package com.walk.or.die.engine.sm.game.states;

import com.badlogic.gdx.Input;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.cameras.MCCameraManager.CameraMode;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.entities.MCExplorationPlayer;
import com.walk.or.die.engine.exceptions.MissingDataException;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.sm.game.MCGameState;
import com.walk.or.die.engine.ui.MCHUDManager;

public abstract class MCGSCombat<T extends MCGameState.StateArgs> extends MCGameState<T> {

    /**
     * Constructs a new MCGSCombat instance.
     * @param parent The game instance.
     */
    public MCGSCombat(MCGame parent) {
        super(parent);
    }

    /**
     * Called on each frame.
     * @param delta The time elapsed since the last frame.
     */
    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    /**
     * Called at state entrance.
     * @param args The arguments passed to the state upon entry.
     */
    @Override
    public void enter(T args) {
        super.enter(args);
    }

    /**
     * Called at state exit.
     */
    @Override
    public void exit() {
        super.exit();
    }

    /**
     * Provides a generic combat done input listener for both combat states.
     * If the allies won, it picks an ally to turn into an exploration player,
     * according to the following order of priority:
     * - The priority index specified in the ally's data.
     * - The ally with the highest health points.
     * @param cda Specifies which team won the battle.
     */
    public void combatDone(MCGame.CombatDoneArgs cda) {
        changeState("VeryBigInformation", new MCGSVeryBigInformation.MCGSVBIArgs(cda));
    }
}