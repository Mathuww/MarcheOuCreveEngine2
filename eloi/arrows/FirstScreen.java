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

/** First screen of the application. Displayed after the application is created. */
public class FirstScreen implements Screen {
    private final Main game;

    private Rectangle macronRect;
    private Texture macronTx;
    private Sprite macronSpr;
    private float speed = 4f;

    private final float TILE_SIZE = 1f;
    private float move_time = 0.25f;
    private int movement = 0;
    private boolean moving = false;
    private float percent = 0f;
    private Vector2 goal = new Vector2(0, 0);

    public FirstScreen(final Main game) {
        this.game = game;

        macronTx = new Texture("sprites/political/spritemacron.png");
        macronSpr = new Sprite(macronTx);
        macronSpr.setSize(1f, 1f);
        macronSpr.setPosition(2, 2);
        macronRect = new Rectangle();
        macronRect.set(2, 2, macronSpr.getWidth(), macronSpr.getHeight());
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
        if (!moving) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                goal.x -= TILE_SIZE;
                movement = 0;
                moving = true;
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                goal.x += TILE_SIZE;
                movement = 1;
                moving = true;
            } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                movement = 2;
                goal.y += TILE_SIZE;
                moving = true;
            } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                movement = 3;
                goal.y -= TILE_SIZE;
                moving = true;
            }
        }
        if (moving && movement != -1) {
            percent += delta/move_time;
            if (percent >= 1f) {
                switch (movement) {
                    case 0:
                        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                            goal.x -= TILE_SIZE;
                            percent -= 1f;
                            move(delta);
                            return ;
                        }
                        break;
                    case 1:
                        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                            goal.x += TILE_SIZE;
                            percent -= 1f;
                            move(delta);
                            return ;
                        }
                        break;
                    case 2:
                        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                            goal.y += TILE_SIZE;
                            percent -= 1f;
                            move(delta);
                            return ;
                        }
                        break;
                    case 3:
                        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                            goal.y -= TILE_SIZE;
                            percent -= 1f;
                            move(delta);
                            return ;
                        }
                        break;
                    default:
                        break;
                }
                macronRect.setPosition(goal);
                percent = 0f;
                moving = false;
                macronSpr.setPosition(macronRect.x, macronRect.y);
                
            } else {
                move(delta);
            }
        }
    }

    private void move(float delta) {
        macronRect.x += sign(goal.x - macronRect.x) * speed * delta;
        macronRect.y += sign(goal.y - macronRect.y) * speed * delta;

        macronSpr.setPosition(macronRect.x, macronRect.y);
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
        game.viewport.apply();;
        // Eloi la prochaine fois que tu mets deux points virgule d'affilée attention !!!
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
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