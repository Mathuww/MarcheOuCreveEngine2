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

public class MCGSVeryBigInformation extends MCGameState<MCGSVeryBigInformation.MCGSVBIArgs> {
    private CombatDoneArgs combatDoneArgs;
    private MCVeryBigInfoHUD vbiHud = hudManager.getVbiHud();

    private float stateTime = 0f;
    private final float TIME_SHOWN = 3f;
    private final float BIG_SCALE = 1.15f; // mon nom de boxeur
    private final float LIL_SCALE = 0.25f; // mon nom de rappeur

    public static class MCGSVBIArgs extends MCGameState.StateArgs {
        public CombatDoneArgs combatDoneArgs;
        public MCGSVBIArgs(CombatDoneArgs cba) {
            combatDoneArgs = cba;
        }
    }

    public MCGSVeryBigInformation(MCGame parent) {
        super(parent);
        this.name = "VeryBigInformation";
    }
        

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

    @Override
    public void exit() {
        vbiHud.setDisplay(false);
        //bus.emit("unfreezeGame", parent);
        super.exit();
    }

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

    @Override
    public void render(SpriteBatch batch) {
    }
}
