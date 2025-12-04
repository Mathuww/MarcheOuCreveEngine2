package com.walk.or.die.engine.sm.game.states;

import java.util.ArrayDeque;
import java.util.Queue;

import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.entities.MCEnemy;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.sm.MCState;
import com.walk.or.die.engine.sm.game.MCGameState;

public class MCGSEnemiesPlaying extends MCGameState<MCGSEnemiesPlaying.EnemiesPlayingArgs> {

    private boolean nextTurnRequest = false;
    private Queue<MCEnemy> enemies = new ArrayDeque<>();

    public static class EnemiesPlayingArgs extends MCState.StateArgs {

    }


    public MCGSEnemiesPlaying(MCGame parent) {
        super(parent);
        this.name = "EnemiesPlaying";
    }

    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
        if (nextTurnRequest && !MCEntityManager.get().isAnyoneBusy()) {
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
        if (enemy != null && !enemy.isDead())
            enemy.playDecision(() -> {
                nextTurnRequest = true;
            });
        else 
            nextTurnRequest = true;
    }

    @Override
    public void exit() {
        nextTurnRequest = false;
        super.exit();
    }

    
}
