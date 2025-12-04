package com.walk.or.die.engine.sm.game.states;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Input;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.sm.entity.states.MCESAim;
import com.walk.or.die.engine.sm.entity.states.MCESReady;
import com.walk.or.die.engine.sm.game.MCGameState;

public class MCGSAlliesPlaying extends MCGameState<MCGSAlliesPlaying.AlliesPlayingArgs> {

    public static class AlliesPlayingArgs extends MCGameState.StateArgs {
        public AlliesPlayingArgs() {}
    }


    public MCGSAlliesPlaying(MCGame parent) {
        super(parent);
        this.name = "AlliesPlaying";
    }

    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    @Override
    public void enter(AlliesPlayingArgs args) {
        bus.on(this, "InputPressed", this::inputPressed);
        System.out.println("entering allies playing");
        super.enter(args);

        for (MCAlly a : MCEntityManager.get().getAllies()) {
            System.out.println("adding to allies turn states " + a.getId());
            a.getTurnState().reset();
        }

    }

    @Override
    public void exit() {
        bus.off(this, "InputPressed");
        super.exit();
    }

    protected void inputPressed(MCInputManager.Command data) {
        if (MCEntityManager.get().isAnyoneBusy())
            return; // on attend tranquillement qu'un ennemi finisse d'etre hurt, etc.

        if (data instanceof MCInputManager.ClickTileCommand tileCmd) {
            //System.out.println("Détecté par le game");
            MCEntity e = MCEntityManager.get().getEntityFromTile(1, tileCmd.getIntVect());
            if (e instanceof MCAlly ally) 
                parent.changeFocus(ally);
        } else if (data instanceof MCInputManager.NextTurnCommand) {
            changeState("EnemiesPlaying", new MCGSEnemiesPlaying.EnemiesPlayingArgs());
        } /* else if (data instanceof MCInputManager.OtherKeyCommand keyCmd) {
            if (keyCmd.key == Input.Keys.F) {
                System.out.println("f!!!!");
                changeState("exploration", new MCGSExploration.ExplStateArgs());
            }
        } */
    }
}
