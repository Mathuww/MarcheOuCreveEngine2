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
     * The class which represents args needed by hurt state to start.
     */
    public static class HurtStateArgs extends MCCharacterState.StateArgs {
        private int damage;
        private String targetAnim;

        /**
         * The constructor.
         * @param damage - taken by the character
         * @param targetAnim - to play
         */
        public HurtStateArgs(int damage, String targetAnim) {
            this.damage = damage;
            this.targetAnim = targetAnim;
        }
    }

    private float HURT_DURATION = 2f; 

    private final float BLINKING_INTERVAL = 0.1f;
    private boolean latencyPassed = false;
    private float stateTime = 0f;
    private float blinkingTime = 0f;
    private int damage;

    private MCCharacter hudFocusBeforeHurt;

    /**
     * The constructor.
     * @param parent
     */
    public MCCSHurt(MCCharacter parent) {
        super(parent);
        this.name = "hurt";
    }

    @Override
    public void enter(HurtStateArgs args) {
        super.enter(args);
        damage = args.damage;
        latencyPassed = false;
        stateTime = 0f;
        blinkingTime = 0f;
        hudFocusBeforeHurt = MCHUDManager.get().getCharacterHud().getCharacter();
        MCHUDManager.get().getCharacterHud().setCharacter(parent);
        if (parent instanceof MCAlly) {
            MCCameraManager.get().addTrauma(
                MathUtils.clamp(((float)damage / (float)parent.getMaxHp()), 0f, 1f)
            );
        }
        parent.playAnimation(args.targetAnim);
    }  

    @Override
    public void update(float delta) {
        // pour avoir l'effet bien kiffant de la barre de vie qui descend en gros
        if (!MCHUDManager.get().getCharacterHud().isFullyShown())
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

    @Override
    public void exit() {
        if (hudFocusBeforeHurt != null) {
            MCHUDManager.get().getCharacterHud().setCharacter(hudFocusBeforeHurt);
            MCHUDManager.get().getCharacterHud().refreshRequest(hudFocusBeforeHurt, true);
        }
        parent.display = true;
    }
    
}
