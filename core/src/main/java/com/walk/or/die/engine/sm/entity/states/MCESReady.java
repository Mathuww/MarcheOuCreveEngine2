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
import com.walk.or.die.engine.sm.MCState;
import com.walk.or.die.engine.sm.MCState.StateArgs;
import com.walk.or.die.engine.sm.entity.MCEntityState;
import com.walk.or.die.engine.tiledmap.MCPathfinder;

import java.util.ArrayList;
import java.util.List;


public class MCESReady extends MCEntityState<MCESReady.ReadyStateArgs> {
    private int tileX = -1;
    private int tileY = -1;

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
        this.bus.emit("connectMouseMoved", new MCInputManager.Function(this::mouseMoved));
        parent.getMoveDisplay().computeValidTilesDisplay();
        parent.getMoveDisplay().display = true;
    }

    @Override
    public void exit() {
        parent.getMoveDisplay().display = false;
        tileX = -1;
        tileY = -1;
        this.bus.emit("disconnectMouseMoved", null);
        super.exit();
    }
    
    @Override
    protected void inputPressed(MCInputManager.Command data) {
        //System.out.println("Input pressed detect in Idle");
        if (data instanceof MCInputManager.ClickTileCommand tileCmd) {
            Vector2 v = tileCmd.getVector();
            if (MCPathfinder.get().isWalkable(MathUtils.floor(v.x), MathUtils.floor(v.y))) {
                List<Vector2> path = MCPathfinder.get().getPath(parent.getPosition(), v);
                if (path.size() == 0 || path.size() > parent.getMaxMoves())
                    changeState("idle", new MCESIdle.IdleStateArgs());
                changeState("click_move", new MCESClickMove.MoveStateArgs(v, MCPathfinder.get().clean(path)));
                return;
            }
            changeState("idle", new MCESIdle.IdleStateArgs());
        }
        else if (!(data instanceof MCInputManager.DirectionalCommand bipboup)) {
            changeState("idle", new MCESIdle.IdleStateArgs());
        }
    }

    private void mouseMoved(Vector2 pos) {
        int newx = MathUtils.floor(pos.x);
        int newy = MathUtils.floor(pos.y);
        if (newx != tileX || newy != tileY) {
            tileX = newx;
            tileY = newy;
            
            if (!MCPathfinder.get().isWalkable(MathUtils.floor(tileX), MathUtils.floor(tileY))) {
                parent.getMoveDisplay().clearTrajectory();
                return;
            }

            int startx = MathUtils.floor(parent.getX());
            int starty = MathUtils.floor(parent.getY());

            List<Vector2> traj = MCPathfinder.get().getPath(new Vector2(startx, starty), new Vector2(tileX, tileY));
            if (traj.size() == 0 || traj.size() > parent.getMaxMoves()) {
                parent.getMoveDisplay().clearTrajectory();
                return;
            }


            System.out.println("switching trajectory");
            parent.getMoveDisplay().showTrajectory(traj);
        }
    }

}