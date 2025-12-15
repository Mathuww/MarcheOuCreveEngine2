package com.walk.or.die.engine.sm.entity.character.states;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
 * The idle but choosing attack state for ally.<br>
 * Name = "attackChoice"<br>
 * This is a non-blocking state.
 */
public class MCCSAttackChoice extends MCCharacterState<MCCSAttackChoice.AtkChoiceStateArgs> {

    /**
     * Class which represents args needed by the attack choice state.
     */
    public static class AtkChoiceStateArgs extends MCCharacterState.StateArgs {}

    private MCAttack displayedAttack;

    /**
     * The constructor.
     * @param parent
     */
    public MCCSAttackChoice(MCCharacter parent) {
        super(parent);
        this.name = "attackChoice";
    }

    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    @Override
    public void enter(AtkChoiceStateArgs args) {
        loadAttackChoiceCarousel();
        parent.playAnimationWithoutReset("idle");
        super.enter(args);
    }

    @Override
    public void exit() {
        if (displayedAttack != null) {
            displayedAttack.display = false;
            displayedAttack = null;
        }
        parent.keep = true;
        bus.off(this, "GameStateChanged");
        super.exit();
    }
    
    @Override
    protected void inputPressed(MCInputManager.Command data) {
        if (!(data instanceof MCInputManager.HudCommand)
            && !(data instanceof MCInputManager.DirectionalCommand) 
            && !(data instanceof MCInputManager.CameraPanCommand)
            && !(data instanceof MCInputManager.CameraZoomCommand)
        ) {
            changeState("idle", new MCCSIdle.IdleStateArgs());
        }
    }

    public void goToAim(MCAttack attack) {
        if (!parent.focus)
            return; // sécurité
        if (MCEntityManager.get().isAnyoneBusy())
            return;
        if (parent instanceof MCAlly ally 
            && ally.getTurnState().canAttack())
            changeState("aim", new MCCSAim.AimStateArgs(attack));
    }

    public void loadAttackChoiceCarousel() {
        if (parent instanceof MCAlly ally) {
            Map<String, MCAttack> attacks = parent.getAttacks();
            HudCustomization customization = ally.getHudCustomization();

            Map<String, Runnable> validateActions = new HashMap<>();
            for (String attackName : attacks.keySet()) {
                MCAttack attack = attacks.get(attackName);
                validateActions.put(
                    attackName,
                    () -> goToAim(attack)
                );
            }
            customization.carouselValidateActions = validateActions;

            Map<String, Runnable> focusActions = new HashMap<>();
            for (String attackName : attacks.keySet()) {
                MCAttack attack = attacks.get(attackName);
                focusActions.put(
                    attackName,
                    () -> {
                        if (displayedAttack != null)
                            displayedAttack.display = false;
                        attack.computeValidTilesDisplay();
                        attack.display = true;
                        displayedAttack = attack;
                        customization.choiceMessage = attack.getSummary();
                        hudManager.getCharacterHud().refreshRequest(parent, false);
                    }
                );
            }
            customization.carouselFocusActions = focusActions;
            customization.canShow = true;
            ally.notifyHudUpdate(true);
        }
    }

    @Override
    public boolean isBlocking() {
        return false;
    }

    @Override
    public void renderOnGridOverlay(SpriteBatch batch) {
        if (displayedAttack == null)
            return;
        displayedAttack.render(batch);
    }
}
