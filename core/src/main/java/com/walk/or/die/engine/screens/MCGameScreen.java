package com.walk.or.die.engine.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Queue;


import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.MCEventBus;
import com.walk.or.die.engine.cameras.MCArrowsCamBehavior;
import com.walk.or.die.engine.cameras.MCCameraBehavior;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.cameras.MCFollowCamBehavior;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCAttackFactory;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.entities.MCEntityFactory;
import com.walk.or.die.engine.exceptions.DataException;
import com.walk.or.die.engine.exceptions.UnexistingBehaviorException;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.game.MCGameState;
import com.walk.or.die.engine.sm.game.states.MCGSCombat;
import com.walk.or.die.engine.sm.game.states.MCGSExploration;
import com.walk.or.die.engine.tiledmap.MCGameMap;
import com.walk.or.die.engine.tiledmap.MCMap;

public class MCGameScreen implements Screen {
    private MCGame game;
    private AssetManager drh;
    
    // Map
    private MCGameMap map;
    private final String MAP_ROOT = "tiled/packed/maps/";

    private final MCEntityFactory entityFact = MCEntityFactory.get();
    private final MCAttackFactory attackFact = MCAttackFactory.get();

    // Camera
    private MCCameraManager camManager;

    // Input
    private MCInputManager inputHandler;
    
    // Entity
    private Array<MCEntity> entities;
    private String playerEntityName;
    private MCCharacter main;
    private MCEntity focusedEntity;
    private float speed = 4f;
    
    // Movements
    private Deque<Vector2> movements;
    private boolean moving = false;
    private float percent = 0f;
    private Vector2 start = new Vector2(0,0);
    private Vector2 deplacement = new Vector2(0, 0);

    public MCGameScreen(MCGame game) throws DataException {
        this.game = game;
        drh = new AssetManager();
        entities = new Array<>();

        game.getStateManager().setCurrentState("combat", new MCGSCombat.CombatStateArgs());

        map = new MCGameMap(MAP_ROOT + "start.tmx", drh);

        camManager = MCCameraManager.get();
        camManager.init(16, 10, MCCameraManager.CameraMode.ARROWS);
        camManager.setLimitX(map.getWidth());
        camManager.setLimitY(map.getHeight());
        //camManager.setFollowTarget(entities.get(0));
        game.viewport.setCamera(camManager.getGdxCam());

        movements = new ArrayDeque<>();
        inputHandler = new MCInputManager(game.viewport);
        Gdx.input.setInputProcessor(inputHandler);

        MCEventBus bus = MCEventBus.get();
        //bus.on(this, "ChangedFocus", this::changeFocus);
        bus.on(this, "InputPressed", this::inputPressed);

        entityFact.init(drh);
        attackFact.init(drh);

        try {
            playerEntityName = map.getPlayerEntityType();
            main = (MCCharacter) entityFact.build(this, map, playerEntityName, "player");
            main.setPosition(map.getPlayerSpawnPos());
            camManager.setFollowTarget(main);
            entities.add(main);
            entities.addAll(map.getEntitiesToSpawn(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (MCEntity e : entities) {
            e.playAnimation("idle");
        }
    }

    // Called once (when the window oppened)
    @Override
    public void show() {}

    // Called every frame
    @Override
    public void render(float delta) {
        logic(delta);
        draw(delta);
    }

    private void logic(float delta) {
        // We don't give a fuck about logic
        // Because we implement MVC (Modular Venomous Contraception) // co autored by mathuww
    }

    private void draw(float delta) {
        //Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();

        try {
            camManager.update(delta);
        } catch (UnexistingBehaviorException e) {
            e.printStackTrace();
        }

        game.getStateManager().update(delta);

        map.render(camManager.getGdxCam());
        game.batch.setProjectionMatrix(camManager.getGdxCam().combined);       
        game.batch.begin();

        for (MCEntity e : entities) {
            e.update(delta);
            e.render(game.batch);
        }

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
       //game.viewport.update(width, height, false);
       game.viewport.update(width, height, false);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }
    
    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
    }

    public MCEntity getEntityFromTile(int layer, Vector2 pos) {
        for (MCEntity e: entities) {
            if (e.getTilePosition().x == pos.x && e.getTilePosition().y == pos.y && e.getLayer() == layer) return e;
        }
        return null;
    }

    public MCEntity getFocusedEntity() {
        return focusedEntity;
    }

    public void changeFocus(MCEntity e) {
        if (focusedEntity != null) {
            System.out.println("Yey");
            if (focusedEntity.loseFocus()) {
                focusedEntity = e;
                if (e != null) {
                    e.getFocus();
                    System.out.println("Haha");
                }
            }
        } else {
            System.out.println("AAAAAAAAA");
            focusedEntity = e;
            if (e != null) e.getFocus();
        }
    }

    protected void inputPressed(MCInputManager.Command data) {
        if (data instanceof MCInputManager.ClickTileCommand tileCmd) {
            //System.out.println("Détecté par le game");
            MCEntity e = getEntityFromTile(1, tileCmd.getVector());
            if (e instanceof MCAlly ally) changeFocus(ally);
        }
    }

}