package com.walk.or.die.engine.sm.entity.character.states;

import java.util.HashMap;
import java.util.Map;

import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.entities.MCCharacter.HudCustomization;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.sm.entity.character.MCCharacterState;
import com.walk.or.die.engine.sm.game.MCGameState;
import com.walk.or.die.engine.sm.game.states.MCGSAlliesPlaying;
import com.walk.or.die.engine.ui.MCUICarousel.CarouselItem;

/**
 * The idle state for ally.<br>
 * Name = "idle"<br>
 * This is a non-blocking state.
 */
public class MCCSIdle extends MCCharacterState<MCCSIdle.IdleStateArgs> {

    /**
     * Class which represents args needed by idle move to start.
     */
    public static class IdleStateArgs extends MCCharacterState.StateArgs {
        // ajouter derniere direction pour jouer idle_up, idle_right etc.
    }

    /**
     * The constructor.
     * @param parent the parent character
     */
    public MCCSIdle(MCCharacter parent) {
        super(parent);
        this.name = "idle";
    }

    /**
     * Called on each frame
     * @param delta the delta time
     */
    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    /**
     * Called at state entrance.
     * @param args the arguments for the idle state
     */
    @Override
    public void enter(IdleStateArgs args) {
        setupHudCustomization();
        parent.keep = false;
        parent.playAnimationWithoutReset("idle");
        bus.on(this, "GameStateChanged", this::gameStateChanged);
        super.enter(args);
    }

    /**
     * Called at state exit
     */
    @Override
    public void exit() {
        parent.keep = true;
        bus.off(this, "GameStateChanged");
        super.exit();
    }
    
    /**
     * Processes input when a key is pressed.
     *
     * @param data The input command data.
     */
    @Override
    protected void inputPressed(MCInputManager.Command data) {
    }

    /**
     * Transitions to the "ready" state if conditions are met.
     */
    public void goToReady() {
        //System.out.println(parent.getId() + "ran gotoready()");
        if (!parent.focus)
            return; // sécurité
        //System.out.println(parent.getId() + "ran gotoready() past 1st security");
        if (MCEntityManager.get().isAnyoneBusy())
            return;
        //System.out.println(parent.getId() + "ran gotoready() past 2nd security");
        if (parent instanceof MCAlly ally 
            && ally.getTurnState().canMove())
            changeState("ready", new MCCSReady.ReadyStateArgs());
    }

    /**
     * Transitions to the "aim" state if conditions are met.
     */
    public void goToAim() {
        if (!parent.focus)
            return;
        if (MCEntityManager.get().isAnyoneBusy())
            return;
        if (parent instanceof MCAlly ally 
            && ally.getTurnState().canAttack())
            changeState("aim", new MCCSAim.AimStateArgs());
    }

    public void goToCapaChoose() {
        if (!parent.focus)
            return;
        if (MCEntityManager.get().isAnyoneBusy())
            return;
        if (parent instanceof MCAlly ally 
            && ally.getTurnState().canUseCapacity())
            changeState("capacityChoose", new MCCSCapacityChoose.CapaChooseArgs());
    }

    /**
     * Sets up the HUD customization for the ally character.
     */
    private void setupHudCustomization() {
        if (parent instanceof MCAlly ally) {
            HudCustomization customization = ally.getHudCustomization();

            customization.reset();
            if (ally.getTurnState().canMove()) {
                customization.carouselItems.add(new CarouselItem(
                    "MOVE",
                    () -> goToReady(),
                    null
                ));
            }
            if (ally.getTurnState().canAttack()) {
                customization.carouselItems.add(new CarouselItem(
                    "ATTACK",
                    () -> goToAim(),
                    null
                ));
            }
            if (ally.getTurnState().canUseCapacity() && ally.getLaunchableEffects().size() > 0) {
                customization.carouselItems.add(new CarouselItem(
                    "CAPACITY",
                    () -> goToCapaChoose(),
                    null
                ));
            }

            customization.choiceMessage = "What should I do ?";
            customization.canShow = true;

            ally.notifyHudUpdate(true);
        } 
    }

    /**
     * Handles game state changes.
     *
     * @param newState The new game state.
     */
    public void gameStateChanged(MCGameState newState) {
        if (newState instanceof MCGSAlliesPlaying) {
            //System.out.println("new state is alies playing ! hourrayyy");
            setupHudCustomization();
        }
    }

    /**
     * Checks if this state is blocking.
     *
     * @return {@code false} because this state is non-blocking.
     */
    @Override
    public boolean isBlocking() {
        return false;
    }
}