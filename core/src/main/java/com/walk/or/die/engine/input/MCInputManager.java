package com.walk.or.die.engine.input;

import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
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

    public static class PreviousMapCommand extends Command {}
    public static class NextMapCommand extends Command {}

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

    public static class CameraZoomCommand extends Command {
        public float scrollDelta;

        public CameraZoomCommand(float scroll) {
            scrollDelta = scroll;
        }
    }

    public static class CameraPanCommand extends Command {
        public float deltaX;
        public float deltaY;

        public CameraPanCommand(float dx, float dy) {
            deltaX = dx;
            deltaY = dy;
        }
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

    private final MCEventBus bus = MCEventBus.get();
    private final MCHUDManager hudManager = MCHUDManager.get();
    private Consumer<Vector2> mouseMovedFunction;

    private Vector3 posBeforeDrag = new Vector3(0f,0f,0f);
    private boolean isDragging = false;

    private MCInputManager() {
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
                if (hudManager.getCharacterHud().isFullyShown())
                    bus.emit("InputPressed", new HudCommand(Type.UP));
                else
                    bus.emit("InputPressed", new DirectionalCommand(0, +1));
                break;

            case Input.Keys.DOWN:
                if (hudManager.getCharacterHud().isFullyShown())
                    bus.emit("InputPressed", new HudCommand(Type.DOWN));
                else
                    bus.emit("InputPressed", new DirectionalCommand(0, -1));
                break;

            case Input.Keys.LEFT:
                if (hudManager.getCharacterHud().isFullyShown())
                    bus.emit("InputPressed", new HudCommand(Type.LEFT));
                else
                    bus.emit("InputPressed", new DirectionalCommand(-1, 0));
                break;

            case Input.Keys.RIGHT:
                if (hudManager.getCharacterHud().isFullyShown())
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
                if (hudManager.getCharacterHud().isFullyShown())
                    bus.emit("InputPressed", new HudCommand(Type.VALIDATE));
                break;

            case Input.Keys.P:
                bus.emit("InputPressed", new NextMapCommand());
                break;
            
            case Input.Keys.O:
                bus.emit("InputPressed", new PreviousMapCommand());
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
        Vector3 hudCoords = new Vector3(worldCoords);
        hudManager.getViewport().unproject(hudCoords);
        Vector2 hudPos = new Vector2(hudCoords.x, hudCoords.y);

        if (button == Buttons.LEFT) {
            if (hudManager.posBelongsToHud(hudPos)) {
                System.out.println("sending click to hud");
                hudManager.handleClick(hudPos);
                return true;
            }
            vp.unproject(worldCoords);
            MCIntVector2 v = new MCIntVector2(worldCoords.x, worldCoords.y);
            bus.emit("InputPressed", new ClickTileCommand(v));
            return true;
        } else if (button == Buttons.RIGHT) {
            if (hudManager.posBelongsToHud(hudPos))
                return false;
            isDragging = true;
            vp.unproject(worldCoords);
            posBeforeDrag.set(worldCoords);
            return true;
        }

        return false;
    }

    @Override public boolean mouseMoved(int x, int y) {
        Vector3 hudPos = new Vector3(x, y, 0f);
        hudManager.getViewport().unproject(hudPos);
        Vector2 hudCoords = new Vector2(hudPos.x, hudPos.y);

        if (hudManager.posBelongsToHud(hudCoords)) {
            hudManager.handleHover(hudCoords);
            return true;
        } else 
            hudManager.handleHoverGone();

        if (mouseMovedFunction != null) {
            Vector3 worldPos = new Vector3(x, y, 0);
            vp.unproject(worldPos);
            Vector2 worldCoords = new Vector2(
                MathUtils.floor(worldPos.x),
                MathUtils.floor(worldPos.y)
            );
            mouseMovedFunction.accept(worldCoords);
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

    public Vector3 askWorldMousePos() {
        Vector3 worldCoords = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        System.out.println(vp.getScreenWidth() + vp.getScreenHeight());
        vp.unproject(worldCoords);
        return worldCoords;
    }

    @Override 
    public boolean scrolled(float x, float y) {
        Vector3 hudPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f);
        hudManager.getViewport().unproject(hudPos);
        Vector2 hudCoords = new Vector2(hudPos.x, hudPos.y);

        if (hudManager.posBelongsToHud(hudCoords)) {
            hudManager.handleScroll(hudCoords, y);
            return true;
        }

        bus.emit("InputPressed", new CameraZoomCommand(y));
        return true;
    }

    @Override
    public boolean touchUp(int x, int y, int p, int button) {
        if (button == Buttons.RIGHT) {
            isDragging = false;
            return true;
        }
        return false;
    }

    @Override 
    public boolean touchDragged(int x, int y, int p) {
        if (!isDragging)
            return false;

        // si bouton relache hors de la fenetre du jeu
        if (!Gdx.input.isButtonPressed(Buttons.RIGHT)) {
            isDragging = false;
            return false;
        }
        
        Vector3 hudPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0f);
        hudManager.getViewport().unproject(hudPos);
        Vector2 hudCoords = new Vector2(hudPos.x, hudPos.y);

        Vector3 delta = new Vector3(x, y, 0);
        vp.unproject(delta);
        delta.sub(posBeforeDrag); // currPosition est mtn delta !

        bus.emit("InputPressed", new CameraPanCommand(delta.x, delta.y));
        return true;
    }

    @Override public boolean keyTyped(char c){return false;}
    @Override public boolean touchCancelled (int screenX, int screenY, int pointer, int button) {return false;}
}
