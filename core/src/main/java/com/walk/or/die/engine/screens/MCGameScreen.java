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
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Queue;


import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.cameras.MCCameraBehavior;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.cameras.MCCameraMode;
import com.walk.or.die.engine.cameras.MCFollowCamBehavior;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.exceptions.DataException;
import com.walk.or.die.engine.exceptions.UndefinedBehaviorException;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.tiledmap.MCMap;

public class MCGameScreen implements Screen {

    private MCGame game;
    private AssetManager drh;
    
    // Map
    private MCMap map;

    // Camera
    private MCCameraManager camManager;

    // Input
    private MCInputManager inputHandler;
    
    // Player
    private MCEntity player;
    private float speed = 4f;
    
    // Movements
    private Deque<Vector2> movements;
    private boolean moving = false;
    private float percent = 0f;
    private Vector2 start = new Vector2(0,0);
    private Vector2 deplacement = new Vector2(0, 0);

    // Debug
    //private ShapeRenderer debugRenderer = new ShapeRenderer();

    public MCGameScreen(MCGame game) throws DataException {
        camManager = MCCameraManager.get();
        camManager.init(8, 5);
        this.game = game;
        drh = new AssetManager();
        map = new MCMap("unoriginal_packed_maps/CArte.tmx", camManager.getGdxCam(), drh);
        try {
            TextureRegion playerTexture = map.getTileSet("player").getTileByType("player").getTextureRegion();
            player = new MCEntity(map, map.getEntitySpawnPos("player"), playerTexture);
        } catch (DataException e) {
            throw new DataException("cannot create player : " + e.getMessage());
        }

        camManager.setLimitX(map.getWidth());
        camManager.setLimitY(map.getHeight());
        game.viewport.setCamera(camManager.getGdxCam());
        camManager.register(MCCameraMode.FOLLOW, new MCFollowCamBehavior(player));
        camManager.setMode(MCCameraMode.FOLLOW);

        setupInput();
    }

    public void setupInput() {
        movements = new ArrayDeque<>();
        inputHandler = new MCInputManager(game.viewport);
        Gdx.input.setInputProcessor(inputHandler);
    }

    // Called once (when the window oppened)
    @Override
    public void show() {}

    // Called every frame
    @Override
    public void render(float delta) {
        input(delta);
        logic(delta);
        draw(delta);
    }

    private void tryAddMovement(Vector2 movement) {
        if (map.isWalkable(MathUtils.floor(movement.x), MathUtils.floor(movement.y))) {
            movements.addLast(movement);
        }
    }

    private void input(float delta) {
        Queue<MCInputManager.Command> commands = inputHandler.getCommands();

        while(!commands.isEmpty()) {
            MCInputManager.Command cmd = commands.poll();

            if (moving) continue;

            if (cmd instanceof MCInputManager.ClickTileCommand cc) {
                Vector2 targetPos = new Vector2(cc.tileX, cc.tileY);
                List<Vector2> path = map.getPath(player.getPosition(), targetPos);
                movements.addAll(path);
            } else if (cmd instanceof MCInputManager.DirectionalCommand omc) {
                Vector2 targetPos = new Vector2(player.getX() + omc.dx, player.getY() + omc.dy);
                tryAddMovement(targetPos);
            }
        }

        // long key presses
        /* 
         * C'est LA le pb avec la diagonale Eloi
         * bon c'est logique y'a aucune verif entre la case de départ et celle d'arrivée
         * qu'elles o
         */
        
    }

    private void logic(float delta) {
        // We don't give a fuck about logic
        // We're going to do random things
        // player.danseSalsa()
        // Ptn la fonction marche pas...
        // Bon bah on va bouger normalement les characters

/*
        if (!moving && movements.isEmpty()) {
            float x = player.getX(), y = player.getY();
            float dx = 0, dy = 0;

            if (inputHandler.isUpGoing()) dy += 1f;
            else if (inputHandler.isDownGoing()) dy -= 1f;
            else if (inputHandler.isLeftGoing()) dx -= 1f;
            else if (inputHandler.isRightGoing()) dx += 1f;

            // Ajout du mouvement si on a bougé
            if (x + dx != x || y + dy != y) {
                tryAddMovement(new Vector2(x + dx, y + dy));
            }
        }

        if (!moving && !movements.isEmpty()) {
            Vector2 end = movements.removeFirst();
            deplacement.x = MathUtils.floor(end.x - player.getX()); // avant : cast (int)end.x....
            deplacement.y = MathUtils.floor(end.y - player.getY());
            start.x = player.getX();
            start.y = player.getY();
            //printPos("start", start);
            moving = true;
        }

        if (moving) {
            //printPos("deplacement", deplacement);
            if (deplacement.x != 0f) {
                percent += delta*speed/Math.abs(deplacement.x);
                if (percent >= 1f) {
                    percent = 0f;
                    player.setX(start.x + deplacement.x);
                    deplacement.x = 0f;
                } else {
                    player.setX(start.x + deplacement.x*percent);
                }
            }
            else if (deplacement.y != 0f) {
                percent += delta*speed/Math.abs(deplacement.y);
                if (percent >= 1f) {
                    percent = 0f;
                    player.setY(start.y + deplacement.y);
                    deplacement.y = 0f;
                } else {
                    player.setY(start.y + deplacement.y*percent);
                }
            } else {
                moving = false;
            }
        } */
    }


    private void draw(float delta) {
        ScreenUtils.clear(Color.BLACK);
        try {
            camManager.update(delta);
        } catch (UndefinedBehaviorException e) {}

        game.viewport.apply();
        map.render();
        game.batch.setProjectionMatrix(camManager.getGdxCam().combined);       game.batch.begin();

        player.update(delta);
        player.render(game.batch);

        game.batch.end();

        // ---- DEBUG -----
        /*
        debugRenderer.setProjectionMatrix(cam.combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.RED);
        // On dessine exactement là où le code PENSE que le joueur est
        debugRenderer.rect(player.getX(), player.getY(), player.getSize(), player.getSize());
        debugRenderer.end();
        */
    }

    @Override
    public void resize(int width, int height) {
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

    private void printPos(String name, Vector2 pos) {
        System.out.println(name + " : " + pos.x + ", " + pos.y);
    }
}