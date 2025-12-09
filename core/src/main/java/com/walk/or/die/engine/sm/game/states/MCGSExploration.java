package com.walk.or.die.engine.sm.game.states;

import com.badlogic.gdx.Input;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.sm.game.MCGameState;

public class MCGSExploration extends MCGameState<MCGSExploration.ExplStateArgs> {
    public static class ExplStateArgs extends MCGameState.StateArgs {}

    public MCGSExploration(MCGame parent) {
        super(parent);
        this.name = "exploration";
    }

    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    @Override
    public void enter(ExplStateArgs args) {
        MCCameraManager.get().setMode(MCCameraManager.CameraMode.FOLLOW);
        super.enter(args);
        bus.on(this, "InputPressed", this::inputPressed);
    }

    @Override
    public void exit() {
        bus.off(this, "InputPressed");
        super.exit();
    }
    
    protected void inputPressed(MCInputManager.Command data) {
        //System.out.println("Input pressed detect in Idle");
        if (data instanceof MCInputManager.OtherKeyCommand keyCmd) {
            if (keyCmd.key == Input.Keys.C) {
                MCCameraManager.get().setMode(MCCameraManager.CameraMode.ARROWS);
                changeState("AlliesPlaying", new MCGSAlliesPlaying.AlliesPlayingArgs());
            }
        }
    }

}
