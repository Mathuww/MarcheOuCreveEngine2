package com.walk.or.die.engine.sm.entity.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.sm.MCState;
import com.walk.or.die.engine.sm.MCState.StateArgs;
import com.walk.or.die.engine.sm.entity.MCEntityState;
import com.walk.or.die.engine.tiledmap.MCPathfinder;

import java.util.ArrayList;
import java.util.List;


public class MCESReady extends MCEntityState<MCESReady.ReadyStateArgs> {
    private MCIntVector2 tile = new MCIntVector2(-1, -1);

    public static class ReadyStateArgs extends MCEntityState.StateArgs {}

    public MCESReady (MCCharacter parent) {
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

    @Override
    public void renderOnGridOverlay(SpriteBatch batch) {
        parent.getMoveDisplay().render(batch);
    }

    @Override
    public void enter(ReadyStateArgs args) {
        super.enter(args);
        parent.playAnimation("ready");
        this.bus.emit("connectMouseMoved", new MCInputManager.MouseListener(this::mouseMoved));
        parent.getMoveDisplay().computeValidTilesDisplay();
        parent.getMoveDisplay().display = true;
        MCInputManager.get().triggerMouseUpdate();
    }

    @Override
    public void exit() {
        parent.getMoveDisplay().display = false;
        tile = new MCIntVector2(-1, -1);
        this.bus.emit("disconnectMouseMoved", null);
        super.exit();
    }
    
    @Override
    protected void inputPressed(MCInputManager.Command data) {
        //System.out.println("Input pressed detect in Idle");
        if (data instanceof MCInputManager.ClickTileCommand tileCmd) {
            MCIntVector2 v = tileCmd.getIntVect();
            if (MCPathfinder.get().isWalkable(v)) {
                List<MCIntVector2> path = MCPathfinder.get().getPath(parent.getTilePosition(), v);
                if (path.size() == 0 || path.size() > parent.getMaxMoves())
                    changeState("idle", new MCESIdle.IdleStateArgs());
                changeState("click_move", new MCESClickMove.MoveStateArgs(v, MCPathfinder.get().clean(path)));
                return;
            }
            changeState("idle", new MCESIdle.IdleStateArgs());
        } else if (!(data instanceof MCInputManager.DirectionalCommand bipboup)) {
            changeState("idle", new MCESIdle.IdleStateArgs());
        }
    }

    private void mouseMoved(Vector2 pos) {
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