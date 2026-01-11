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

public class MCGSEnemiesPlaying extends MCGSCombat<MCGSEnemiesPlaying.EnemiesPlayingArgs> {

    /**
     * Does not refer to a Allies/Enemies turn,
     * but to the internal turns within the current state. <br>
     * True if it is time to make another enemy play. <br>
     * Consumed when an enemy played. <br>
     * Allows us to set a min. time between two enemies playing.
     */
    private boolean nextTurnRequest = false;
    /**
     * Only true before the first enemy played.
     */
    private boolean firstPlay = false;
    /**
     * Stores the time elapsed since the last enemy played.
     */
    private float intermediateTime = 0f;
    /**
     * Used to artificially slow down the Enemies turn,
     * to make it more bearable to watch and more cinematic.
     */
    private final float MIN_TIME_BETWEEN_PLAYS = 2.5f;
    /**
     * Contains the enemies who should play.
     */
    private Queue<MCEnemy> enemies = new ArrayDeque<>();
    /**
     * Current processed enemy.
     */
    private MCEnemy enemy = null;

    /**
     * Used to reset HUD focus to the character selected before clicking END TURN.
     */
    private MCCharacter hudFocusBefore;
    /**
     * Used to reset camera position to the one it had before clicking END TURN.
     */
    private Vector2 cameraPosBefore = new Vector2();

    public static class EnemiesPlayingArgs extends MCState.StateArgs {}

    public MCGSEnemiesPlaying(MCGame parent) {
        super(parent);
        this.name = "EnemiesPlaying";
    }

    /**
     * Called on each frame.
     * If it is time for another enemy to play,
     * will check if enough time elapsed and if that is the case,
     * will make it play.
     * @param delta The time in seconds since the last frame.
     */
    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
        intermediateTime += delta;
        if (nextTurnRequest && !MCEntityManager.get().isAnyoneBusy() 
            && (intermediateTime >= MIN_TIME_BETWEEN_PLAYS || firstPlay) ||
            (enemy != null && enemy.isDead()) ) {
            intermediateTime = 0f;
            if (firstPlay) firstPlay = false;
            nextTurnRequest = false;
            playOne();
        }
    }

    /**
     * Called at state entrance.
     * Inits the enemies list, then setups both HUD & camera.
     * @param args The arguments passed to the state.
     */
    @Override
    public void enter(EnemiesPlayingArgs args) {
        super.enter(args);
        bus.on(this, "CombatDone", this::combatDone);
        enemies.clear();
        for (MCEnemy e : MCEntityManager.get().getEnemies()) e.newTurn();
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

    /**
     * Called when a new enemy should play.
     */
    private void playOne() {
        // nettoyer les ennemis morts
        while (!enemies.isEmpty() && enemies.peek().isDead())
            enemies.poll();

        if (enemies.isEmpty()) {
            changeState("AlliesPlaying", new MCGSAlliesPlaying.AlliesPlayingArgs());
            return;
        }
        enemy = enemies.poll();
        if (enemy != null && !enemy.isDead()) {
            camManager.setFollowTarget(enemy);
            hudManager.getCharacterHud().setCharacter(enemy);
            enemy.playDecision(() -> {
                nextTurnRequest = true;
            });
        } else 
            nextTurnRequest = true;
    }

    /**
     * Called at state exit. <br>
     * Resets HUD & camera to the state they were before.
     */
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

    /**
     * Called when a combat is done. <br>
     * Delegates logic to MCGSCombat.
     * @param args Which team won.
     */
    @Override
    public void combatDone(MCGame.CombatDoneArgs args) {
        System.out.println("received combat done evt");
        super.combatDone(args);
    }
}