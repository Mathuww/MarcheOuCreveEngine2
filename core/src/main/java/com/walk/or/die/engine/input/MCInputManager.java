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
    /**
     * The singleton instance of the MCInputManager.
     */
    private static MCInputManager instance = null;

    /**
     * Gets the singleton instance.
     * @return The singleton instance.
     */
    public static MCInputManager get() {
        if (instance == null) instance = new MCInputManager();
        return instance;
    }

    /**
     * Initializes the singleton.
     * @param v The viewport to use.
     */
    public void init(Viewport v) {
        vp = v;
    }

    /**
     * Represents a base class for commands, designed to be extended.
     */
    public static abstract class Command {}

    /**
     * Represents a directional input command.
     */
    public static class DirectionalCommand extends Command {
        /**
         * The directional vector.
         */
        private MCIntVector2 v;

        /**
         * Constructs a DirectionalCommand with a given vector.
         * @param v The MCIntVector2 representing the direction.
         */
        public DirectionalCommand(MCIntVector2 v) {
            this.v = v;
        }

        /**
         * Constructs a DirectionalCommand with given integer coordinates.
         * @param x The x-coordinate of the direction.
         * @param y The y-coordinate of the direction.
         */
        public DirectionalCommand(int x, int y) {
            this.v = new MCIntVector2(x, y);
        }
        
        /**
         * Retrieves the direction vector of the command.
         * @return The direction vector.
         */
        public MCIntVector2 getIntVect() {
            return this.v;
        }
    }

    /**
     * Represents a command for a tile click.
     */
    public static class ClickTileCommand extends Command {
        /**
         * The position of the clicked tile.
         */
        private MCIntVector2 v;

        /**
         * Constructs a ClickTileCommand with a given tile position.
         * @param v The MCIntVector2 representing the tile position.
         */
        public ClickTileCommand(MCIntVector2 v) {
            this.v = v;
        }

        /**
         * Retrieves the tile position.
         * @return The tile position.
         */
        public MCIntVector2 getIntVect() {
            return this.v;
        }
    }

    /**
     * Represents an aim input command.
     */
    public static class AimCommand extends Command {
        /**
         * Constructs an AimCommand.
         */
        public AimCommand() {}
    }

    /**
     * Represents a ready input command.
     */
    public static class ReadyCommand extends Command {
        /**
         * Constructs a ReadyCommand.
         */
        public ReadyCommand() {}    
    }

    /**
     * Represents a command related to HUD interactions.
     */
    public static class HudCommand extends Command {
        /**
         * Defines the types of HUD commands.
         */
        public static enum Type {
            UP,
            DOWN,
            LEFT,
            RIGHT,
            VALIDATE
        }

        /**
         * The type of the HUD command.
         */
        public Type type;

        /**
         * Constructs a HudCommand with the specified type.
         * @param type The type of the HUD command.
         */
        public HudCommand(Type type) {
            this.type = type;
        }
    }

    /**
     * Represents a command to navigate to the previous map.
     */
    public static class PreviousMapCommand extends Command {}
    /**
     * Represents a command to navigate to the next map.
     */
    public static class NextMapCommand extends Command {}
    /**
     * Represents a command to pause the game.
     */
    public static class PauseCommand extends Command {}

    /**
     * Represents a command for a non-handled key input.
     */
    public static class OtherKeyCommand extends Command {
        /**
         * The key code of the unhandled input.
         */
        public int key;

        /**
         * Constructs an OtherKeyCommand with the specified key code.
         * @param key The key code of the input.
         */
        public OtherKeyCommand(int key) {
            this.key = key;
        }
    }

    /**
     * Represents a command to advance to the next turn.
     */
    public static class NextTurnCommand extends Command {
        /**
         * Constructs a NextTurnCommand.
         */
        public NextTurnCommand() {}
    }

    /**
     * Represents a command to zoom the camera.
     */
    public static class CameraZoomCommand extends Command {
        /**
         * The scroll delta value.
         */
        public float scrollDelta;

        /**
         * Constructs a CameraZoomCommand with the specified scroll delta.
         * @param scroll The scroll delta value.
         */
        public CameraZoomCommand(float scroll) {
            scrollDelta = scroll;
        }
    }

    /**
     * Represents a command to pan the camera.
     */
    public static class CameraPanCommand extends Command {
        /**
         * The delta X value for panning.
         */
        public float deltaX;
        /**
         * The delta Y value for panning.
         */
        public float deltaY;

        /**
         * Constructs a CameraPanCommand with the specified delta values.
         * @param dx The delta X value for panning.
         * @param dy The delta Y value for panning.
         */
        public CameraPanCommand(float dx, float dy) {
            deltaX = dx;
            deltaY = dy;
        }
    }
    
    /**
     * Stores functions to call when the mouse moves.
     */
    public static class MouseListener {
        /**
         * The function to be called when the mouse moves.
         */
        public Consumer<Vector2> mouseMovedFunction;

        /**
         * Constructs a MouseListener with the specified consumer.
         * @param consumer The consumer function to call when the mouse moves.
         */
        public MouseListener(Consumer<Vector2> consumer) {
            this.mouseMovedFunction = consumer;
        }
    }

    /**
     * The viewport used for coordinate transformations.
     */
    private Viewport vp;

    /**
     * The event bus for broadcasting input commands.
     */
    private final MCEventBus bus = MCEventBus.get();
    /**
     * The HUD manager for handling HUD-specific inputs.
     */
    private final MCHUDManager hudManager = MCHUDManager.get();
    /**
     * The function to be called when the mouse moves, if connected.
     */
    private Consumer<Vector2> mouseMovedFunction;

    /**
     * The world coordinates of the mouse position before a drag operation.
     */
    private Vector3 posBeforeDrag = new Vector3(0f,0f,0f);
    /**
     * Indicates whether a mouse drag operation is currently active.
     */
    private boolean isDragging = false;

    /**
     * Constructs the MCInputManager singleton instance.
     */
    private MCInputManager() {
        bus.on(this, "connectMouseMoved", this::connectMouseMoved);
        bus.on(this, "disconnectMouseMoved", this::disconnectMouseMoved);
    }

    /**
     * Connects a function to be called when the mouse moves.
     * @param consumer The mouse listener containing the function.
     */
    public void connectMouseMoved(MouseListener consumer) {
        if (mouseMovedFunction != null) 
            throw new IllegalStateException("cant connect multiple mouse moved listeners at the same time"); // C'est la merde faudrait une erreur
        mouseMovedFunction = consumer.mouseMovedFunction;
    }

    /**
     * Disconnects the currently connected mouse moved function.
     * @param consumer The mouse listener.
     */
    public void disconnectMouseMoved(MouseListener consumer) {
        mouseMovedFunction = null;
    }

    /**
     * Called when a key is pressed down. Processes the key event and emits corresponding commands.
     * @param k The key code of the pressed key.
     * @return True if the event was handled, false otherwise.
     */
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
                if (hudManager.canReceiveHudCommand())
                    bus.emit("InputPressed", new HudCommand(Type.UP));
                else
                    bus.emit("InputPressed", new DirectionalCommand(0, +1));
                break;

            case Input.Keys.DOWN:
                if (hudManager.canReceiveHudCommand())
                    bus.emit("InputPressed", new HudCommand(Type.DOWN));
                else
                    bus.emit("InputPressed", new DirectionalCommand(0, -1));
                break;

            case Input.Keys.LEFT:
                if (hudManager.canReceiveHudCommand())
                    bus.emit("InputPressed", new HudCommand(Type.LEFT));
                else
                    bus.emit("InputPressed", new DirectionalCommand(-1, 0));
                break;

            case Input.Keys.RIGHT:
                if (hudManager.canReceiveHudCommand())
                    bus.emit("InputPressed", new HudCommand(Type.RIGHT));
                else
                    bus.emit("InputPressed", new DirectionalCommand(+1, 0));
                break;

            case Input.Keys.ENTER:
                if (hudManager.canReceiveHudCommand())
                    bus.emit("InputPressed", new HudCommand(Type.VALIDATE));
                break;

            case Input.Keys.ESCAPE:
                bus.emit("InputPressed", new PauseCommand());
                break;

            default:
                bus.emit("InputPressed", new OtherKeyCommand(k));
        }
        return true;
    }

    /**
     * Called when a key is released. Processes the key release event and emits corresponding commands.
     * @param k The key code of the released key.
     * @return True if the event was handled, false otherwise.
     */
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

    /**
     * Called when a touch screen is touched or a mouse button is pressed. Handles clicks and initiates dragging.
     * @param x The x-coordinate of the touch/click.
     * @param y The y-coordinate of the touch/click.
     * @param pointer The pointer for the event.
     * @param button The button that was pressed (for mouse events).
     * @return True if the event was handled, false otherwise.
     */
    @Override public boolean touchDown(int x, int y, int pointer, int button) {
        Vector3 worldCoords = new Vector3(x, y, 0);
        Vector3 hudCoords = new Vector3(worldCoords);
        hudManager.getViewport().unproject(hudCoords);
        Vector2 hudPos = new Vector2(hudCoords.x, hudCoords.y);

        if (button == Buttons.LEFT) {
            if (hudManager.posBelongsToHud(hudPos)) {
                //System.out.println("sending click to hud");
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

    /**
     * Called when the mouse is moved. Handles HUD hovering and relays mouse movement to connected listeners.
     * @param x The x-coordinate of the mouse cursor.
     * @param y The y-coordinate of the mouse cursor.
     * @return True if the event was handled, false otherwise.
     */
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
     * Triggers a manual mouse update, simulating a mouse movement event.
     */
    public void triggerMouseUpdate() {
        int x = Gdx.input.getX();
        int y = Gdx.input.getY();
        mouseMoved(x, y);
    }

    /**
     * Retrieves the current world coordinates of the mouse position.
     * @return The world mouse position.
     */
    public Vector3 askWorldMousePos() {
        Vector3 worldCoords = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        //System.out.println(vp.getScreenWidth() + vp.getScreenHeight());
        vp.unproject(worldCoords);
        return worldCoords;
    }

    /**
     * Called when the mouse wheel is scrolled. Handles HUD scrolling and camera zooming.
     * @param x The horizontal scroll amount (not typically used for vertical scrolling).
     * @param y The vertical scroll amount.
     * @return True if the event was handled, false otherwise.
     */
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

    /**
     * Called when a touch screen is no longer touched or a mouse button is released. Ends drag operations.
     * @param x The x-coordinate of the touch/release.
     * @param y The y-coordinate of the touch/release.
     * @param p The pointer for the event.
     * @param button The button that was released (for mouse events).
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean touchUp(int x, int y, int p, int button) {
        if (button == Buttons.RIGHT) {
            isDragging = false;
            return true;
        }
        return false;
    }

    /**
     * Called when a touch screen is touched and then dragged, or when a mouse button is pressed and the mouse is moved. Handles camera panning.
     * @param x The x-coordinate of the drag event.
     * @param y The y-coordinate of the drag event.
     * @param p The pointer for the event.
     * @return True if the event was handled, false otherwise.
     */
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

    /**
     * Called when a character is typed. Currently not handled.
     * @param c The character that was typed.
     * @return Always returns false as the event is not handled.
     */
    @Override public boolean keyTyped(char c){return false;}

    /**
     * Called when a touch event is cancelled. Currently not handled.
     * @param screenX The x-coordinate of the cancelled touch.
     * @param screenY The y-coordinate of the cancelled touch.
     * @param pointer The pointer for the event.
     * @param button The button associated with the cancelled touch.
     * @return Always returns false as the event is not handled.
     */
    @Override public boolean touchCancelled (int screenX, int screenY, int pointer, int button) {return false;}
}