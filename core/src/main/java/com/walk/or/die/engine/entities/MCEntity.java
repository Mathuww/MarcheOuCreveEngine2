package com.walk.or.die.engine.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.sm.entity.MCEntityStateMachine;
import com.walk.or.die.engine.sm.entity.states.MCESClickMove;
import com.walk.or.die.engine.sm.entity.states.MCESIdle;
import com.walk.or.die.engine.tiledmap.MCMap;

public class MCEntity {
    private MCMap map;
    private Rectangle hitbox;
    private TextureRegion currentRegion;
    private Sprite sprite;
    private MCEntityStateMachine stateManager;

    private float SIZE = 1f;

    public MCEntity(MCMap map, Vector2 spawn, TextureRegion baseRegion) {
        this.map = map;
        currentRegion = baseRegion;
        stateManager = new MCEntityStateMachine(this);
        stateManager.addState(new MCESClickMove(this));
        stateManager.addState(new MCESIdle(this));
        stateManager.setCurrentState("idle", new MCESIdle.IdleStateArgs());

        sprite = new Sprite(currentRegion);
        sprite.setSize(SIZE, SIZE);
        sprite.setPosition(spawn.x, spawn.y);

        hitbox = new Rectangle(spawn.x, spawn.y, sprite.getWidth(), sprite.getHeight());
    }

    public void update(float delta) {
        // utile pour ajouter des anims par la suite hihihi
        stateManager.update(delta);
        sprite.setPosition(hitbox.x, hitbox.y);
        sprite.setRegion(currentRegion);
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public MCEntityStateMachine getStateManager() {
        return this.stateManager;
    }

    public void setStateManager(MCEntityStateMachine stateManager) {
        this.stateManager = stateManager;
    }

    public float getX() {
        return this.hitbox.x;
    }

    public float getY() {
        return this.hitbox.y;
    }

    public void setX(float x) {
        this.hitbox.x = x;
    }

    public void setY(float y) {
        this.hitbox.y = y;
    }

    public float getSize() {
        return sprite.getWidth();
    }

    public Vector2 getPosition() {
        return new Vector2(this.hitbox.x, this.hitbox.y);
    }

    public void setPosition(float x, float y) {
        this.hitbox.setPosition(x, y);
    }

    public MCMap getMap() {
        return map;
    }
}