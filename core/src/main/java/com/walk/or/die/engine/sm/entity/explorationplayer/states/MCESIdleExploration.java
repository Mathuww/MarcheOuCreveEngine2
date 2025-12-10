package com.walk.or.die.engine.sm.entity.explorationplayer.states;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.entities.MCExplorationPlayer;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.sm.entity.character.states.MCESAim;
import com.walk.or.die.engine.sm.entity.character.states.MCESReady;
import com.walk.or.die.engine.sm.entity.explorationplayer.MCExplorationPlayerState;
import com.walk.or.die.engine.tiledmap.MCPathfinder;

public class MCESIdleExploration extends MCExplorationPlayerState<MCESIdleExploration.IdleStateArgs> {

    public static class IdleStateArgs extends MCExplorationPlayerState.StateArgs {}

    public MCESIdleExploration(MCExplorationPlayer parent) {
        super(parent);
        this.name = "idle";
    }

    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    @Override
    public void enter(MCESIdleExploration.IdleStateArgs args) {
        parent.keep = false;
        parent.playAnimation("idle");
        super.enter(args);
    }

    @Override
    public void exit() {
        parent.keep = true;
        super.exit();
    }
    
    @Override
    protected void inputPressed(MCInputManager.Command data) {
        if (!parent.focus)
            return;
        if (!parent.getParent().getStateManager().isIn("MCGSExploration"))
            return;
        if (MCEntityManager.get().isAnyoneBusy())
            return;
        super.inputPressed(data);
    }

    /*
    public void play(MCIntVector2 pos) {
        changeState("click_move", new MCESClickMove.MoveStateArgs(pos, MCPathfinder.get().getPath(parent.getTilePosition(), pos)));
    }
    */
}
