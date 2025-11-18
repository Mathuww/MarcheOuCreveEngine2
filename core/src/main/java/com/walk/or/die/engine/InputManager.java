package com.walk.or.die.engine;

import java.util.ArrayDeque;
import java.util.Queue;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class InputManager implements InputProcessor {
    public static abstract class Command {}

    public static class OneMoveCommand extends Command {
        public float dx, dy;
        public OneMoveCommand(float dx, float dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    public static class ClickTileCommand extends Command {
        public float tileX, tileY;
        public ClickTileCommand(float tileX, float tileY) {
            this.tileX = tileX;
            this.tileY = tileY;
        }
    }

    private Queue<Command> commands;
    private Viewport vp;

    public InputManager(Viewport vp) {
        this.commands = new ArrayDeque<>();
        this.vp = vp;
    }

    public Queue<Command> getCommands() {
        return this.commands;
    }

    @Override
    public boolean keyDown(int k) {
        switch (k) {
            case Input.Keys.Z:
            case Input.Keys.UP:
                commands.add(new OneMoveCommand(0, +1));
                break;

            case Input.Keys.S:
            case Input.Keys.DOWN:
                commands.add(new OneMoveCommand(0, -1));
                break;

            case Input.Keys.Q:
            case Input.Keys.LEFT:
                commands.add(new OneMoveCommand(-1, 0));
                break;

            case Input.Keys.D:
            case Input.Keys.RIGHT:
                commands.add(new OneMoveCommand(+1, 0));
                break;
        }
        return true;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        Vector3 worldCoords = new Vector3(x, y, 0);
        vp.getCamera().unproject(worldCoords);
        Vector2 v = new Vector2(MathUtils.floor(worldCoords.x), MathUtils.floor(worldCoords.y));
        commands.add(new ClickTileCommand(v.x, v.y));

        return true;
    }

    @Override public boolean keyUp(int k){return false;}
    @Override public boolean keyTyped(char c){return false;}
    @Override public boolean touchUp(int x,int y,int p,int b){return false;}
    @Override public boolean touchDragged(int x,int y,int p){return false;}
    @Override public boolean touchCancelled (int screenX, int screenY, int pointer, int button) {return false;}
    @Override public boolean mouseMoved(int x,int y){return false;}
    @Override public boolean scrolled(float x,float y){return false;}
}
