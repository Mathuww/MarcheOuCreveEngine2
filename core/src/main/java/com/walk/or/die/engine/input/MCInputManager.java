package com.walk.or.die.engine.input;

import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.walk.or.die.engine.input.MCInputManager.HudCommand.Type;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.ui.MCHUDManager;

/**
 * Our singleton which manages inputs.
 */
public class MCInputManager implements InputProcessor {
    private static MCInputManager instance = null;

    /**
     * The getter.
     * @return
     */
    public static MCInputManager get() {
        if (instance == null) instance = new MCInputManager();
        return instance;
    }

    /**
     * Init the singleton.
     * @param v
     */
    public void init(Viewport v) {
        vp = v;
    }

    /**
     * Command class, destinate to be extend.
     */
    public static abstract class Command {}

    /**
     * Directional input.
     */
    public static class DirectionalCommand extends Command {
        private MCIntVector2 v;

        /**
         * The creator, by a (MC)vector.
         * @param v
         */
        public DirectionalCommand(MCIntVector2 v) {
            this.v = v;
        }

        /**
         * The creator, by int.
         * @param x
         * @param y
         */
        public DirectionalCommand(int x, int y) {
            this.v = new MCIntVector2(x, y);
        }
        
        /**
         * Get the direction pressed.
         * @return
         */
        public MCIntVector2 getIntVect() {
            return this.v;
        }
    }

    /**
     * Tile clicked.
     */
    public static class ClickTileCommand extends Command {
        private MCIntVector2 v;

        /**
         * The creator.
         * @param v
         */
        public ClickTileCommand(MCIntVector2 v) {
            this.v = v;
        }

        /**
         * Return the tile position.
         * @return
         */
        public MCIntVector2 getIntVect() {
            return this.v;
        }
    }

    /**
     * Aim input.
     */
    public static class AimCommand extends Command {
        public AimCommand() {}
    }

    /**
     * Ready input.
     */
    public static class ReadyCommand extends Command {
        public ReadyCommand() {}    
    }

    public static class HudCommand extends Command {
        public static enum Type {
            UP,
            DOWN,
            LEFT,
            RIGHT,
            VALIDATE
        }

        public Type type;

        public HudCommand(Type type) {
            this.type = type;
        }
    }

    /**
     * Non-handled input.
     */
    public static class OtherKeyCommand extends Command {
        public int key;

        /**
         * The creator.
         * @param key
         */
        public OtherKeyCommand(int key) {
            this.key = key;
        }
    }

    /**
     * Next-turn input.
     */
    public static class NextTurnCommand extends Command {
        public NextTurnCommand() {}
    }
    
    /**
     * A class to store functions to call when the mouse move.
     */
    public static class MouseListener {
        public Consumer<Vector2> mouseMovedFunction;

        /**
         * The constructor.
         * @param consumer
         */
        public MouseListener(Consumer<Vector2> consumer) {
            this.mouseMovedFunction = consumer;
        }
    }

    private Viewport vp;

    private MCEventBus bus;
    private Consumer<Vector2> mouseMovedFunction;

    private MCInputManager() {
        this.bus = MCEventBus.get();
        bus.on(this, "connectMouseMoved", this::connectMouseMoved);
        bus.on(this, "disconnectMouseMoved", this::disconnectMouseMoved);
    }

    /**
     * Call the given function when the mouse move.
     * @param consumer
     */
    public void connectMouseMoved(MouseListener consumer) {
        if (mouseMovedFunction != null) 
            throw new IllegalStateException("cant connect multiple mouse moved listeners at the same time"); // C'est la merde faudrait une erreur
        mouseMovedFunction = consumer.mouseMovedFunction;
    }

    /**
     * Stop to call the function
     * @param consumer
     */
    public void disconnectMouseMoved(MouseListener consumer) {
        mouseMovedFunction = null;
    }

    @Override 
    public boolean keyDown(int k) {
        switch (k) {
            case Input.Keys.W:
                bus.emit("InputPressed", new DirectionalCommand(0, +1));
                break;

            case Input.Keys.S:
                bus.emit("InputPressed", new DirectionalCommand(0, -1));
                break;

            case Input.Keys.A:
                bus.emit("InputPressed", new DirectionalCommand(-1, 0));
                break;

            case Input.Keys.D:
                bus.emit("InputPressed", new DirectionalCommand(+1, 0));
                break;

            case Input.Keys.UP:
                if (MCHUDManager.get().isHudShown())
                    bus.emit("InputPressed", new HudCommand(Type.UP));
                else
                    bus.emit("InputPressed", new DirectionalCommand(0, +1));
                break;

            case Input.Keys.DOWN:
                if (MCHUDManager.get().isHudShown())
                    bus.emit("InputPressed", new HudCommand(Type.DOWN));
                else
                    bus.emit("InputPressed", new DirectionalCommand(0, -1));
                break;

            case Input.Keys.LEFT:
                if (MCHUDManager.get().isHudShown())
                    bus.emit("InputPressed", new HudCommand(Type.LEFT));
                else
                    bus.emit("InputPressed", new DirectionalCommand(-1, 0));
                break;

            case Input.Keys.RIGHT:
                if (MCHUDManager.get().isHudShown())
                    bus.emit("InputPressed", new HudCommand(Type.RIGHT));
                else
                    bus.emit("InputPressed", new DirectionalCommand(+1, 0));
                break;

            case Input.Keys.Q:
                //bus.emit("InputPressed", new AimCommand());
                break;
            
            case Input.Keys.SEMICOLON:
                //bus.emit("InputPressed", new ReadyCommand());
                break;

            case Input.Keys.SPACE:
                bus.emit("InputPressed", new NextTurnCommand());
                break;

            case Input.Keys.ENTER:
                if (MCHUDManager.get().isHudShown())
                    bus.emit("InputPressed", new HudCommand(Type.VALIDATE));
                break;

            default:
                bus.emit("InputPressed", new OtherKeyCommand(k));
        }
        return true;
    }

    @Override 
    public boolean keyUp(int k){
        switch (k) {
            case Input.Keys.W:
            case Input.Keys.UP:
                bus.emit("InputReleased", new DirectionalCommand(0, +1));
                break;

            case Input.Keys.S:
            case Input.Keys.DOWN:
                bus.emit("InputReleased", new DirectionalCommand(0, -1));
                break;

            case Input.Keys.A:
            case Input.Keys.LEFT:
                bus.emit("InputReleased", new DirectionalCommand(-1, 0));
                break;

            case Input.Keys.D:
            case Input.Keys.RIGHT:
                bus.emit("InputReleased", new DirectionalCommand(+1, 0));
                break;
        }
        return true;
    }

    @Override public boolean touchDown(int x, int y, int pointer, int button) {
        Vector3 worldCoords = new Vector3(x, y, 0);
        System.out.println(vp.getScreenWidth() + vp.getScreenHeight());
        vp.unproject(worldCoords);
        MCIntVector2 v = new MCIntVector2(worldCoords.x, worldCoords.y);

        bus.emit("InputPressed", new ClickTileCommand(v));
        return true;
    }

    @Override public boolean mouseMoved(int x,int y){
        if (mouseMovedFunction != null) {
            Vector3 worldCoords = new Vector3(x, y, 0);
            vp.unproject(worldCoords);
            Vector2 v = new Vector2(
                MathUtils.floor(worldCoords.x), 
                MathUtils.floor(worldCoords.y)
            );

            mouseMovedFunction.accept(v);
            return true;
        }
        return false;
    }

    /**
     * To force a mouse update.
     */
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
