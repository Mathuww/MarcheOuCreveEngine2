package com.walk.or.die.engine.input;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.walk.or.die.engine.shared.MCEventBus;

public class MCInputManager implements InputProcessor {
    private static MCInputManager instance = null;

    public static MCInputManager get() {
        if (instance == null) instance = new MCInputManager();
        return instance;
    }

    public void init(Viewport v) {
        vp = v;
    }

    public static abstract class Command {}

    public static class DirectionalCommand extends Command {
        public float dx, dy;
        public DirectionalCommand(float dx, float dy) {
            this.dx = dx;
            this.dy = dy;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            DirectionalCommand comp = (DirectionalCommand) obj;
            return Float.compare(dx, comp.dx) == 0 && Float.compare(dy, comp.dy) == 0;
        }

        @Override
        public int hashCode() {
            int res = Float.hashCode(dx);
            res = 31 * res + Float.hashCode(dy);
            return res;
        }
    }

    public static class ClickTileCommand extends Command {
        public float tileX, tileY;
        public ClickTileCommand(float tileX, float tileY) {
            this.tileX = tileX;
            this.tileY = tileY;
        }
        public Vector2 getVector() {
            return new Vector2(tileX, tileY);
        }
    }

    public static class AimCommand extends Command {
        public AimCommand() {}
    }

    public static class ReadyCommand extends Command {
        public ReadyCommand() {}    
    }

    public static class OtherKeyCommand extends Command {
        public int key;
        public OtherKeyCommand(int key) {
            this.key = key;
        }
    }

    public static class Function {
        public Consumer<Vector2> mouseMovedFunction;

        public Function(Consumer<Vector2> consumer) {
            this.mouseMovedFunction = consumer;
        }
    }

    private Viewport vp;

    private boolean upGoing, downGoing, leftGoing, rightGoing;

    private MCEventBus bus;
    private Consumer<Vector2> mouseMovedFunction;

    private MCInputManager() {
        this.bus = MCEventBus.get();
        bus.on(this, "connectMouseMoved", this::connectMouseMoved);
        bus.on(this, "disconnectMouseMoved", this::disconnectMouseMoved);
        //this.vp = vp;
        this.upGoing = false;
        this.downGoing = false;
        this.leftGoing = false;
        this.rightGoing = false; 
    }

    public boolean isUpGoing() {
        return this.upGoing;
    }

    public boolean isDownGoing() {
        return this.downGoing;
    }

    public boolean isLeftGoing() {
        return this.leftGoing;
    }

    public boolean isRightGoing() {
        return this.rightGoing;
    }

    public void connectMouseMoved(Function consumer) {
        if (mouseMovedFunction != null) 
            throw new IllegalStateException("cant connect multiple mouse moved listeners at the same time"); // C'est la merde faudrait une erreur
        mouseMovedFunction = consumer.mouseMovedFunction;
    }

    public void disconnectMouseMoved(Function consumer) {
        mouseMovedFunction = null;
    }

    @Override 
    public boolean keyDown(int k) {
        switch (k) {
            case Input.Keys.Z:
            case Input.Keys.UP:
                upGoing = true;
                bus.emit("InputPressed", new DirectionalCommand(0, +1));
                break;

            case Input.Keys.S:
            case Input.Keys.DOWN:
                downGoing = true;
                bus.emit("InputPressed", new DirectionalCommand(0, -1));
                break;

            case Input.Keys.Q:
            case Input.Keys.LEFT:
                leftGoing = true;
                bus.emit("InputPressed", new DirectionalCommand(-1, 0));
                break;

            case Input.Keys.D:
            case Input.Keys.RIGHT:
                rightGoing = true;
                bus.emit("InputPressed", new DirectionalCommand(+1, 0));
                break;

            case Input.Keys.SPACE:
                bus.emit("InputPressed", new AimCommand());
                break;
            
            case Input.Keys.M:
                bus.emit("InputPressed", new ReadyCommand());
                break;
                
            default:
                bus.emit("InputPressed", new OtherKeyCommand(k));
        }
        return true;
    }

    @Override 
    public boolean keyUp(int k){
        switch (k) {
            case Input.Keys.Z:
            case Input.Keys.UP:
                upGoing = false;
                bus.emit("InputReleased", new DirectionalCommand(0, +1));
                break;

            case Input.Keys.S:
            case Input.Keys.DOWN:
                downGoing = false;
                bus.emit("InputReleased", new DirectionalCommand(0, -1));
                break;

            case Input.Keys.Q:
            case Input.Keys.LEFT:
                leftGoing = false;
                bus.emit("InputReleased", new DirectionalCommand(-1, 0));
                break;

            case Input.Keys.D:
            case Input.Keys.RIGHT:
                rightGoing = false;
                bus.emit("InputReleased", new DirectionalCommand(+1, 0));
                break;
        }
        return true;
    }

    @Override public boolean touchDown(int x, int y, int pointer, int button) {
        Vector3 worldCoords = new Vector3(x, y, 0);
        vp.getCamera().unproject(worldCoords);
        Vector2 v = new Vector2(MathUtils.floor(worldCoords.x), MathUtils.floor(worldCoords.y));

        bus.emit("InputPressed", new ClickTileCommand(v.x, v.y));
        return true;
    }

    @Override public boolean mouseMoved(int x,int y){
        if (mouseMovedFunction != null) {
            Vector3 worldCoords = new Vector3(x, y, 0);
            vp.getCamera().unproject(worldCoords);
            Vector2 v = new Vector2(MathUtils.floor(worldCoords.x), MathUtils.floor(worldCoords.y));

            mouseMovedFunction.accept(v);
            return true;
        }
        return false;
    }

    public void triggerMouseUpdate() {
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();
        mouseMoved(x, y);
    }

    @Override public boolean keyTyped(char c){return false;}
    @Override public boolean touchUp(int x,int y,int p,int b){return false;}
    @Override public boolean touchDragged(int x,int y,int p){return false;}
    @Override public boolean touchCancelled (int screenX, int screenY, int pointer, int button) {return false;}
    @Override public boolean scrolled(float x,float y){return false;}
}
