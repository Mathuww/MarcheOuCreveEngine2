package com.walk.or.die.engine.sm.game.states;

import java.util.ArrayDeque;
import java.util.Queue;

import com.badlogic.gdx.Input;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCEnemy;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.sm.MCState;
import com.walk.or.die.engine.sm.game.MCGameState;
import com.walk.or.die.engine.sm.game.states.MCGSAlliesPlaying.AlliesPlayingArgs;

public class MCGSEnemiesPlaying extends MCGameState<MCGSEnemiesPlaying.EnemiesPlayingArgs> {

    Queue<MCEnemy> enemies = new ArrayDeque<>();

    public static class EnemiesPlayingArgs extends MCState.StateArgs {

    }


    public MCGSEnemiesPlaying(MCGame parent) {
        super(parent);
        this.name = "EnemiesPlaying";
    }

    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    @Override
    public void enter(EnemiesPlayingArgs args) {
        super.enter(args);
        enemies.addAll(MCEntityManager.get().getEnemies());
        playOne();
    }

    private void playOne() {
        if (enemies.isEmpty()) {
            changeState("AlliesPlaying", new MCGSAlliesPlaying.AlliesPlayingArgs());
            return;
        }
        MCEnemy enemy = enemies.poll();
        if (enemy != null && !enemy.isDead())
            enemy.playDecision(this::playOne);
    }

    @Override
    public void exit() {
        super.exit();
    }

    
}
