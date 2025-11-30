package com.walk.or.die.engine;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
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
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.game.MCGameState;
import com.walk.or.die.engine.sm.game.states.MCGSCombat;
import com.walk.or.die.engine.sm.game.states.MCGSExploration;
import com.walk.or.die.engine.tiledmap.MCMap;
import com.walk.or.die.engine.tiledmap.MCMapLayer;
import com.walk.or.die.engine.tiledmap.MCPathfinder;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MCGame extends Game {
    public static final int VIEWPORT_WIDTH = 12;
    public static final int VIEWPORT_HEIGHT = 10;

    // Map
    private MCTerrainMap map;
    private MCPathfinder pathfinder = MCPathfinder.get();
    private AssetManager drh = new AssetManager();
    private final String TILED_ROOT = "tiled/packed/";
    private final String MAP_ROOT = TILED_ROOT + "maps/";

    private final MCEntityFactory entityFact = MCEntityFactory.get();
    private final MCAttackFactory attackFact = MCAttackFactory.get();
    private final MCSharedAssets sharedAssets = MCSharedAssets.get();

    private final MCStateMachine<MCGameState, MCGame> stateManager = new MCStateMachine<MCGameState,MCGame>(this);
    private final MCEventBus bus = MCEventBus.get();

    // Camera
    private final MCCameraManager camManager = MCCameraManager.get();

    // Input
    private MCInputManager inputHandler = MCInputManager.get();
    
    // Entity
    private MCEntityManager entityManager = MCEntityManager.get();
    private String playerEntityName;
    private MCCharacter main;
    private MCEntity focusedEntity;

    // Debug
    private MCDebugRenderer debugRenderer = MCDebugRenderer.get();

    public SpriteBatch batch;
    public FitViewport viewport;

    public MCGame() {}

    @Override // commence pas je vais t'attraper
    // cast me if you can ;)
    // MCGaia sale_terrorist = (MCGaia) anothercoderterrorist
    
    public void create() {
        // View objects init
        batch = new SpriteBatch();
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

        // Singleton inits
        pathfinder.init(this);

        try {
            sharedAssets.init(MAP_ROOT + "misc.tmx", drh);
            debugRenderer.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Map init (start.tmx)
        map = new MCTerrainMap(MAP_ROOT + "start.tmx", drh);

        // Cam manager init
        camManager.init(
            VIEWPORT_WIDTH,
            VIEWPORT_HEIGHT,
            MCCameraManager.CameraMode.ARROWS
        );
        camManager.setLimitX(map.getWidth());
        camManager.setLimitY(map.getHeight());
        viewport.setCamera(camManager.getGdxCam());

        // Input init
        inputHandler.init(viewport);
        Gdx.input.setInputProcessor(inputHandler);

        //bus.on(this, "ChangedFocus", this::changeFocus);

        // Entities init
        try {
            entityFact.init(drh);
            attackFact.init(drh);
            entityManager.init(this);

            playerEntityName = map.getPlayerEntityType();
            main = (MCCharacter) entityFact.build(this, map, playerEntityName, "player");
            main.setPosition(map.getPlayerSpawnPos().toGdxVect());
            camManager.setFollowTarget(main);
            entityManager.addEntity(main);
            entityManager.addAllEntities(map.spawnEntities(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        entityManager.playGlobalAnimation("idle");

        stateManager.addState(new MCGSCombat(this));
        stateManager.addState(new MCGSExploration(this));
        stateManager.setCurrentState("combat", new MCGSCombat.CombatStateArgs());

        try {
            setScreen(new MCGameScreen(this));
        } catch (DataException e) {
            e.printStackTrace();
        }
    }

    private void logic(float delta) {
        // We don't give a fuck about logic
        // Because we implement MVC (Modular Venomous Contraception) // co autored by mathuww

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

    public MCStateMachine getStateManager() {
        return this.stateManager;
    }

    public MCTerrainMap getTerrainMap() {
        return this.map;
    }

    public MCEntity getFocusedEntity() {
        return focusedEntity;
    }

    public boolean isWalkable(MCIntVector2 pos) {
        if (entityManager.getEntityFromTile(1, pos) == null) {
            return map.isWalkable(pos);
        }
        return false;
    }

    public void changeFocus(MCEntity e) {
        if (focusedEntity != null) {
            System.out.println("Yey");
            if (focusedEntity.loseFocus()) {
                focusedEntity = e;
                if (e != null) {
                    e.getFocus();
                    System.out.println("Haha");
                }
            }
        } else {
            System.out.println("AAAAAAAAA");
            focusedEntity = e;
            if (e != null) e.getFocus();
        }
    }
    
}