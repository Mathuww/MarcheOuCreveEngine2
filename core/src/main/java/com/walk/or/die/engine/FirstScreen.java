package com.walk.or.die.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.assets.AssetManager;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    private final MarcheOuCreve game;
    private OrthographicCamera cam;
    private AssetManager drh;

    private Player player;
    private float speed = 4f;

    private Deque<Vector2> movements;
    private InputManager inputHandler;

    private final float CAM_MARGIN_X = 2f;
    private final float CAM_MARGIN_Y = 2f;
    private final float CAM_LERP = 3f;

    private boolean moving = false;
    private float percent = 0f;
    private Vector2 start = new Vector2(0,0);
    private Vector2 deplacement = new Vector2(0, 0);

    private Map map;

    private ShapeRenderer debugRenderer = new ShapeRenderer();

    public FirstScreen(final MarcheOuCreve game) throws DataException {
        this.game = game;
        //cam = new OrthographicCamera();
        cam = (OrthographicCamera)game.viewport.getCamera();
        System.out.println("test : " + game.viewport.getWorldHeight() + game.viewport.getWorldWidth());
        cam.setToOrtho(false, 8, 5); 

        movements = new ArrayDeque<>();
        inputHandler = new InputManager(game.viewport);
        Gdx.input.setInputProcessor(inputHandler);

        drh = new AssetManager();
        map = new Map("unoriginal_packed_maps/CArte.tmx", cam, drh);
        try {
            player = new Player(map, map.getSpawnPos("player"));
        } catch (DataException e) {
            throw new DataException("cannot create player : " + e.getMessage());
        }
    }

    // Called once (when the window oppened)
    @Override
    public void show() {
        // Prepare your screen here.
    }

    // Called every frame
    @Override
    public void render(float delta) {
        input(delta);
        logic();
        draw(delta);
    }

    private void tryAddMovement(Vector2 movement) {
        if (map.isWalkable(MathUtils.floor(movement.x), MathUtils.floor(movement.y))) {
            movements.addLast(movement);
        }
    }

    private void input(float delta) {
        Queue<InputManager.Command> commands = inputHandler.getCommands();

        while(!commands.isEmpty()) {
            InputManager.Command cmd = commands.poll();

            if (moving) continue;

            if (cmd instanceof InputManager.ClickTileCommand cc) {
                Vector2 targetPos = new Vector2(cc.tileX, cc.tileY);
                List<Vector2> path = map.getPath(player.getPosition(), targetPos);
                movements.addAll(path);
            } else if (cmd instanceof InputManager.OneMoveCommand omc) {
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

            //player.update(delta);
        }
    }


    private float sign(float x) {
        if (x > 0f) return 1f ;
        if (x < 0f) return -1f ;
        return 0f;
    }

    private void logic() {

    }

    private void updateCamera(float delta) {
        float maxLeft = cam.position.x - cam.viewportWidth / 2 + CAM_MARGIN_X;
        float maxRight = cam.position.x + cam.viewportWidth / 2 - CAM_MARGIN_X;
        float maxBottom = cam.position.y - cam.viewportHeight / 2 + CAM_MARGIN_Y;
        float maxTop = cam.position.y + cam.viewportHeight / 2 - CAM_MARGIN_Y;

        float px = player.getX() + player.getSize() / 2;
        float py = player.getY() + player.getSize() / 2;

        float targetX = cam.position.x;
        float targetY = cam.position.y;

        float camHalfWidth = cam.viewportWidth / 2;
        float camHalfHeight = cam.viewportHeight / 2;

        if (px < maxLeft) targetX = px + camHalfWidth - CAM_MARGIN_X;
        else if (px > maxRight) targetX = px - camHalfWidth + CAM_MARGIN_X;

        if (py < maxBottom) targetY = py + camHalfHeight - CAM_MARGIN_Y;
        else if (py > maxTop) targetY = py - camHalfHeight + CAM_MARGIN_Y;

        float mapWidth = map.getWidth();
        float mapHeight = map.getHeight();

        targetX = MathUtils.clamp(targetX, camHalfWidth, mapWidth - camHalfWidth);
        targetY = MathUtils.clamp(targetY, camHalfHeight, mapHeight - camHalfHeight);

        printPos("cameraPos", new Vector2(targetX, targetY));

        cam.position.x += (targetX - cam.position.x) * CAM_LERP * delta;
        cam.position.y += (targetY - cam.position.y) * CAM_LERP * delta;
        //float ppu = map.getTileSize();
        //cam.position.x = MathUtils.round(targetX * ppu) / ppu;
        //cam.position.y = MathUtils.round(targetY * ppu) / ppu;
        //cam.position.x = targetX;
        //cam.position.y = targetY;

        cam.update();
    }

    private void draw(float delta) {
        ScreenUtils.clear(Color.BLACK);
        updateCamera(delta);
        game.viewport.apply();
        map.render();
        game.batch.setProjectionMatrix(cam.combined);
        game.batch.begin();

        //float scrWidth = game.viewport.getWorldWidth();
        //float scrHeight = game.viewport.getWorldHeight();

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
       game.viewport.update(width, height, true);
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