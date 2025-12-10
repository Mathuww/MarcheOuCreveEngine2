package com.walk.or.die.engine.entities;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.shared.MCUtils;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.entity.character.MCCharacterState;
import com.walk.or.die.engine.sm.entity.character.states.MCCSHurt;
import com.walk.or.die.engine.sm.entity.character.states.MCCSIdle;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;
import com.walk.or.die.engine.ui.MCCharacterHUD;
import com.walk.or.die.engine.ui.MCOnGridHealth;

/**
 * An entity wich can move and shoot.
 */
public class MCCharacter extends MCEntity {
    protected final int MAX_ATTACK_NUMBER = 6;
    
    private String displayName;

    private Integer maxHp;
    private Integer hp;
    private boolean dead = false;
    private Integer maxDeplacements;
    private MCStateMachine<MCCharacterState, MCEntity> stateManager;
    private Map<String, MCAttack> attacks;
    private String baseAttack;
    private String displayedAttack;
    private MCMoveDisplay moveDisplay;
    private boolean shoot = true;

    private MCOnGridHealth healthBar;
    private boolean displayHud = true;

    /**
     * The creator.
     * @param parent
     * @param map
     * @param entityGenericName
     */
    public MCCharacter(MCGame parent, MCTerrainMap map, String entityGenericName) {
        super(parent, map, entityGenericName);
        attacks = new HashMap<>();
        stateManager = new MCStateMachine<>(this);
        try {
            moveDisplay = new MCMoveDisplay(this);
        } catch(Exception e) { 
            // Mouahahaha e.printStackTrace(); // pitié :'''''''''''''''''')
            e.printStackTrace();
        }
    }

    @Override
    public void onSpawn() {
        stateManager.setCurrentState("idle", new MCCSIdle.IdleStateArgs());
        healthBar = new MCOnGridHealth(this);
    }

    @Override
    public void initFromProperties(MapProperties props) throws Exception {
        maxHp = MCUtils.getIntProperty(props, "hp", 100);
        hp = maxHp;
        maxDeplacements = MCUtils.getIntProperty(props, "maxMoves", 2);

        MCAttackFactory attackFact = MCAttackFactory.get();
        for (int i = 1; i < MAX_ATTACK_NUMBER; i++) {
            // on vient chercher attack1, attack2, etc.
            //System.out.println("searching for " + "attack" + i);
            String attackName = props.get("attack" + i, String.class);
            if (attackName == null) {
                //System.out.println("not found");
                break;
            }
            MCAttack attack = attackFact.build(this, attackName);
            //System.out.println(attackName + attack.toString());
            addAttack(attackName, attack);
        }

        baseAttack = props.get("baseAttack", String.class);
    }

    /**
     * Add a attack to your character.
     * @param name
     * @param attack
     * @see MCAttack
     */
    public void addAttack(String name, MCAttack attack) {
        attacks.put(name, attack);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        stateManager.update(delta);
        if (healthBar != null)
            healthBar.update(delta);
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        stateManager.render(batch);
    }

    /**
     * Render effect (call each frame, after classic render)
     * @param batch
     */
    public void renderOnGridOverlay(SpriteBatch batch) {
        // j'ai séparé le rendu de l'entité proprement dit
        // et celles de ses overlays
        // dans EntityManager,
        // sinon les prochaines entités viennent par dessus
        stateManager.renderOnGridOverlay(batch);
        if (healthBar != null && hp < maxHp)
            healthBar.render(batch);
    }

    /**
     * Set the stage manager.
     * @param stateManager
     */
    public void setStateManager(MCStateMachine<MCCharacterState, MCEntity> stateManager) {
        this.stateManager = stateManager;
    }

    /**
     * Get max moves.
     * @return
     */
    public int getMaxMoves() {
        return this.maxDeplacements;
    }

    /**
     * Get the move display
     * @return
     * @see MCMoveDisplay
     */
    public MCMoveDisplay getMoveDisplay() {
        return moveDisplay;
    }

    /**
     * Get the current attack
     * @return
     * @throws IllegalStateException
     * @see MCAttack
     */
    public MCAttack getAttack() {
        MCAttack attack = attacks.get(baseAttack);
        if (attack == null)
            throw new IllegalStateException("trying to shoot without a base attack !");
        return attack;
    }
    
    /**
     * Shoot and call a function when the action ends.
     * @param end
     * @param attack
     * @param onArrival
     */
    public void shootThenCall(MCIntVector2 end, MCAttack attack, Runnable onArrival) {
        // System.out.println("trying to shoot with damage : " + damage);
        MCProjectile proj;
        try {
            proj = attack.spawnProjectile();
        } catch (Exception e) {
            System.err.println("cant spawn projectile from " + getTilePosition().toString() + " to " + end.toString());
            e.printStackTrace();
            return;
        }
        proj.setPosition(getPosition()); // le projectile commence ici !
        proj.callOnArrival(onArrival);
        proj.launchTo(end);
    }

    /**
     * Get the max hp.
     * @return
     */
    public int getMaxHp() {
        return maxHp;
    }

    /**
     * Get current health.
     * @return
     */
    public int getHealth() {
        return hp;
    }

    /**
     * Hurt behavior.
     * @param damage
     * @param targetAnim
     */
    public void getHurt(int damage, String targetAnim) {
        if (dead)
            return;
        if (damage < 0f) 
            throw new IllegalArgumentException("cant get hurt with negative damage");
        hp = Math.max(0, hp - damage);
        stateManager.setCurrentState("hurt", new MCCSHurt.HurtStateArgs(damage, targetAnim));
        System.out.println("J'ai pris " + damage + "dégats !");
    }
    
    /**
     * If the character is already dead.
     * @return
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * Set dead state.
     */
    public void setDead() {
        dead = true;
    }

    /**
     * If there's currently an action running.
     * @return
     */
    public boolean isBusy() {
        if (stateManager.getCurrentState() == null) 
            return false;
        return stateManager.getCurrentState().isBlocking();
    }

    /**
     * Set the name to display.
     * @param displayName
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Get the name to display.
     * @return
     */
    public String getDisplayName() {
        if (displayName != null)
            return displayName;
        else 
            return getId();
    }

    /**
     * Get the state manager.
     */
    public MCStateMachine getStateManager() {
        return stateManager;
    }
}
