package com.walk.or.die.engine.sm.entity.character.states;

import com.badlogic.gdx.math.MathUtils;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.sm.entity.character.MCCharacterState;
import com.walk.or.die.engine.ui.MCHUDHPBar;
import com.walk.or.die.engine.ui.MCHUDManager;

/**
 * The state to take damages.<br>
 * Name = "hurt"
 */
public class MCCSHurt extends MCCharacterState<MCCSHurt.HurtStateArgs> {
    /**
     * The class which represents arguments needed by hurt state to start.
     */
    public static class HurtStateArgs extends MCCharacterState.StateArgs {
        private int damage;
        private String targetAnim;

        /**
         * The constructor.
         * @param damage The damage taken by the character
         * @param targetAnim The animation to play
         */
        public HurtStateArgs(int damage, String targetAnim) {
            this.damage = damage;
            this.targetAnim = targetAnim;
        }

        /**
         * Gets the damage.
         * @return The damage
         */
        public int getDamage() {
            return damage;
        }

        /**
         * Gets the target animation.
         * @return The target animation
         */
        public String getTargetAnim() {
            return targetAnim;
        }
    }

    private float HURT_DURATION = 2f;

    private final float BLINKING_INTERVAL = 0.1f;
    private boolean latencyPassed = false;
    private float stateTime = 0f;
    private float blinkingTime = 0f;
    private int damage;

    private MCCharacter hudFocusBeforeHurt;
    private boolean hudRightPanelDispBefore = false;

    /**
     * The constructor.
     * @param parent The parent character
     */
    public MCCSHurt(MCCharacter parent) {
        super(parent);
        this.name = "hurt";
    }

    /**
     * Called at state entrance.
     * @param args The arguments for the hurt state
     */
    @Override
    public void enter(HurtStateArgs args) {
        super.enter(args);
        damage = args.damage;
        latencyPassed = false;
        stateTime = 0f;
        blinkingTime = 0f;
        hudFocusBeforeHurt = hudManager.getCharacterHud().getCharacter();
        hudRightPanelDispBefore = hudManager.getCharacterHud().getRightPanelDisplay();
        hudManager.getCharacterHud().setRightPanelDisplay(false);
        hudManager.getCharacterHud().setCharacter(parent);
        if (parent instanceof MCAlly) {
            camManager.addTrauma(
                MathUtils.clamp(((float)damage / (float)parent.getHealth()), 0f, 0.5f)
            );
        }

        parent.spawnDamageIndicator(damage);

        parent.playAnimation(args.targetAnim);
    }

    /**
     * Called on each frame.
     * @param delta The time delta
     */
    @Override
    public void update(float delta) {
        // pour avoir l'effet bien kiffant de la barre de vie qui descend en gros
        if (!hudManager.getCharacterHud().isFullyShown())
            return;
        if (!latencyPassed) {
            parent.setHealth(Math.max(0, parent.getHealth() - damage));
            latencyPassed = true;
        }
        stateTime += delta;
        blinkingTime += delta;
        if (stateTime >= HURT_DURATION) { // c'est bon un peu de discipline, releve toi non ? va pas chialer 1000 ans lui
            if (parent.getHealth() <= 0)
                changeState("dead", new MCCSDead.DeadStateArgs());
            else
                changeState("idle", new MCCSIdle.IdleStateArgs());
            return;
        }
        if (blinkingTime >= BLINKING_INTERVAL) {
            parent.display = !parent.display; // on inverse la visiblité
            blinkingTime = 0f;
        }
    }

    /**
     * Called at state exit.
     */
    @Override
    public void exit() {
        if (hudFocusBeforeHurt != null) {
            hudManager.getCharacterHud().setCharacter(hudFocusBeforeHurt);
            hudManager.getCharacterHud().refreshRequest(hudFocusBeforeHurt, true);
        }
        hudManager.getCharacterHud().setRightPanelDisplay(hudRightPanelDispBefore);
        parent.display = true;
    }
    
}