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
import com.walk.or.die.engine.entities.MCExplorationPlayer;
import com.walk.or.die.engine.exceptions.MissingDataException;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.sm.entity.character.states.MCESAim;
import com.walk.or.die.engine.sm.entity.character.states.MCESReady;
import com.walk.or.die.engine.sm.game.MCGameState;
import com.walk.or.die.engine.ui.MCHUDManager;

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
        System.out.println("entering allies playing");
        bus.on(this, "InputPressed", this::inputPressed);
        super.enter(args);

        for (MCAlly a : MCEntityManager.get().getAllies()) {
            System.out.println("adding to allies turn states " + a.getId());
            a.getTurnState().reset();
        }

    }

    @Override
    public void exit() {
        System.out.println("off input pressed allies playing state");
        bus.off(this, "InputPressed");
        super.exit();
    }

    protected void inputPressed(MCInputManager.Command data) {
        if (MCEntityManager.get().isAnyoneBusy())
            return; // on attend tranquillement qu'un ennemi finisse d'etre hurt, etc.

        if (data instanceof MCInputManager.ClickTileCommand tileCmd) {
            //System.out.println("Détecté par le game");
            MCEntity e = MCEntityManager.get().getEntityFromTile(1, tileCmd.getIntVect());

            if (e instanceof MCCharacter c)
                MCHUDManager.get().setHudTarget(c);
            else if (e == null) // pour cacher le hud en cliquant sur une tile vide
                MCHUDManager.get().setHudTarget(null);

            if (e instanceof MCAlly ally) 
                parent.changeFocus(ally);
        } else if (data instanceof MCInputManager.NextTurnCommand) {
            changeState("EnemiesPlaying", new MCGSEnemiesPlaying.EnemiesPlayingArgs());
        } else if (data instanceof MCInputManager.OtherKeyCommand keyCmd) {
            if (keyCmd.key == Input.Keys.E) {
                try {
                    MCExplorationPlayer player = MCEntityManager.get().getExplorationPlayer();
                    MCCameraManager.get().setFollowTarget(player);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("no player in this map");
                    return;
                }
                MCCameraManager.get().setMode(MCCameraManager.CameraMode.FOLLOW);
                changeState("exploration", new MCGSExploration.ExplStateArgs());
                System.out.println("Je m'ennuis, p'tit pause s'impose");
            }
        }
    }
}
