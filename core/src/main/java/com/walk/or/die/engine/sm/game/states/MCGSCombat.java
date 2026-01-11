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

public class MCGSCombat<T extends MCGameState.StateArgs> extends MCGameState<T> {

    public MCGSCombat(MCGame parent) {
        super(parent);
    }

    /**
     * Called on each frame.
     */
    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    /**
     * Called at state entrance.
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
     * Generic combat done input listener for both combat states.
     * If the allies won, picks an ally to turn it into an exploration player,
     * according to this order of priority :
     * - The priority index specified in the ally's data.
     * - The ally with the highest health points.
     * @param args Specifies which team won the battle.
     */
    public void combatDone(MCGame.CombatDoneArgs args) {
        if (args == MCGame.CombatDoneArgs.ALLIES_WON) {
            // ... transformer l'ally élu en expl player!!
            // choisir celui avec le explPriority le plus haut (ajouter ça dans initFromProperties)
            // puis si meme priorty choisir le + haut HP
            changeState("exploration", new MCGSExploration.ExplStateArgs());
        } else if (args == MCGame.CombatDoneArgs.ENEMIES_WON) {
            parent.reloadMap();
        }
    }
}