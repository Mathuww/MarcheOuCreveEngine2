package com.walk.or.die.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.entities.MCAttackFactory;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.entities.MCEntityFactory;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.entities.MCExplorationPlayer;
import com.walk.or.die.engine.exceptions.InvalidDataException;
import com.walk.or.die.engine.exceptions.MissingDataException;
import com.walk.or.die.engine.exceptions.UnexistingBehaviorException;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.input.MCInputManager.NextMapCommand;
import com.walk.or.die.engine.input.MCInputManager.PreviousMapCommand;
import com.walk.or.die.engine.screens.MCGameScreen;
import com.walk.or.die.engine.screens.MCMainMenuScreen;
import com.walk.or.die.engine.shared.MCDebugRenderer;
import com.walk.or.die.engine.shared.MCEmpty;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.game.MCGameState;
import com.walk.or.die.engine.sm.game.states.MCGSAlliesPlaying;
import com.walk.or.die.engine.sm.game.states.MCGSEnemiesPlaying;
import com.walk.or.die.engine.sm.game.states.MCGSExploration;
import com.walk.or.die.engine.sm.game.states.MCGSVeryBigInformation;
import com.walk.or.die.engine.tiledmap.MCPathfinder;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;
import com.walk.or.die.engine.ui.MCHUDManager;
import com.walk.or.die.engine.vehicles.MCVehicle;

/**
 * Implements the {@link com.badlogic.gdx.ApplicationListener} shared by all
 * platforms. This is the main class of the game, which manages the whole game.
 */
public class MCGame extends Game {
    public static enum CombatDoneArgs {
        ALLIES_WON,
        ENEMIES_WON
    }

    /**
     * Width of the viewport.
     */
    public static final int VIEWPORT_WIDTH = 16;
    /**
     * Height of the viewport.
     */
    public static final int VIEWPORT_HEIGHT = 12;
    /**
     * Describes how far the camera can exceed the map lower boundaries.
    */
    public static final Vector2 CAM_LOWER_LIMIT_OFFSET = new Vector2(0f, -4f);
    /**
     * Describes how far the camera can exceed the map upper boundaries.
    */
    public static final Vector2 CAM_UPPER_LIMIT_OFFSET = new Vector2(0f, 1f); 

    /** 
     * Sets the fixed HUD viewport height.
    */
    public final static int WINDOW_DEFAULT_HEIGHT = 480;
      /** 
     * Sets the fixed HUD viewport width.
    */
    public final static int WINDOW_DEFAULT_WIDTH = 
        MathUtils.round((float)WINDOW_DEFAULT_HEIGHT * ((float)VIEWPORT_WIDTH / (float)VIEWPORT_HEIGHT));

    /**
     * The current map being played on.
     * @see MCTerrainMap
     */
    private MCTerrainMap map;
    private Map<String, Boolean> mapsStates = new HashMap<>();
    private int mapIndex = 1;
    /**
     * The pathfinder singleton.
     * @see MCPathfinder
     */
    private MCPathfinder pathfinder = MCPathfinder.get();
    /**
     * Another HR manager who treats his assets as disposable and replaceable resources.
     * @see AssetManager
     */
    private AssetManager drh = new AssetManager();
    /**
     * The path where all tiled files are stored.
     */
    public static final String TILED_ROOT = "tiled/packed/";
    /**
     * The path where all tiled maps are stored.
     */
    private final String MAP_ROOT = TILED_ROOT + "maps/";
    /**
     * The path where all fonts are stored.
     */
    private final String FONT_ROOT = "fonts/";

    /**
     * The MCEntityFactory singleton.
     * @see MCEntityFactory
     */
    private final MCEntityFactory entityFact = MCEntityFactory.get();
    /**
     * The MCAttackFactory singleton.
     * @see MCAttackFactory
     */
    private final MCAttackFactory attackFact = MCAttackFactory.get();
    /**
     * The MCSharedAssets singleton.
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
     * The unpredictable EventBus, which rides your fears straight towards the wall.
     * @see MCEventBus
     * @see MCVehicle
     */
    private final MCEventBus bus = MCEventBus.get();

    /**
     * The CameraManager singleton.
     * @see MCCameraManager
     */
    private final MCCameraManager camManager = MCCameraManager.get();

