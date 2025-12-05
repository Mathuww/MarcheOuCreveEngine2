package com.walk.or.die.engine;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCAttackFactory;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.entities.MCEntityFactory;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.exceptions.DataException;
import com.walk.or.die.engine.exceptions.UnexistingBehaviorException;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.screens.MCGameScreen;
import com.walk.or.die.engine.shared.MCDebugRenderer;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.vehicles.MCVehicle;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.game.MCGameState;
import com.walk.or.die.engine.sm.game.states.MCGSAlliesPlaying;
import com.walk.or.die.engine.sm.game.states.MCGSEnemiesPlaying;
import com.walk.or.die.engine.sm.game.states.MCGSExploration;
import com.walk.or.die.engine.tiledmap.MCMap;
import com.walk.or.die.engine.tiledmap.MCMapLayer;
import com.walk.or.die.engine.tiledmap.MCPathfinder;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;
import com.walk.or.die.engine.ui.MCHUDManager;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 * tu peux me voir écrire en sdsdsdsdqd temps !!!
 * MOTEUR ! ACTION !
 * THIS WAS NOT CREATED WITH A TEMPLATE.
 * 
 * This is the main class of the game, who manage the whole game.
 */
public class MCGame extends Game {
    /**
     * Width of the viewport.
     */
    public static final int VIEWPORT_WIDTH = 12;
    /**
     * Height of the viewport.
     */
    public static final int VIEWPORT_HEIGHT = 10;
    /**
     * Describe how far the camera can exceed the map lower boundaries.
    */
    public static final Vector2 CAM_LOWER_LIMIT_OFFSET = new Vector2(-2f, -4f);
    /**
     * Describe how far the camera can exceed the map upper boundaries.
    */
    public static final Vector2 CAM_UPPER_LIMIT_OFFSET = new Vector2(2f, 2f); 

    /** 
     * Sets fixed HUD viewport height
    */
    public final static int WINDOW_DEFAULT_HEIGHT = 480;
      /** 
     * Sets fixed HUD viewport width
    */
    public final static int WINDOW_DEFAULT_WIDTH = WINDOW_DEFAULT_HEIGHT * (VIEWPORT_WIDTH / VIEWPORT_HEIGHT);

    /**
     * The current map we are playing on.
     * @see MCTerrainMap
     */
    private MCTerrainMap map;
    /**
     * The pathfinder's singleton.
     * @see MCPathfinder
     */
    private MCPathfinder pathfinder = MCPathfinder.get();
    /**
     * Another HR manager who treats his assets as disposable and replaceable resources.
     * @see AssetManager
     */
    private AssetManager drh = new AssetManager();
    /**
     * The path were all tiled files are.
     */
    private final String TILED_ROOT = "tiled/packed/";
    /**
     * The path wera all tiled maps are.
     */
    private final String MAP_ROOT = TILED_ROOT + "maps/";
    /**
     * The path where all fonts are stored.
     */
    private final String FONT_ROOT = "fonts/";

    /**
     * The MCEntityFactory's singleton.
     * @see MCEntityFactory
     */
    private final MCEntityFactory entityFact = MCEntityFactory.get();
    /**
     * The MCAttackFactory's singleton.
     * @see MCAttackFactory
     */
    private final MCAttackFactory attackFact = MCAttackFactory.get();
    /**
     * The MCSharedAssets's singleton.
     * @see MCSharedAssets
     */
    private final MCSharedAssets sharedAssets = MCSharedAssets.get();

    /**
     * The StateMachine of the game, which determines how the game progresses.
     * @see MCStateMachine
     * @see MCGameState
     */
    private final MCStateMachine<MCGameState, MCGame> stateManager = new MCStateMachine<MCGameState, MCGame>(this);
    /**
     * The unpredictible EventBus, riding your fears, straight towards the wall.
     * @see MCEventBus
     * @see MCVehicle
     */
    private final MCEventBus bus = MCEventBus.get();

    /**
     * The CameraManager's singleton.
     * @see MCCameraManager
     */
    private final MCCameraManager camManager = MCCameraManager.get();

    /**
     * The InputManager's singleton.
     * @see MCInputManager
     */
    private MCInputManager inputHandler = MCInputManager.get();

    /**
     * The EntityManager's singleton
     * @see MCEntityManager
     */
    private MCEntityManager entityManager = MCEntityManager.get();
    /**
     * The current focus Character, null there's no focusCharacter.
     * @see MCCharacter
     */
    private MCCharacter focusedCharacter;

