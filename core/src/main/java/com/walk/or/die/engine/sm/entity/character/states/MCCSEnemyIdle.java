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
import com.walk.or.die.engine.ui.MCUICarousel.CarouselItem;

/**
 * The idle state for enemies.<br>
 * Name = "idle".<br>
 * This is a non-blocking state.
 */
public class MCCSEnemyIdle extends MCCharacterState<MCCSIdle.IdleStateArgs> {

    /** The currently displayed attack. */
    private MCAttack displayedAttack;

    /**
     * Constructs a new MCCSEnemyIdle state.
     * @param parent The parent character.
     */
    public MCCSEnemyIdle(MCCharacter parent) {
        super(parent);
        this.name = "idle";
    }

    /**
     * Called on each frame.
     * @param delta The time in seconds since the last frame.
     */
    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    /**
     * Renders the effects.
     * @param batch The sprite batch used for rendering.
     */
    @Override
    public void renderEffects(SpriteBatch batch) {
        if (displayedAttack == null)
            return;
        displayedAttack.render(batch);
    }

    /**
     * Called at entrance.
     * @param args The arguments for entering the idle state.
     */
    @Override
    public void enter(MCCSIdle.IdleStateArgs args) {
        parent.keep = false;
        parent.onHudVisibilityLost();
        setupHudCustomization();
        parent.playAnimation("idle");
        System.out.print(parent.getId() + "entered idle");
        super.enter(args);
    }

    /**
     * Called at exit.
     */
    @Override
    public void exit() {
        parent.keep = true;
        if (displayedAttack != null)
            displayedAttack.display = false;
        super.exit();
    }
    
    /**
     * Handles input pressed events.
     * @param data The input command data.
     */
    @Override
    protected void inputPressed(MCInputManager.Command data) {
        super.inputPressed(data);
        
    }

    /**
     * Launches a move action.
     * @param pos The position to move on.
     */
    public void play(MCIntVector2 pos) {
        changeState("click_move", new MCCSClickMove.MoveStateArgs(pos, MCPathfinder.get().getPath(parent.getTilePosition(), pos)));
    }

    /**
     * Launches a shoot action.
     * @param ally The targeted ally.
     * @param traj The trajectory of the bullet.
     */
    public void shoot(MCAlly ally, List<MCIntVector2> traj) {
        changeState("shoot", new MCCSShoot.ShootStateArgs((MCCharacter)ally, parent.getAttack(), traj));
    }

    /**
     * Determines if the state is blocking.
     * @return True if the state is blocking, false otherwise.
     */
    @Override
    public boolean isBlocking() {
        return false;
    }

    /**
     * Sets up the HUD customization for the character.
     */
    private void setupHudCustomization() {
        if (parent instanceof MCEnemy enemy) {
            Map<String, MCAttack> attacks = enemy.getAttacks();
            HudCustomization customization = enemy.getHudCustomization();

            customization.reset();
            for (String attackName : attacks.keySet()) {
                MCAttack newAttack = attacks.get(attackName);
                customization.carouselItems.add(new CarouselItem(
                    attackName,
                    null,
                    () -> {
                        if (displayedAttack != null)
                            displayedAttack.display = false;

                        if (!parent.getParent().getStateManager().isIn("AlliesPlaying"))
                            return;
                        newAttack.computeValidTilesDisplay();
                        newAttack.display = true;
                        displayedAttack = newAttack;
                        customization.choiceMessage = newAttack.getSummary();
                        hudManager.getCharacterHud().refreshRequest(parent, false);
                    }
                ));
            }

            customization.canShow = true;
            enemy.notifyHudUpdate(true);
        } 
    }

    /**
     * Called when HUD visibility is lost.
     */
    @Override
    public void onHudVisibilityLost() {
        if (displayedAttack != null) {
            displayedAttack.display = false;
            displayedAttack = null;
        }
    }
}