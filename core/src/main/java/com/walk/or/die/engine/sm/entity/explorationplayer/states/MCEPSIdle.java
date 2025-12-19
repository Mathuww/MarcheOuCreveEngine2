package com.walk.or.die.engine.sm.entity.explorationplayer.states;

import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.entities.MCExplorationPlayer;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.sm.entity.explorationplayer.MCExplorationPlayerState;


/**
 * The idle state for explorer player.<br>
 * Name = "idle"<br>
 * This is a non-blocking state.
 */
public class MCEPSIdle extends MCExplorationPlayerState<MCEPSIdle.IdleStateArgs> {

    /**
     * Class which represents args needed by idle move to start.
     */
    public static class IdleStateArgs extends MCExplorationPlayerState.StateArgs {}

    /**
     * The constructor.
     * @param parent
     */
    public MCEPSIdle(MCExplorationPlayer parent) {
        super(parent);
        this.name = "idle";
    }

    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    @Override
    public void enter(MCEPSIdle.IdleStateArgs args) {
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
        if (!parent.getParent().getStateManager().isIn("Exploration"))
            return;
        if (MCEntityManager.get().isAnyoneBusy())
            return;
        if (data instanceof MCInputManager.DirectionalCommand deplacement) {
            changeState("move", new MCEPSMove.MoveStateArgs(deplacement));
        }
    }

    /*
    public void play(MCIntVector2 pos) {
        changeState("click_move", new MCESClickMove.MoveStateArgs(pos, MCPathfinder.get().getPath(parent.getTilePosition(), pos)));
    }
    */
}
