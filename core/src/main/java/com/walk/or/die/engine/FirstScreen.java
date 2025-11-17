package com.walk.or.die.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayDeque;
import java.util.Deque;

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    private final MarcheOuCreve game;
    private OrthographicCamera cam;
    private AssetManager drh;

    private Rectangle macronRect;
    private Texture macronTx;
    private Sprite macronSpr;
    private float speed = 4f;

    private Deque<Vector2> movements;

    private final float TILE_SIZE = 1f;
    private boolean moving = false;
    private float percent = 0f;
    private Vector2 start = new Vector2(0,0);
    private Vector2 deplacement = new Vector2(0, 0);

    private Map map;

    public FirstScreen(final MarcheOuCreve game) {
        this.game = game;
        cam = new OrthographicCamera();
        cam.setToOrtho(false, 8, 5); 

        movements = new ArrayDeque<>();

        macronTx = new Texture("sprites/political/spritemacron.png");
        macronSpr = new Sprite(macronTx);
        macronSpr.setSize(1f, 1f);
        macronSpr.setPosition(0, 0);
        macronRect = new Rectangle();
        macronRect.set(0, 0, macronSpr.getWidth(), macronSpr.getHeight());

        drh = new AssetManager();
        map = new Map("unoriginal_packed_maps/CArte.tmx", cam , 1/16f, drh);
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
        if (!moving && !movements.isEmpty()) {
            Vector2 end = movements.removeFirst();
            deplacement.x = (int) (end.x - macronRect.x);
            deplacement.y = (int) (end.y - macronRect.y);
            start.x = macronRect.x;
            start.y = macronRect.y;
            moving = true;
        }
        if (Gdx.input.justTouched() && !moving) {
            int screenX = Gdx.input.getX();
            int screenY = Gdx.input.getY();
            Vector3 worldCoords = new Vector3(screenX, screenY,0);
            game.viewport.getCamera().unproject(worldCoords);

            for (Vector2 i : map.getPath(
                new Vector2(macronRect.x, macronRect.y),
                new Vector2(MathUtils.floor(worldCoords.x), MathUtils.floor(worldCoords.y)
                ))) {
                    movements.addLast(i);
            }
            System.out.println(deplacement);
        }

        if (moving) {
            if (deplacement.x != 0f) {
                percent += delta*speed/Math.abs(deplacement.x);
                if (percent >= 1f) {
                    percent = 0f;
                    macronRect.x = start.x + deplacement.x;
                    deplacement.x = 0f;
                } else {
                    macronRect.x = start.x + deplacement.x*percent;
                }
            }
            else if (deplacement.y != 0f) {
                percent += delta*speed/Math.abs(deplacement.y);
                if (percent >= 1f) {
                    percent = 0f;
                    macronRect.y = start.y + deplacement.y;
                    deplacement.y = 0f;
                } else {
                    macronRect.y = start.y + deplacement.y*percent;
                }
            } else {
                moving = false;
            }
            macronSpr.setPosition(macronRect.x, macronRect.y);

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
        ScreenUtils.clear(Color.RED);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        map.render();
        game.batch.begin();

        float scrWidth = game.viewport.getWorldWidth();
        float scrHeight = game.viewport.getWorldHeight();

        macronSpr.draw(game.batch);

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
}