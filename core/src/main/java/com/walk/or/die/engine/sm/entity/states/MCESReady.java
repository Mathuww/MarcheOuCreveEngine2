package com.walk.or.die.engine.sm.entity.states;

import com.badlogic.gdx.Input;
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
    public void enter(ReadyStateArgs args) {
        super.enter(args);
    }

    @Override
    public void exit() {
        super.exit();
    }
    
    @Override
    protected void inputPressed(MCInputManager.Command data) {
        //System.out.println("Input pressed detect in Idle");
        if (data instanceof MCInputManager.ClickTileCommand tileCmd) {
            Vector2 v = tileCmd.getVector();
            if (MCPathfinder.get().isWalkable(MathUtils.floor(v.x), MathUtils.floor(v.y))) {
                List<Vector2> path = MCPathfinder.get().getPath(parent.getPosition(), v);
                if (path.size() == 0)
                    changeState("idle", new MCESIdle.IdleStateArgs());
                changeState("click_move", new MCESClickMove.MoveStateArgs(v, path));
                return;
            }
            changeState("idle", new MCESIdle.IdleStateArgs());
        }
        else if (!(data instanceof MCInputManager.DirectionalCommand bipboup)) {
            changeState("idle", new MCESIdle.IdleStateArgs());
        }
    }

}