    /**
     * The debug's singleton.
     * @see MCDebugRenderer
     */
    private MCDebugRenderer debugRenderer = MCDebugRenderer.get();

    /**
     * @see SpriteBatch
     */
    public SpriteBatch batch;
    /**
     * @see FitViewport
     */
    public FitViewport gameViewport;
    public FitViewport hudViewport;

    /**
     * HUD manager singleton
     */
    private MCHUDManager hudManager = MCHUDManager.get();

    /**
     * A very big constructor
     */
    public MCGame() {}

    @Override // commence pas je vais t'attraper
              // cast me if you can ;)
              // MCGaia sale_terrorist = (MCGaia) anothercoderterrorist
    public void create() {
        // View objects init
        batch = new SpriteBatch();
        gameViewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

        // Singleton init
        pathfinder.init(this);
        // Debug init
        try {
            sharedAssets.init(MAP_ROOT + "misc.tmx", FONT_ROOT, drh);
            debugRenderer.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Map init (start.tmx)
        map = new MCTerrainMap(MAP_ROOT + "start.tmx", drh);

        // Cam manager init
        camManager.setLowerLimit(CAM_LOWER_LIMIT_OFFSET);
        camManager.setUpperLimit(new Vector2(
            CAM_UPPER_LIMIT_OFFSET.x + map.getWidth(),
            CAM_UPPER_LIMIT_OFFSET.y + map.getHeight()
        ));
        camManager.init(
            VIEWPORT_WIDTH,
            VIEWPORT_HEIGHT,
            MCCameraManager.CameraMode.ARROWS
        );
        gameViewport.setCamera(camManager.getGdxCam());

        // Input init
        inputHandler.init(gameViewport);
        Gdx.input.setInputProcessor(inputHandler);

        // Entities init
        try {
            entityFact.init(drh);
            attackFact.init(drh);
            entityManager.init(this);
            entityManager.addAllEntities(map.spawnEntities(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // sinon les entités ne sont pas créées
        entityManager.update(Gdx.graphics.getDeltaTime());

        // State init
        //stateManager.addState(new MCGSCombat(this));
        stateManager.addState(new MCGSAlliesPlaying(this));
        stateManager.addState(new MCGSEnemiesPlaying(this));
        stateManager.addState(new MCGSExploration(this));
        // stateManager.setCurrentState("combat", new MCGSCombat.CombatStateArgs());
        stateManager.setCurrentState("AlliesPlaying", new MCGSAlliesPlaying.AlliesPlayingArgs());

        // entityManager.playGlobalAnimation("idle");

        hudViewport = new FitViewport(WINDOW_DEFAULT_WIDTH, WINDOW_DEFAULT_HEIGHT);
        hudManager.init(hudViewport);

        try {
            setScreen(new MCGameScreen(this));
        } catch (DataException e) {
            e.printStackTrace();
        }
    }

    /**
     * The global logic of the game, triggered each frame.
     * @param delta
     */
    private void logic(float delta) {
        // We don't give a fuck about logic
        // Because we implement MVC (Modular Venomous Contraception) // co autored by
        // mathuww

        try {
            camManager.update(delta);
        } catch (UnexistingBehaviorException e) {
            e.printStackTrace();
        }

        // si la camera bouge, la tile visée a bougé!
        if (camManager.hasMovedThisFrame())
            MCInputManager.get().triggerMouseUpdate();

        stateManager.update(delta);
        entityManager.update(delta);

        hudManager.update(delta);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        logic(delta);
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    /**
     * Change the focused Character
     * @param c le nouveau character focus (peut être null)
     */
    public void changeFocus(MCCharacter c) {
        if (focusedCharacter != null) {
            if (focusedCharacter.loseFocus()) {
                focusedCharacter = c;
                if (c != null) {
                    c.getFocus();
                }
            }
        } else {
            focusedCharacter = c;
            if (c != null) {
                c.getFocus();
            }
        }
    }

    /**
     * @return the state manager.
     */
    public MCStateMachine getStateManager() {
        return this.stateManager;
    }

    /**
     * @return the terrain map.
     */
    public MCTerrainMap getTerrainMap() {
        return this.map;
    }

    /**
     * @param pos the case's position
     * @return if we can walk on this case
     */
    public boolean isWalkable(MCIntVector2 pos) {
        if (entityManager.getEntityFromTile(1, pos) == null) {
            return map.isWalkable(pos);
        }
        return false;
    }
}