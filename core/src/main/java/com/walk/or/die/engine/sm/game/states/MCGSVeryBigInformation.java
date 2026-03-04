package com.walk.or.die.engine.sm.game.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.MCGame.CombatDoneArgs;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCExplorationPlayer;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.sm.game.MCGameState;
import com.walk.or.die.engine.ui.MCUILayout;
import com.walk.or.die.engine.ui.MCUISimpleText;
import com.walk.or.die.engine.ui.MCVeryBigInfoHUD;

/**
 * Represents a game state for displaying very big information, typically after combat.
 */
public class MCGSVeryBigInformation extends MCGameState<MCGSVeryBigInformation.MCGSVBIArgs> {
    /** Stores the arguments detailing the outcome of the combat. */
    private CombatDoneArgs combatDoneArgs;
    /** The HUD used to display very big information messages. */
    private MCVeryBigInfoHUD vbiHud = hudManager.getVbiHud();

    /** The time elapsed since entering this state. */
    private float stateTime = 0f;
    /** The duration for which the big information is displayed. */
    private final float TIME_SHOWN = 3f;
    /** The scale factor for large text. */
    private final float BIG_SCALE = 1.15f; // mon nom de boxeur
    /** The scale factor for small text. */
    private final float LIL_SCALE = 0.25f; // mon nom de rappeur

    /**
     * Represents the arguments passed to the {@link MCGSVeryBigInformation} state.
     */
    public static class MCGSVBIArgs extends MCGameState.StateArgs {
        /** The arguments detailing the outcome of the combat. */
        public CombatDoneArgs combatDoneArgs;
        
        /**
         * Initializes a new instance of the MCGSVBIArgs class with the specified combat done arguments.
         * @param cba The combat done arguments.
         */
        public MCGSVBIArgs(CombatDoneArgs cba) {
            combatDoneArgs = cba;
        }
    }

    /**
     * Initializes a new instance of the MCGSVeryBigInformation class with the parent game.
     * @param parent The parent game instance.
     */
    public MCGSVeryBigInformation(MCGame parent) {
        super(parent);
        this.name = "VeryBigInformation";
    }
        
    /**
     * Called at entrance. Enters the very big information state.
     * @param args The arguments for entering the state, containing combat done arguments.
     */
    @Override
    public void enter(MCGSVBIArgs args) {
        //System.out.println("entering vbi state");
        //bus.emit("freezeGame", parent);
        combatDoneArgs = args.combatDoneArgs;
        stateTime = 0f;
        if (combatDoneArgs == CombatDoneArgs.ALLIES_WON) {
            vbiHud.setUpperTextScale(BIG_SCALE);
            vbiHud.setUpperText("WALK");
            vbiHud.setLowerTextScale(LIL_SCALE);
            vbiHud.setLowerText("OR YOU'LL DIE");
        } else if (combatDoneArgs == CombatDoneArgs.ENEMIES_WON) {
            vbiHud.setUpperTextScale(LIL_SCALE);
            vbiHud.setUpperText("YOU'RE");
            vbiHud.setLowerTextScale(BIG_SCALE);
            vbiHud.setLowerText("DEAD");
        }
        vbiHud.setDisplay(true);
        hudManager.getSimpleHud().disable();
        super.enter(args);
    }

    /**
     * Called at exit. Exits the very big information state.
     */
    @Override
    public void exit() {
        vbiHud.setDisplay(false);
        //bus.emit("unfreezeGame", parent);
        super.exit();
    }

    /**
     * Called on each frame. Updates the state logic on each frame.
     * @param delta The time in seconds since the last frame.
     */
    @Override
    public void update(float delta) {
        stateTime += delta;
        if (stateTime >= TIME_SHOWN) {
            if (combatDoneArgs == MCGame.CombatDoneArgs.ALLIES_WON) {
                MCAlly chosen = MCEntityManager.get().getBestAlly();
                MCExplorationPlayer player = new MCExplorationPlayer(chosen);
                MCEntityManager.get().addEntity(player);
                MCEntityManager.get().kill(chosen);
                player.onSpawn();
                changeState("exploration", new MCGSExploration.ExplStateArgs());
            } else if (combatDoneArgs == MCGame.CombatDoneArgs.ENEMIES_WON) {
                parent.reloadMap();
            }
        }
    }

    /**
     * Called on each frame. Renders the state visuals on each frame.
     * @param batch The sprite batch used for rendering.
     */
    @Override
    public void render(SpriteBatch batch) {
    }
}