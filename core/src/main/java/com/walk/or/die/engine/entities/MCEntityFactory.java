package com.walk.or.die.engine.entities;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.utils.Array;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.exceptions.InvalidDataException;
import com.walk.or.die.engine.shared.MCUtils;
import com.walk.or.die.engine.tiledmap.MCMap;
import com.walk.or.die.engine.tiledmap.MCMapObject;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;

/**
 * A singleton which creates entities, based on tiled data.
 */
public class MCEntityFactory {
    /** The singleton instance of MCEntityFactory. */
    private static MCEntityFactory instance = null;

    /**
     * Gets the instance.
     * @return The instance.
     */
    public static MCEntityFactory get() {
        if (instance == null) instance = new MCEntityFactory();
        return instance;
    }

    /** The root path for entity animations. */
    private final String ENTITY_ROOT = "tiled/packed/entities_anims/";
    /** The asset manager for loading resources. */
    private AssetManager assetManager;
    /** A map of entity type strings to their corresponding classes. */
    private Map<String, Class<? extends MCEntity>> entityTypes;
    /** Stores the generic names of entities found during initialization to detect duplicates. */
    private Map<String, String> possibleEntities;
    /** A cache of entity maps, keyed by their generic names. */
    private Map<String, MCMap> mapCache;

    /**
     * Constructs a new MCEntityFactory instance.
     */
    private MCEntityFactory() {
        entityTypes = new HashMap<>();
        possibleEntities = new HashMap<>();
        mapCache = new HashMap<>();
    }

    /**
     * Initializes the singleton.
     * @param assetManager The asset manager.
     * @throws InvalidDataException If the data is invalid.
     */
    public void init(AssetManager assetManager) throws InvalidDataException {
        this.assetManager = assetManager;

        entityTypes.put("character", MCCharacter.class);
        entityTypes.put("ally", MCAlly.class);
        entityTypes.put("enemy", MCEnemy.class);
        entityTypes.put("explorationPlayer", MCExplorationPlayer.class);
        entityTypes.put("projectile", MCProjectile.class);
        entityTypes.put("portal", MCPortal.class);

        //System.out.println("INIIIIIIIIIIIIIIIIT COME INUITTTTTTTTTTTTT");
        // on recupere la liste de toutes les entités possibles
        for (FileHandle entityFile : MCUtils.listFilesByExt(ENTITY_ROOT, " hdoihv soh spichq zhgepd")) {
            String entityGenericName = entityFile.nameWithoutExtension();
            //System.out.println(entityGenericName);
            //System.out.println("adding : " + entityGenericName);
            if (possibleEntities.containsKey(entityGenericName)) {
                throw new InvalidDataException("duplicate entity tmx files exist for entity name " + entityGenericName);
            }
            MCMap entityMap = new MCMap(entityFile.path(), assetManager);
            mapCache.put(entityGenericName, entityMap);
        }
    }

