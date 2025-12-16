package com.walk.or.die.engine.sm.entity.character.states;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCAttack;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEnemy;
import com.walk.or.die.engine.entities.MCCharacter.HudCustomization;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.sm.entity.character.MCCharacterState;
import com.walk.or.die.engine.tiledmap.MCPathfinder;

/**
 * The idle state for ennemies.<br>
 * Name = "idle".<br>
 * This is a non-blocking state.
 */
public class MCCSEnemyIdle extends MCCharacterState<MCCSIdle.IdleStateArgs> {

    private MCAttack displayedAttack;

    public MCCSEnemyIdle(MCCharacter parent) {
        super(parent);
        this.name = "idle";
    }

    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    @Override
    public void renderOnGridOverlay(SpriteBatch batch) {
        if (displayedAttack == null)
            return;
        displayedAttack.render(batch);
    }

    @Override
    public void enter(MCCSIdle.IdleStateArgs args) {
        parent.keep = false;
        parent.onHudVisibilityLost();
        setupHudCustomization();
        parent.playAnimation("idle");
        super.enter(args);
    }

    @Override
    public void exit() {
        parent.keep = true;
        if (displayedAttack != null)
            displayedAttack.display = false;
        super.exit();
    }
    
    @Override
    protected void inputPressed(MCInputManager.Command data) {
        super.inputPressed(data);
        
    }

    /**
     * Launch move action.
     * @param pos - to move on.
     */
    public void play(MCIntVector2 pos) {
        changeState("click_move", new MCCSClickMove.MoveStateArgs(pos, MCPathfinder.get().getPath(parent.getTilePosition(), pos)));
    }

    /**
     * Launch shoot action.
     * @param ally - targeted.
     * @param traj - of the bullet.
     */
    public void shoot(MCAlly ally, List<MCIntVector2> traj) {
        changeState("shoot", new MCCSShoot.ShootStateArgs((MCCharacter)ally, parent.getAttack(), traj));
    }

    @Override
    public boolean isBlocking() {
        return false;
    }

    private void setupHudCustomization() {
        if (parent instanceof MCEnemy enemy) {
            Map<String, MCAttack> attacks = enemy.getAttacks();
            HudCustomization customization = enemy.getHudCustomization();

            Map<String, Runnable> validateActions = new HashMap<>();
            for (String attackName : attacks.keySet()) {
                MCAttack attack = attacks.get(attackName);
                validateActions.put(attackName, null);
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
            enemy.notifyHudUpdate(true);
        } 
    }

    @Override
    public void onHudVisibilityLost() {
        if (displayedAttack != null) {
            displayedAttack.display = false;
            displayedAttack = null;
        }
    }
}
