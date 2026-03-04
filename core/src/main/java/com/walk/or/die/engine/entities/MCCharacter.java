package com.walk.or.die.engine.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.capacities.MCDecreaseShieldEffect;
import com.walk.or.die.engine.capacities.MCEffectFactory;
import com.walk.or.die.engine.capacities.MCEffects;
import com.walk.or.die.engine.capacities.MCShieldEffect;
import com.walk.or.die.engine.capacities.MCSpeedShoot;
import com.walk.or.die.engine.capacities.MCSpeedUpEffect;
import com.walk.or.die.engine.capacities.MCStrengthEffect;
import com.walk.or.die.engine.exceptions.MissingDataException;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.shared.MCUtils;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.entity.character.MCCharacterState;
import com.walk.or.die.engine.sm.entity.character.states.MCCSDead;
import com.walk.or.die.engine.sm.entity.character.states.MCCSHurt;
import com.walk.or.die.engine.sm.entity.character.states.MCCSIdle;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;
import com.walk.or.die.engine.ui.MCCharacterHUD;
import com.walk.or.die.engine.ui.MCHUDManager;
import com.walk.or.die.engine.ui.MCTerrainHPBar;
import com.walk.or.die.engine.ui.MCUICarousel.CarouselItem;

/**
 * An entity which can move and shoot.
 */
public class MCCharacter extends MCEntity {
    /**
     * A class that encapsulates HUD customization settings for the character.
     */
    public class HudCustomization {
        /**
         * The list of carousel items.
         */
        public List<CarouselItem> carouselItems = new ArrayList<>();
        /**
         * The first index of the carousel.
         */
        public int carouselFirstIndex = 0;
        /**
         * The message displayed when the carousel is empty.
         */
        public String carouselEmptyMsg = "(Nothing to see here)";
        /**
         * The message displayed for the current choice.
         */
        public String choiceMessage = "";
        /**
         * Indicates whether the HUD can be shown.
         */
        public boolean canShow = true;

        /**
         * Resets the HUD customization.
         */
        public void reset() {
            carouselItems = new ArrayList<>();
            carouselFirstIndex = 0;
            carouselEmptyMsg = "(Nothing to see here)";
            choiceMessage = "";
            canShow = true;
        }
    }

    /**
     * The maximum number of attacks the character can have.
     */
    protected final int MAX_ATTACK_NUMBER = 6;
    /**
     * The maximum number of capacities (effects) the character can have.
     */
    protected final int MAX_CAPACITY_NUMBER = 6;
    /**
     * The maximum number of hurt animations available.
     */
    protected final int MAX_HURT_ANIM_NUMBER = 6;
    
    /**
     * The display name of the character.
     */
    private String displayName;

    /**
     * The maximum health points of the character.
     */
    private Integer maxHp;
    /**
     * The current health points of the character.
     */
    private Integer hp;
    /**
     * The tolerance for the hitbox percentage.
     */
    private Float toleranceHitbox;
    /**
     * Indicates whether the character is dead.
     */
    private boolean dead = false;
    /**
     * The maximum number of movements (deplacements) allowed per turn.
     */
    private Integer maxDeplacements;
    /**
     * The movement speed of the character.
     */
    private Float speed;
    /**
     * The state machine managing character states.
     */
    private MCStateMachine<MCCharacterState, MCEntity> stateManager;
    /**
     * A map of attacks available to the character, keyed by their names.
     */
    private Map<String, MCAttack> attacks;
    /**
     * The name of the character's base attack.
     */
    private String baseAttack;
    /**
     * The name of the currently displayed attack.
     */
    private String displayedAttack;
    /**
     * The move display for the character, showing possible movement paths.
     */
    private MCMoveDisplay moveDisplay;
    /**
     * Indicates whether the character can shoot.
     */
    private boolean shoot = true;

    /**
     * The health bar displayed for the character.
     */
    private MCTerrainHPBar healthBar;
    /**
     * The customization settings for the character's HUD.
     */
    private HudCustomization hudCustomization = new HudCustomization();

    /**
     * A list of effects that the character can launch.
     */
    private List<MCEffects> launchableEffects = new ArrayList<>();
    /**
     * A list of active effects on the character.
     */
    private List<MCEffects> effects = new ArrayList<>();

    /**
     * The random number generator for various actions.
     */
    private Random rng = new Random();

