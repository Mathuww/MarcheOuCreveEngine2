package com.walk.or.die.engine.sm.entity.character.states;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.sm.entity.character.MCCharacterState;
import com.walk.or.die.engine.tiledmap.MCPathfinder;

/**
 * The state to symbolize when the player chooses a tile to move on.<br>
 * Name = "ready"
 */
public class MCCSReady extends MCCharacterState<MCCSReady.ReadyStateArgs> {
    private MCIntVector2 tile = new MCIntVector2(-1, -1);

    /**
     * Class which represents arguments needed by the ready state to start.
     */
    public static class ReadyStateArgs extends MCCharacterState.StateArgs {}

    /**
     * The constructor.
     * @param parent The parent character.
     */
    public MCCSReady(MCCharacter parent) {
        super(parent);
        this.name = "ready";
    }

    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    @Override
    public void render(SpriteBatch batch) {
        
    }

    /**
     * Renders the effects.
     * @param batch The sprite batch.
     */
    @Override
    public void renderEffects(SpriteBatch batch) {
        parent.getMoveDisplay().render(batch);
    }

    /**
     * Called at entrance.
     * @param args The state arguments.
     */
    @Override
    public void enter(ReadyStateArgs args) {
        super.enter(args);
        parent.getHudCustomization().canShow = false;
        parent.notifyHudUpdate(true);
        parent.playAnimation("ready");
        this.bus.emit("connectMouseMoved", new MCInputManager.MouseListener(this::mouseMoved));
        parent.getMoveDisplay().computeValidTilesDisplay();
        parent.getMoveDisplay().display = true;
        MCInputManager.get().triggerMouseUpdate();
    }

    /**
     * Called at exit.
     */
    @Override
    public void exit() {
        parent.getMoveDisplay().display = false;
        tile = new MCIntVector2(-1, -1);
        this.bus.emit("disconnectMouseMoved", null);
        super.exit();
    }

    /**
     * Cancels the state.
     */
    private void cancel() {
        changeState("idle", new MCCSIdle.IdleStateArgs());
    }

    /**
     * Called when an input is pressed.
     * @param data The input data.
     */
    @Override
    protected void inputPressed(MCInputManager.Command data) {
        //System.out.println("Input pressed detect in Idle");
        if (data instanceof MCInputManager.ClickTileCommand tileCmd) {
            MCIntVector2 v = tileCmd.getIntVect();
            if (MCPathfinder.get().isWalkable(v)) {
                List<MCIntVector2> path = MCPathfinder.get().getPath(parent.getTilePosition(), v);
                if (path.size() == 0 || path.size() > parent.getMaxMoves()) {
                    cancel();
                    return;
                }
                changeState("click_move", new MCCSClickMove.MoveStateArgs(v, MCPathfinder.get().clean(path)));
                return;
            }
            cancel();
        } else if (!(data instanceof MCInputManager.DirectionalCommand) 
            && !(data instanceof MCInputManager.CameraPanCommand)
            && !(data instanceof MCInputManager.CameraZoomCommand)
        ) {
            cancel();
        }
    }

    /**
     * Called when the mouse is moved.
     * @param pos The position of the mouse.
     */
    private void mouseMoved(Vector2 pos) {
        /**
         * Gets the current position and verifies if it's walkable
         * @param pos the current Vector2 position of the mouse.
         */
        MCIntVector2 newPos = new MCIntVector2(pos);
        if (!newPos.equals(tile)) {
            tile = newPos;
            
            if (!MCPathfinder.get().isWalkable(tile)) {
                parent.getMoveDisplay().clearTrajectory();
                return;
            }

            MCIntVector2 start = parent.getTilePosition();

            List<MCIntVector2> traj = MCPathfinder.get().getPath(start, tile);
            if (traj.size() == 0 || traj.size() > parent.getMaxMoves()) {
                parent.getMoveDisplay().clearTrajectory();
                return;
            }

            parent.getMoveDisplay().showTrajectory(traj);
        }
    }

}