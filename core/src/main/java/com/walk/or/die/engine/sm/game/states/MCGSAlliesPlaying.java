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
import com.walk.or.die.engine.sm.game.states.MCGSCombat.CombatStateArgs;

public class MCGSAlliesPlaying extends MCGameState<MCGSAlliesPlaying.AlliesPlayingArgs> {
    private class AllyTurnState {
        public boolean hasMoved = false;
        public boolean hasAttacked = false;
        public boolean hasUsedCapacity = false;
        
        public AllyTurnState() {}
    }

    private Map<MCAlly, AllyTurnState> alliesStates = new HashMap<>();
    private MCCharacter focusedCharacter;
    private boolean busy = false;

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
        bus.on(this, "ActionCancelled", this::onActionCancelled);
        bus.on(this, "ActionDone", this::onActionDone);
        System.out.println("entering allies playing");
        super.enter(args);

        alliesStates.clear();
        for (MCAlly a : MCEntityManager.get().getAllies()) {
            System.out.println("adding to allies turn states " + a.getId());
            alliesStates.put(a, new AllyTurnState());
        }
    }

    @Override
    public void exit() {
        bus.off(this, "InputPressed");
        bus.off(this, "ActionCancelled");
        bus.off(this, "ActionDone");
        super.exit();
    }

    public void changeFocus(MCCharacter c) {
        if (focusedCharacter != null) {
            if (focusedCharacter.loseFocus()) {
                focusedCharacter = c;
                if (c != null) 
                    c.getFocus();
            }
        } else {
            focusedCharacter = c;
            if (c != null)
                c.getFocus();
        }
    }

    private void onActionCancelled(MCCharacter.ActionCancelledEvent evt) {
        busy = false;
    }

    private void onActionDone(MCCharacter.ActionDoneEvent evt) {
        MCCharacter c = evt.character;
        AllyTurnState turnState = alliesStates.get(c);
        switch (evt.action) {
            case MOVE -> turnState.hasMoved = true;
            case ATTACK -> turnState.hasAttacked = true;
        }
        busy = false;
    }
    
    protected void inputPressed(MCInputManager.Command data) {
        if (MCEntityManager.get().isAnyoneBusy())
            return; // on attend tranquillement qu'un ennemi finisse d'etre hurt, etc.

        if (data instanceof MCInputManager.ClickTileCommand tileCmd) {
            //System.out.println("Détecté par le game");
            MCEntity e = MCEntityManager.get().getEntityFromTile(1, tileCmd.getIntVect());
            if (e instanceof MCAlly ally) 
                changeFocus(ally);
        } else if (data instanceof MCInputManager.NextTurnCommand) {
            changeState("EnemiesPlaying", new MCGSEnemiesPlaying.EnemiesPlayingArgs());
        } /* else if (data instanceof MCInputManager.OtherKeyCommand keyCmd) {
            if (keyCmd.key == Input.Keys.F) {
                System.out.println("f!!!!");
                changeState("exploration", new MCGSExploration.ExplStateArgs());
            }
        } */

        if (!busy && focusedCharacter != null) {
            AllyTurnState turnState = alliesStates.get(focusedCharacter);
            if (data instanceof MCInputManager.ReadyCommand) {
                if (turnState.hasMoved)
                    return;
                busy = true;
                bus.emit(
                    "ChangeStateCommand", 
                    new MCCharacter.ChangeStateCommandEvent(focusedCharacter, MCCharacter.ChangeStateCommandEvent.State.READY)
                );
                //changeState("ready", new MCESReady.ReadyStateArgs());
            } else if (data instanceof MCInputManager.AimCommand) {
                System.out.println("oh");
                if (turnState.hasAttacked)
                    return;
                busy = true;
                bus.emit(
                    "ChangeStateCommand", 
                    new MCCharacter.ChangeStateCommandEvent(focusedCharacter, MCCharacter.ChangeStateCommandEvent.State.AIM)
                );
                //changeState("aim", new MCESAim.AimStateArgs(focusedCharacter.getAttack()));
            } /* else if (data instanceof MCInputManager.ClickTileCommand) {
                parent.ai.searchShelts(parent.getTilePosition(), 5);
            } */
        }
    }
}