    /**
     * Builds a new instance of an entity.
     * @param parentScreen The parent screen.
     * @param parentMap The parent map.
     * @param entityGenericName The entity generic name.
     * @param entityId The entity ID.
     * @param props The map properties.
     * @return The built entity.
     * @throws IllegalStateException If the state is illegal.
     * @throws InvalidDataException If the data is invalid.
     * @throws Exception If a general error occurs during entity creation.
     */
    public MCEntity build(MCGame parentScreen, MCTerrainMap parentMap, String entityGenericName, String entityId, MapProperties props) throws Exception {
        if (assetManager == null) 
            throw new IllegalStateException("must init entity factory before using it");

        MCMap entityMap = mapCache.get(entityGenericName);
        if (entityMap == null)
            throw new IllegalStateException("cant find " + entityGenericName + " in entity factory map cache");
        
        String typeStr = entityMap.getProperty("type");
        if (typeStr == null) 
            throw new InvalidDataException("cant decide of type of entity " + entityGenericName + " because its type is not filled in map properties");
        Class<? extends MCEntity> clazz = entityTypes.get(typeStr);
        if (clazz == null)
            throw new IllegalStateException("cannot find type " + typeStr + " in entity types map in factory");

        // Partie 1 : constuire l'instance
        // le bordel pour en arrive la déjà ...
        // ca au passage ca peut générer 4 exceptions différentes ptdr
        final MCEntity entity;
        try {
            entity = clazz
                    .getDeclaredConstructor(MCGame.class, MCTerrainMap.class, String.class)
                    .newInstance(parentScreen, parentMap, entityId);
            entity.initFromMapProperties(props);
        } catch (InvocationTargetException  e) {
            e.printStackTrace();
            if (e.getCause() != null) e.getCause().printStackTrace();
            throw new IllegalStateException("cannot build entity " + entityGenericName + " of type " + typeStr + " because invoking constructor failed");
        }
        

        // Partie 2 : transmettre les propriétés éventuelles
        // ca javoue c'est l'entité qui gere mdr
        // vu qu'on sait pas desquelles l'entité a besoin
        entity.initFromProperties(entityMap.getProperties());

        // Partie 3 : construire les animations (lignes dans la map)
        // (ces maps la ont une seule layer)
        Array<Array<TiledMapTile>> tiles = entityMap.getLayer(0).splitInTiles();

        for (Array<TiledMapTile> row : tiles) {
            TiledMapTile firstTile = row.get(0);
            if (firstTile == null)
                continue;
            String animName = firstTile.getProperties().get("animName", String.class);
            String playModeStr = firstTile.getProperties().get("playMode", String.class);
            int fps = MCUtils.getIntProperty(firstTile.getProperties(), "fps", 15);
            float frameDuration = 1f / (float)fps;

            Animation.PlayMode playMode = Animation.PlayMode.NORMAL;
            if (playModeStr != null) {
                try {
                    playMode = Animation.PlayMode.valueOf(playModeStr);
                } catch (IllegalArgumentException e) {
                    playMode = Animation.PlayMode.LOOP;
                }
            }

            MCAnimation anim = new MCAnimation();
            Array<TextureRegion> animRegions = new Array<TextureRegion>();

            for (TiledMapTile tile : row) {
                int i = 0;
                if (tile != null) {
                    if (tile.getTextureRegion() != null) {
                        TextureRegion frame = new TextureRegion(tile.getTextureRegion());
                        animRegions.add(frame);
                    }
                }
                i++;
            }

            Animation<TextureRegion> rawAnim = new Animation<>(frameDuration, animRegions, playMode);
            anim.setRawAnim(rawAnim);

            entity.addAnimation(animName, anim);
        }

        List<String> addedAnims = new ArrayList<>(entity.getAnimationNames());
        for (String animName : addedAnims) {
            //System.out.println("checking if " + animName);
            if (animName.endsWith("_right")) {
                String base = animName.substring(0, animName.length() - "_right".length());
                String leftName = base + "_left";

                if (!entity.hasAnimation(leftName)) {
                    //System.out.println("flipping : " + leftName);
                    MCAnimation rightAnim = entity.getAnimation(animName);
                    MCAnimation leftAnim = rightAnim.getFlippedAnim();
                    entity.addAnimation(leftName, leftAnim);
                }
            }

            if (animName.endsWith("_left")) {
                String base = animName.substring(0, animName.length() - "_left".length());
                String rightName = base + "_right";

                if (!entity.hasAnimation(rightName)) {
                    MCAnimation leftAnim = entity.getAnimation(animName);
                    MCAnimation rightAnim = leftAnim.getFlippedAnim();
                    entity.addAnimation(rightName, rightAnim);
                }
            }
        }

        entity.onSpawn();
        return entity;
    }

    /**
     * Disposes of the map cache.
     */
    public void dispose() {
        for (MCMap m : mapCache.values())
            m.dispose();
    }
}