package com.walk.or.die.engine.sm.game.states;

import com.badlogic.gdx.Input;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.cameras.MCCameraManager.CameraMode;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEnemy;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.entities.MCExplorationPlayer;
import com.walk.or.die.engine.exceptions.MissingDataException;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.sm.game.MCGameState;
import com.walk.or.die.engine.ui.MCHUDManager;

public class MCGSAlliesPlaying extends MCGSCombat<MCGSAlliesPlaying.AlliesPlayingArgs> {

    /**
     * Arguments for the allies playing state.
     */
    public static class AlliesPlayingArgs extends MCGameState.StateArgs {
        /**
         * Constructs a new AlliesPlayingArgs instance.
         */
        public AlliesPlayingArgs() {}
    }

    /**
     * Constructs a new MCGSAlliesPlaying state.
     * @param parent The parent game instance.
     */
    public MCGSAlliesPlaying(MCGame parent) {
        super(parent);
        this.name = "AlliesPlaying";
    }

    /**
     * Called on each frame.
     * @param delta The time in seconds since the last frame.
     */
    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    /**
     * Called at state entrance.
     * Sets up the event listeners, resets the allies' turn states,
     * then sets up both the camera and the HUD.
     * @param args The arguments for the state.
     */
    @Override
    public void enter(AlliesPlayingArgs args) {
        //System.out.println("entering allies playing");
        bus.on(this, "InputPressed", this::inputPressed);
        bus.on(this, "CombatDone", this::combatDone);

        for (MCAlly a : MCEntityManager.get().getAllies())
            a.newTurn();

        hudManager.getCharacterHud().setRightPanelDisplay(true);
        hudManager.getSimpleHud().setText("END TURN");
        hudManager.getSimpleHud().setAction(
            () -> bus.emit("InputPressed", new MCInputManager.NextTurnCommand())
        );
        hudManager.getSimpleHud().enable();

        // pour le démarrage du jeu 
        camManager.setMode(CameraMode.ARROWS);
        super.enter(args);
    }

    /**
     * Called at state exit.
     * Hides the HUD.
     */
    @Override
    public void exit() {
        System.out.print("exiting allies playing");
        bus.off(this, "InputPressed");
        bus.off(this, "CombatDone");
        hudManager.getCharacterHud().hide();
        super.exit();
    }

    /**
     * Reacts to input commands not handled by the entities nor the HUD.
     * Will instantly return if any entity is in a busy state.
     * This event listener is primarily used to switch HUD focus between characters.
     * @param data The input command data.
     */
    public void inputPressed(MCInputManager.Command data) {
        super.inputPressed(data);

        if (MCEntityManager.get().isAnyoneBusy()) {
            //System.out.println("cant process input, someone s busy");
            return; // on attend tranquillement qu'un ennemi finisse d'etre hurt, etc.
        }

        if (data instanceof MCInputManager.ClickTileCommand tileCmd) {
            //System.out.println("current game state input istener is alliesplaying");
            MCEntity e = MCEntityManager.get().getEntityFromTile(1, tileCmd.getIntVect());

            if (e instanceof MCCharacter c) {
                hudManager.getCharacterHud().setCharacter(c);
            } else if (e == null) // pour cacher le hud en cliquant sur une tile vide
                hudManager.getCharacterHud().hide();

            if (e instanceof MCAlly ally) 
                parent.changeFocus(ally);
        } else if (data instanceof MCInputManager.NextTurnCommand) {
            changeState("EnemiesPlaying", new MCGSEnemiesPlaying.EnemiesPlayingArgs());
        }
    }

    /**
     * Called when a combat is done. <br>
     * Delegates the logic to MCGSCombat.
     * @param args Indicates which team won.
     */
    @Override
    public void combatDone(MCGame.CombatDoneArgs args) {
        //System.out.println("received combat done evt");
        super.combatDone(args);
    }
}