package com.walk.or.die.engine.sm.game.states;

import com.badlogic.gdx.Input;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.entities.MCExplorationPlayer;
import com.walk.or.die.engine.exceptions.MissingDataException;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.sm.game.MCGameState;
import com.walk.or.die.engine.ui.MCHUDManager;

public class MCGSExploration extends MCGameState<MCGSExploration.ExplStateArgs> {
    public static class ExplStateArgs extends MCGameState.StateArgs {}

    public MCGSExploration(MCGame parent) {
        super(parent);
        this.name = "Exploration";
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
     * Sets up camera & HUD.
     * @param args The arguments passed to the state.
     */
    @Override
    public void enter(ExplStateArgs args) {
        super.enter(args);
        MCExplorationPlayer player = MCEntityManager.get().getExplorationPlayer();
        if (player == null) {
            System.err.println("no player in this map");
            return;
        }
        camManager.setFollowTarget(player);
        camManager.setMode(MCCameraManager.CameraMode.FOLLOW);
        bus.on(this, "InputPressed", this::inputPressed);
        hudManager.getSimpleHud().setText("INVENTORY");
        hudManager.getSimpleHud().setAction(() -> System.out.println("Inventory shown"));
        hudManager.getSimpleHud().enable();
        hudManager.getCharacterHud().hide();
    }
    
    /**
     * Called at state exit.
     */
    @Override
    public void exit() {
        bus.off(this, "InputPressed");
        super.exit();
    }
    
    /**
     * Handles input when a key is pressed.
     *
     * @param data The input command data.
     */
    protected void inputPressed(MCInputManager.Command data) {
        //System.out.println("Input pressed detect in Idle");
        if (data instanceof MCInputManager.OtherKeyCommand keyCmd) {
            if (keyCmd.key == Input.Keys.C) {
                camManager.setMode(MCCameraManager.CameraMode.ARROWS);
                changeState("AlliesPlaying", new MCGSAlliesPlaying.AlliesPlayingArgs());
            }
        }
    }

    /**
     * Called on each frame.
     * @param delta The time in seconds since the last frame.
     */
    @Override
    public void render(float delta) {
        super.render(delta);
    }
}