    /**
     * The InputManager singleton.
     * @see MCInputManager
     */
    private MCInputManager inputHandler = MCInputManager.get();

    /**
     * The EntityManager singleton.
     * @see MCEntityManager
     */
    private MCEntityManager entityManager = MCEntityManager.get();
    /**
     * The current focused character, or null if there is no focused character.
     * @see MCCharacter
     */
    private MCCharacter focusedCharacter;

    /**
     * The debug singleton.
     * @see MCDebugRenderer
     */
    private MCDebugRenderer debugRenderer = MCDebugRenderer.get();

    /**
     * The sprite batch used for rendering.
     * @see SpriteBatch
     */
    public SpriteBatch batch;
    /**
     * The game viewport.
     * @see FitViewport
     */
    public FitViewport gameViewport;

    /**
     * The HUD manager singleton.
     */
    private MCHUDManager hudManager = MCHUDManager.get();

    private String currentMapFile;
    private String mapFileToLoad = null;
    private int destIDPortalToLoad = 0;


    private Screen currentScreen;
    private boolean paused = false;

    /**
     * Constructs a new MCGame instance.
     */
    public MCGame() {}

    /**
     * Gets the root map.
     * @return The root map.
     */
    public String getRootMap() {
        return MAP_ROOT;
    }

    public String getMapFileToLoad() {
        return mapFileToLoad;
    }

    public void setMapFileToLoad(String filename) {
        mapFileToLoad = filename;
    }

    public int getDestIDPortalToLoad() {
        return destIDPortalToLoad;
    }

    public void setDestIDPortalToLoad(int destID) {
        destIDPortalToLoad = destID;
    }

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

        // Cam manager init
        camManager.setLowerLimit(CAM_LOWER_LIMIT_OFFSET);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        hudManager.init(WINDOW_DEFAULT_WIDTH, WINDOW_DEFAULT_HEIGHT);

        // State init
        stateManager.addState(new MCGSAlliesPlaying(this));
        stateManager.addState(new MCGSEnemiesPlaying(this));
        stateManager.addState(new MCGSExploration(this));
        stateManager.addState(new MCGSVeryBigInformation(this));

        loadMap("start.tmx");

        currentScreen = new MCMainMenuScreen(this);
        setScreen(currentScreen);

