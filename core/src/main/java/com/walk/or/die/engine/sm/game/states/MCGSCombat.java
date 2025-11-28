package com.walk.or.die.engine.sm.game.states;

import com.badlogic.gdx.Input;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.sm.game.MCGameState;

public class MCGSCombat extends MCGameState<MCGSCombat.CombatStateArgs> {
    public static class CombatStateArgs extends MCGameState.StateArgs {}

    public MCGSCombat(MCGame parent) {
        super(parent);
        this.name = "combat";
    }

    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    @Override
    public void enter(CombatStateArgs args) {
        MCCameraManager.get().setMode(MCCameraManager.CameraMode.ARROWS);
        bus.on(this, "InputPressed", this::inputPressed);
        super.enter(args);
    }

    @Override
    public void exit() {
        bus.off(this, "InputPressed");
        super.exit();
    }
    
    protected void inputPressed(MCInputManager.Command data) {
        if (data instanceof MCInputManager.ClickTileCommand tileCmd) {
            //System.out.println("Détecté par le game");
            MCEntity e = MCEntityManager.get().getEntityFromTile(1, tileCmd.getVector());
            if (e instanceof MCAlly ally) parent.changeFocus(ally);
        } else if (data instanceof MCInputManager.OtherKeyCommand keyCmd) {
            if (keyCmd.key == Input.Keys.F) {
                System.out.println("f!!!!");
                changeState("exploration", new MCGSExploration.ExplStateArgs());
            }
        }
    }
}
