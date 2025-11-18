package com.walk.or.die.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

    //private final float TILE_SIZE = 1f;
    private boolean moving = false;
    private float percent = 0f;
    private Vector2 start = new Vector2(0,0);
    private Vector2 deplacement = new Vector2(0, 0);

    private Map map;

    public FirstScreen(final MarcheOuCreve game) throws DataException {
        this.game = game;
        cam = new OrthographicCamera();
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
        draw();
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
                if (map.isWalkable((int)targetPos.x, (int)targetPos.y)) {
                    movements.addLast(targetPos);
                }

            }
        }

        if (!moving && !movements.isEmpty()) {
            Vector2 end = movements.removeFirst();
            deplacement.x = (int) (end.x - player.getX());
            deplacement.y = (int) (end.y - player.getY());
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

            player.update(delta);
        }
    }


    private float sign(float x) {
        if (x > 0f) return 1f ;
        if (x < 0f) return -1f ;
        return 0f;
    }

    private void logic() {

    }

    private void draw() {
        ScreenUtils.clear(Color.WHITE);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        map.render();
        game.batch.begin();

        float scrWidth = game.viewport.getWorldWidth();
        float scrHeight = game.viewport.getWorldHeight();

        player.render(game.batch);

        game.batch.end();
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