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

/**
 * Represents the game state where enemies are taking their turns.
 * This state manages enemy actions, turn progression, and associated UI/camera changes.
 */
public class MCGSEnemiesPlaying extends MCGSCombat<MCGSEnemiesPlaying.EnemiesPlayingArgs> {

    /**
     * Does not refer to an Allies/Enemies turn,
     * but to the internal turns within the current state. <br>
     * Indicates if it is time to make another enemy play. <br>
     * It is consumed when an enemy plays. <br>
     * Allows the setting of a minimum time between two enemies playing.
     */
    private boolean nextTurnRequest = false;
    /**
     * Indicates if it is true only before the first enemy plays.
     */
    private boolean firstPlay = false;
    /**
     * Stores the time elapsed since the last enemy played.
     */
    private float intermediateTime = 0f;
    /**
     * Used to artificially slow down the enemies' turn,
     * making it more bearable to watch and more cinematic.
     */
    private final float MIN_TIME_BETWEEN_PLAYS = 2.5f;
    /**
     * Contains the enemies that should play.
     */
    private Queue<MCEnemy> enemies = new ArrayDeque<>();
    /**
     * The current processed enemy.
     */
    private MCEnemy enemy = null;

    /**
     * Used to reset the HUD focus to the character selected before clicking END TURN.
     */
    private MCCharacter hudFocusBefore;
    /**
     * Used to reset the camera position to the one it had before clicking END TURN.
     */
    private Vector2 cameraPosBefore = new Vector2();

    /**
     * Arguments for the EnemiesPlaying state.
     */
    public static class EnemiesPlayingArgs extends MCState.StateArgs {}

    /**
     * Constructs a new MCGSEnemiesPlaying state.
     * @param parent The game instance.
     */
    public MCGSEnemiesPlaying(MCGame parent) {
        super(parent);
        this.name = "EnemiesPlaying";
    }

    /**
     * Called on each frame.
     * If it is time for another enemy to play, it checks if enough time has elapsed and, if so, makes the enemy play.
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
     * Initializes the enemies list, then sets up both the HUD and camera.
     * @param args The arguments passed to the state.
     */
    @Override
    public void enter(EnemiesPlayingArgs args) {
        super.enter(args);
        bus.on(this, "CombatDone", this::combatDone);
        enemies.clear();
        for (MCEnemy e : MCEntityManager.get().getEnemies()) {
            e.newTurn();
            e.onHudVisibilityLost();
        }
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
     * Is called when a new enemy should play.
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
     * Resets the HUD and camera to the state they were in before.
     */
    @Override
    public void exit() {
        nextTurnRequest = false;
        if (hudFocusBefore != null) {
            hudManager.getCharacterHud().setCharacter(hudFocusBefore);
        }
        hudManager.getCharacterHud().hide();
        camManager.setMode(CameraMode.ARROWS);
        camManager.interpolateTo(cameraPosBefore);
        bus.off(this, "CombatDone");
        super.exit();
    }

    /**
     * Is called when combat is done. <br>
     * Delegates logic to the MCGSCombat class.
     * @param args The arguments indicating which team won.
     */
    @Override
    public void combatDone(MCGame.CombatDoneArgs args) {
        //System.out.println("received combat done evt");
        super.combatDone(args);
    }
}