package com.walk.or.die.engine.sm.entity.explorationplayer.states;

import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.entities.MCExplorationPlayer;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.sm.entity.explorationplayer.MCExplorationPlayerState;


/**
 * The idle state for the exploration player.<br>
 * The name of this state is "idle".<br>
 * This state is non-blocking.
 */
public class MCEPSIdle extends MCExplorationPlayerState<MCEPSIdle.IdleStateArgs> {

    /**
     * Represents the arguments needed for an idle move to start.
     */
    public static class IdleStateArgs extends MCExplorationPlayerState.StateArgs {}

    /**
     * Constructs a new `MCEPSIdle` state.
     * @param parent The exploration player parent.
     */
    public MCEPSIdle(MCExplorationPlayer parent) {
        super(parent);
        this.name = "idle";
    }

    /**
     * Called on each frame.
     * @param delta The time elapsed since the last frame.
     */
    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    /**
     * Called at entrance.
     * @param args The arguments for entering the idle state.
     */
    @Override
    public void enter(MCEPSIdle.IdleStateArgs args) {
        parent.keep = false;
        parent.playAnimation("idle");
        super.enter(args);
    }

    /**
     * Called at exit.
     */
    @Override
    public void exit() {
        parent.keep = true;
        super.exit();
    }
    
    /**
     * Called when an input is pressed.
     * @param data The input command data.
     */
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