        bus.on(this, "MainMenu", this::goToMainMenu);
        bus.on(this, "Pause", this::pauseGame);
        bus.on(this, "Resume", this::resumeGame);
        bus.on(this, "PlayFromMainMenu", this::playFromMainMenu);
        bus.on(this, "Quit", this::quit);
    }

    /**
     * Triggers a clean transition with the map name (tmx file).
     * @param filename The name of the map file.
     */
    public void teleportationActivate (String filename, int destID) {
        setMapFileToLoad(filename);
        setDestIDPortalToLoad(destID);
    }

    public void reloadMap() {
        setMapFileToLoad(currentMapFile);
    }

    /**
     * Loads and cleans a map with the map name (tmx file).
     * @param filename The name of the map file.
     * @throws IllegalStateException If the map file does not exist.
     */
    private void loadMap(String filename) throws IllegalStateException {
        FileHandle mapFile = Gdx.files.internal(MAP_ROOT + filename);
        if (!mapFile.exists()) {
            throw new IllegalStateException("map " + filename + " trying to be loaded but doesn't exist.");
        }
        currentMapFile = filename;
        System.out.println("teleportation to " + filename);
        hudManager.getCharacterHud().hide();
        MCExplorationPlayer chosen = entityManager.getExplorationPlayer();
        entityManager.clearEntities();

        if (map != null)
            map.dispose(); 
        map = new MCTerrainMap(MAP_ROOT + filename, drh);
        MapProperties mapProps = map.getProperties();
        Boolean mapCombat = mapProps.get("battleMap", Boolean.class);
        if(mapCombat == null) {
            mapCombat = false;
        }
        
        camManager.setUpperLimit(new Vector2(
            CAM_UPPER_LIMIT_OFFSET.x + map.getWidth(),
            CAM_UPPER_LIMIT_OFFSET.y + map.getHeight()
        ));

        MCExplorationPlayer newPlayer = null;
        MCExplorationPlayer newPlayerInTheNewMap = null;

        if (mapCombat) {
            try {
                entityManager.addAllEntities(map.spawnEntities(this));
            } catch (Exception e) {
                e.printStackTrace();
            }
            entityManager.update(Gdx.graphics.getDeltaTime());
            stateManager.setCurrentState("AlliesPlaying", new MCGSAlliesPlaying.AlliesPlayingArgs());
        } else {
            try {
                newPlayer = entityManager.addExplorationEntities(map.spawnEntities(this), chosen);
                //System.out.println("Player trouvé");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                newPlayerInTheNewMap = new MCExplorationPlayer(newPlayer, map);
                entityManager.spawnExplorationPlayerWithPortal(map.spawnEntities(this), getDestIDPortalToLoad(), newPlayerInTheNewMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            entityManager.update(Gdx.graphics.getDeltaTime());
            stateManager.setCurrentState("Exploration", new MCGSExploration.ExplStateArgs());
        }
        hudManager.getCharacterHud().hide();
    }

    /**
     * Handles the global logic of the game, triggered each frame.
     * @param delta The time elapsed since the last frame.
     */
    private void logic(float delta) {
        // We don't give a fuck about logic
        // Because we implement MVC (Modular Venomous Contraception) // co autored by
        // mathuww

        if (currentScreen instanceof MCMainMenuScreen) {
            hudManager.updateMainMenu(delta);
            return;
        }

        // la condition pour que le changement de map se fait correctement par portail
        if(mapFileToLoad != null) {
            loadMap(getMapFileToLoad());
            setMapFileToLoad(null);
            setDestIDPortalToLoad(0);
        }

        try {
            camManager.update(delta);
        } catch (UnexistingBehaviorException e) {
            e.printStackTrace();
        }

        // si la camera bouge, la tile visée a bougé!
        if (camManager.hasMovedThisFrame())
            inputHandler.triggerMouseUpdate();

        stateManager.update(delta);
        entityManager.update(delta);
        hudManager.update(delta);
    }

    @Override
    public void render() {
        // on va pas render à < 10 FPS non plus
        // (jai essayé de mettre à delta à 0.25 c'était pas beau à voir)
        // la caméra était partie rejoindre la Lune ou on a jamais mis les pieds.
        float delta = Math.min(Gdx.graphics.getDeltaTime(), 0.1f);
        logic(delta);
        super.render();
    }

    @Override
    public void dispose() {
        if (map != null)
            map.dispose();
    }

    /**
     * Changes the focused character.
     * @param c The new focused character (can be null).
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

    public boolean isPaused() {
        return paused;
    }

    public void pauseGame(MCEmpty e) {
        paused = true;
        bus.emit("freezeGame");
        hudManager.getCharacterHud().hide();
        hudManager.getPauseHud().setDisplay(true);
    }

    public void playFromMainMenu(MCEmpty emp) {
        try {  
            currentScreen = new MCGameScreen(this);
        } catch (InvalidDataException e) {
            e.printStackTrace();
            return;
        }
        setScreen(currentScreen);
    }

    public void quit(MCEmpty e) {
        //System.out.print("quitting, please wait(forever.)");
        Gdx.app.exit();
    }

    public void goToMainMenu(MCEmpty e) {
        //System.out.println("going straight to main menu, please wait (forever.)");
        currentScreen = new MCMainMenuScreen(this);
        setScreen(currentScreen);
    }

    public void resumeGame(MCEmpty e) {
        paused = false;
        bus.emit("unfreezeGame");
        hudManager.getPauseHud().setDisplay(false);
    }

    public Screen getCurrentScreen() {
        return currentScreen;
    }

    /**
     * Gets the state manager.
     * @return The state manager.
     */
    public MCStateMachine getStateManager() {
        return this.stateManager;
    }

    /**
     * Gets the terrain map.
     * @return The terrain map.
     */
    public MCTerrainMap getTerrainMap() {
        return this.map;
    }

    /**
     * Determines if a case is walkable.
     * @param pos The position of the tile.
     * @return True if the case is walkable.
     */
    public boolean isWalkable(MCIntVector2 pos) {
        if (entityManager.getEntityFromTile(1, pos) == null) {
            return map.isWalkable(pos);
        }
        return false;
    }
}