    /**
     * Constructs a new MCCharacter.
     * @param parent The parent MCGame instance.
     * @param map The terrain map.
     * @param entityGenericName The generic name of the entity.
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

        /*
        launchableEffects.add(new MCDecreaseShieldEffect(this));
        launchableEffects.add(new MCSpeedUpEffect(this));
        launchableEffects.add(new MCSpeedShoot(this));
        launchableEffects.add(new MCStrengthEffect(this));
        launchableEffects.add(new MCShieldEffect(this));
        */
    }

    /**
     * Called at entrance.
     */
    @Override
    public void onSpawn() {
        stateManager.setCurrentState("idle", new MCCSIdle.IdleStateArgs());
        healthBar = new MCTerrainHPBar(this, getParent().gameViewport);
        //effects.add(new MCSpeedUpEffect(this, "speed"));
        //effects.add(new MCShieldEffect(this, "shield_test"));
        //effects.add(new MCSpeedShoot(this, "speedShoot"));
    }

    /**
     * Initializes the character from properties.
     * @param props The properties to initialize from.
     * @throws Exception If an error occurs during initialization.
     */
    @Override
    public void initFromProperties(MapProperties props) throws Exception {
        maxHp = MCUtils.getIntProperty(props, "hp", 100);
        hp = maxHp;
        maxDeplacements = MCUtils.getIntProperty(props, "maxMoves", 2);
        toleranceHitbox = (MCUtils.getFloatProperty(props, "hitboxTolerancePercentage", 0.05f))/(100*2);
        speed = (MCUtils.getFloatProperty(props, "speed", 2f));

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
            addAttack(attack.getName(), attack);
        }

        for (int i = 1; i < MAX_CAPACITY_NUMBER; i++) {
            String capacityName = props.get("capacity" + i, String.class);
            if (capacityName == null) 
                break;
            MCEffects capa = MCEffectFactory.get().buildEffect(this, capacityName);
            if (capa == null)
                continue;
            if (capa instanceof MCDecreaseShieldEffect se) {
                float percentage = MCUtils.getFloatProperty(props, "capacity" + i + "_percentage", 50f);
                se.setPercentage(percentage);
                int dist = MCUtils.getIntProperty(props, "capacity" + i + "_dist", 4);
                se.setAffectDistance(dist);
            } else if (capa instanceof MCSpeedUpEffect eff) {
                int dist = MCUtils.getIntProperty(props, "capacity" + i + "_dist", 4);
                eff.setAffectDistance(dist);
            }
            launchableEffects.add(capa);
        }

        baseAttack = props.get("baseAttack", String.class);
    }

    /**
     * Adds an attack to the character.
     * @param name The name of the attack.
     * @param attack The attack to add.
     * @see MCAttack
     */
    public void addAttack(String name, MCAttack attack) {
        attacks.put(name, attack);
    }

    /**
     * Called on each frame.
     * @param delta The time delta.
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        if (!isFreeze()) stateManager.update(delta);
        if (healthBar != null)
            healthBar.update(delta);
        for (MCEffects e: effects) e.update(delta);
    }

    /**
     * Called on each frame.
     * @param batch The sprite batch to render to.
     */
    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        stateManager.render(batch);
    }

    /**
     * Spawns a damage indicator.
     * @param damage The amount of damage to show.
     */
    public void spawnDamageIndicator(int damage) {
        healthBar.showDamage(damage);
    }

    /**
     * Gets the launchable effects.
     * @return The list of launchable effects.
     */
    public List<MCEffects> getLaunchableEffects() {
        return launchableEffects;
    }

    /**
     * Adds an effect to the character.
     * @param effect The effect to add.
     */
    public void addEffect(MCEffects effect) {
        effects.add(effect);
    }
    
    /**
     * Removes an effect from the character.
     * @param name The name of the effect to remove.
     */
    public void removeEffect(String name) {
        Iterator<MCEffects> it = effects.iterator();
        while (it.hasNext()) {
            if (it.next().name.equals(name)) it.remove();
        }
    }

    /**
     * Removes all disposable effects. Called each turn by default.
     */
    public void cleanEffects() {
        Iterator<MCEffects> it = effects.iterator();
        while (it.hasNext()) {
            if (it.next().isDisposable()) it.remove();
        }
    }

    /**
     * Called at the beginning of the turn.
     */
    public void newTurn() {
        for (MCEffects e: effects) {
            e.onNewTurn();
        }
        cleanEffects();
    }
    
    /**
     * Renders the character's effects and overlays. (Called each frame, after classic render)
     * @param batch The sprite batch to render to.
     */
    public void renderEffects(SpriteBatch batch) {
        // j'ai séparé le rendu de l'entité proprement dit
        // et celles de ses overlays
        // dans EntityManager,
        // sinon les prochaines entités viennent par dessus
        stateManager.renderEffects(batch);
        if (healthBar != null && hp < maxHp)
            healthBar.render(batch);
        for (MCEffects e: effects) e.render(batch);
    }

    /**
     * Sets the state manager for the character.
     * @param stateManager The state machine to set.
     */
    public void setStateManager(MCStateMachine<MCCharacterState, MCEntity> stateManager) {
        this.stateManager = stateManager;
    }

    /**
     * Gets the maximum number of moves.
     * @return The maximum number of moves.
     */
    public int getMaxMoves() {
        int move = this.maxDeplacements;
        for (MCEffects e: effects) {
            move = e.getMaxMoves(move);
        }
        return move;
    }

    /**
     * Gets the character's speed.
     * @return The character's speed.
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Gets the tolerance hitbox percentage.
     * @return The tolerance hitbox percentage.
     */
    public Float getToleranceHitbox() {
        return toleranceHitbox;
    }

    /**
     * Gets the move display.
     * @return The move display instance.
     * @see MCMoveDisplay
     */
    public MCMoveDisplay getMoveDisplay() {
        return moveDisplay;
    }

    /**
     * Gets the map of available attacks.
     * @return A map containing the character's attacks.
     */
    public Map<String, MCAttack> getAttacks() {
        return attacks;
    }

    /**
     * Gets the character's current (base) attack.
     * @return The current attack instance.
     * @throws IllegalStateException If a base attack is not defined.
     * @see MCAttack
     */
    public MCAttack getAttack() {
        // FIX TEMPORAIRE !! c'est juste pour faire essayer le jeu à camylle
        return attacks.get("BOW");
        // faudra que les ennemis puissent décider QUELLE attaque utiliser parmi les leurs
        // pas juste une seule attaque aussi !
        /*
        MCAttack attack = attacks.get(baseAttack);
        if (attack == null)
            throw new IllegalStateException("trying to shoot without a base attack !");
        attack.setPower(attack.getBasePower());
        for (MCEffects e: effects) {
            e.onAttack(attack);
        }
        return attack;
        */
    }
    
    /**
     * Shoots a projectile and calls a function when the action ends.
     * @param end The target position for the projectile.
     * @param attack The attack to use for shooting.
     * @param onArrival The runnable to call upon the projectile's arrival.
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
     * Gets the maximum health points.
     * @return The maximum health points.
     */
    public int getMaxHp() {
        return maxHp;
    }

    /**
     * Gets the current health points.
     * @return The current health points.
     */
    public int getHealth() {
        return hp;
    }

    /**
     * Sets the character's current health.
     * @param hp The health points to set.
     */
    public void setHealth(int hp) {
        this.hp = MathUtils.clamp(hp, 0, maxHp);
    }

    /**
     * Handles the character's hurt behavior and applies damage.
     * @param damage The amount of damage to apply.
     */
    public void getHurt(int damage) {
        if (dead)
            return;
        if (damage < 0f) 
            throw new IllegalArgumentException("Can't get hurt with negative damage.");

        for (MCEffects e: effects) {
            damage = e.onHurt(damage);
        }
        // Anim aléatoire parmi hurt, hurt2, hurt3 ...
        List<String> existingHurtAnims = new ArrayList<>();
        for (int i = 1; i < MAX_HURT_ANIM_NUMBER; i++) {
            String hurtAnimToSearch = (i == 1) ? "hurt" : ("hurt" + i);
            //System.out.println("searching for hurt anim " + hurtAnimToSearch);
            if (getAnimation(hurtAnimToSearch) != null) {
                //System.out.println("anim " + hurtAnimToSearch + " exists");
                existingHurtAnims.add(hurtAnimToSearch);
            }
        }
        String animToPlay;
        if (existingHurtAnims.size() == 0) {
            System.err.println(getId() + " doesnt have any HURT animations to play");
            animToPlay = "hurt";
        } else {
            int animIndexToPlay = rng.nextInt(existingHurtAnims.size());
            animToPlay = existingHurtAnims.get(animIndexToPlay);
        }

        if (!isFreeze())  {
            stateManager.setCurrentState("hurt", new MCCSHurt.HurtStateArgs(damage, animToPlay));
            //System.out.println("On lance le hurt");
        } else {
            setHealth(Math.max(0, getHealth() - damage));
            if (getHealth() <= 0)
                stateManager.setCurrentState("dead", new MCCSDead.DeadStateArgs());
        }
    }
    
    /**
     * Checks if the character is currently dead.
     * @return True if the character is dead, false otherwise.
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * Sets the character's dead state.
     */
    public void setDead() {
        dead = true;
    }

    /**
     * Checks if the character is currently busy with an action.
     * @return True if the character is busy, false otherwise.
     */
    public boolean isBusy() {
        if (stateManager.getCurrentState() == null) 
            return false;
        return stateManager.getCurrentState().isBlocking();
    }

    /**
     * Sets the display name for the character.
     * @param displayName The name to display for the character.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the name to display for the character.
     * @return The character's display name, or its ID if not set.
     */
    public String getDisplayName() {
        if (displayName != null)
            return displayName;
        else 
            return getId();
    }

    /**
     * Gets the character's state manager.
     * @return The state machine instance.
     */
    public MCStateMachine getStateManager() {
        return stateManager;
    }

    /**
     * Gets the HUD customization settings for the character.
     * @return The HUD customization settings.
     */
    public HudCustomization getHudCustomization() {
        return hudCustomization;
    }

    /**
     * Notifies the HUD manager to update the character's HUD.
     * @param reloadCarousel True to reload the carousel items, false otherwise.
     */
    public void notifyHudUpdate(boolean reloadCarousel) {
        MCHUDManager.get().getCharacterHud().refreshRequest(this, reloadCarousel);
    }

    /**
     * Called when HUD visibility is lost.
     */
    public void onHudVisibilityLost() {
        stateManager.getCurrentState().onHudVisibilityLost();
    }
}