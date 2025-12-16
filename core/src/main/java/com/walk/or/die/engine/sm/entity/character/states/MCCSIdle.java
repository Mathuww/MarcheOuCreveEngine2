package com.walk.or.die.engine.sm.entity.character.states;

import java.util.HashMap;
import java.util.Map;

import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCAttack;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.entities.MCCharacter.HudCustomization;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.sm.entity.character.MCCharacterState;
import com.walk.or.die.engine.sm.game.MCGameState;
import com.walk.or.die.engine.sm.game.states.MCGSAlliesPlaying;

/**
 * The idle state for ally.<br>
 * Name = "idle"<br>
 * This is a non-blocking state.
 */
public class MCCSIdle extends MCCharacterState<MCCSIdle.IdleStateArgs> {

    /**
     * Class which represents args needed by idle move to start.
     */
    public static class IdleStateArgs extends MCCharacterState.StateArgs {}

    private MCAttack displayedAttack;

    /**
     * The constructor.
     * @param parent
     */
    public MCCSIdle(MCCharacter parent) {
        super(parent);
        this.name = "idle";
    }

    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    @Override
    public void enter(IdleStateArgs args) {
        setupHudCustomization();
        parent.keep = false;
        parent.playAnimationWithoutReset("idle");
        bus.on(this, "GameStateChanged", this::gameStateChanged);
        super.enter(args);
    }

    @Override
    public void exit() {
        parent.keep = true;
        bus.off(this, "GameStateChanged");
        super.exit();
    }
    
    @Override
    protected void inputPressed(MCInputManager.Command data) {
    }

    public void goToReady() {
        if (!parent.focus)
            return; // sécurité
        if (MCEntityManager.get().isAnyoneBusy())
            return;
        if (parent instanceof MCAlly ally 
            && ally.getTurnState().canMove())
            changeState("ready", new MCCSReady.ReadyStateArgs());
    }

    private void setupHudCustomization() {
        if (parent instanceof MCAlly ally) {
            HudCustomization customization = ally.getHudCustomization();

            Map<String, Runnable> validateActions = new HashMap<>();
            if (ally.getTurnState().canMove())
                validateActions.put("MOVE", () -> goToReady());
            if (ally.getTurnState().canAttack())
                validateActions.put("ATTACK", () -> changeState("attackChoice", new MCCSAttackChoice.AtkChoiceStateArgs()));
            customization.carouselValidateActions = validateActions;

            Map<String, Runnable> focusActions = new HashMap<>();
            focusActions.put("MOVE", () -> System.out.println("Move focused"));
            focusActions.put("ATTACK", () -> System.out.println("Attack focused"));
            customization.carouselFocusActions = focusActions;

            customization.choiceMessage = "What should I do ?";
            customization.canShow = true;

            ally.notifyHudUpdate(true);
        } 
    }

    public void gameStateChanged(MCGameState newState) {
        if (newState instanceof MCGSAlliesPlaying) {
            System.out.println("new state is alies playing ! hourrayyy");
            setupHudCustomization();
        }
    }

    @Override
    public boolean isBlocking() {
        return false;
    }
}
