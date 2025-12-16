package com.walk.or.die.engine.sm.game.states;

import java.util.ArrayDeque;
import java.util.Queue;

import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.cameras.MCCameraManager.CameraMode;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEnemy;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.input.MCInputManager.ClickTileCommand;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.sm.MCState;
import com.walk.or.die.engine.sm.game.MCGameState;
import com.walk.or.die.engine.ui.MCHUDManager;

public class MCGSEnemiesPlaying extends MCGameState<MCGSEnemiesPlaying.EnemiesPlayingArgs> {

    private boolean nextTurnRequest = false;
    private boolean firstPlay = false;
    private float intermediateTime = 0f;
    private final float MIN_TIME_BETWEEN_PLAYS = 2.5f;
    private Queue<MCEnemy> enemies = new ArrayDeque<>();

    private MCCharacter hudFocusBefore;
    private Vector2 cameraPosBefore = new Vector2();

    public static class EnemiesPlayingArgs extends MCState.StateArgs {

    }


    public MCGSEnemiesPlaying(MCGame parent) {
        super(parent);
        this.name = "EnemiesPlaying";
    }

    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
        intermediateTime += delta;
        if (nextTurnRequest && !MCEntityManager.get().isAnyoneBusy() 
            && (intermediateTime >= MIN_TIME_BETWEEN_PLAYS || firstPlay)) {
            intermediateTime = 0f;
            if (firstPlay) firstPlay = false;
            nextTurnRequest = false;
            playOne();
        }
    }

    @Override
    public void enter(EnemiesPlayingArgs args) {
        super.enter(args);
        enemies.clear();
        enemies.addAll(MCEntityManager.get().getEnemies());
        nextTurnRequest = true;
        firstPlay = true;
        hudFocusBefore = hudManager.getCharacterHud().getCharacter();
        hudManager.getSimpleHud().disable();
        hudManager.getCharacterHud().hide();
        hudManager.getCharacterHud().setRightPanelDisplay(false);

        cameraPosBefore = camManager.getPosition();
        camManager.setMode(CameraMode.FOLLOW);
    }

    private void playOne() {
        // nettoyer les ennemis morts
        while (!enemies.isEmpty() && enemies.peek().isDead())
            enemies.poll();

        if (enemies.isEmpty()) {
            changeState("AlliesPlaying", new MCGSAlliesPlaying.AlliesPlayingArgs());
            return;
        }
        MCEnemy enemy = enemies.poll();
        if (enemy != null && !enemy.isDead()) {
            camManager.setFollowTarget(enemy);
            hudManager.getCharacterHud().setCharacter(enemy);
            enemy.playDecision(() -> {
                nextTurnRequest = true;
            });
        } else 
            nextTurnRequest = true;
    }

    @Override
    public void exit() {
        nextTurnRequest = false;
        if (hudFocusBefore != null) {
            hudManager.getCharacterHud().setCharacter(hudFocusBefore);
        } else
            hudManager.getCharacterHud().hide();
        camManager.setMode(CameraMode.ARROWS);
        camManager.interpolateTo(cameraPosBefore);
        super.exit();
    }
}
