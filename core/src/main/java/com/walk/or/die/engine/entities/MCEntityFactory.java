package com.walk.or.die.engine.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.particles.ParticleShader.AlignMode;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.exceptions.DataException;
import com.walk.or.die.engine.exceptions.MissingDataException;
import com.walk.or.die.engine.shared.MCUtils;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;
import com.walk.or.die.engine.tiledmap.MCMap;

public class MCEntityFactory {
    private static MCEntityFactory instance = null;

    public static MCEntityFactory get() {
        if (instance == null) instance = new MCEntityFactory();
        return instance;
    }

    private final String ENTITY_ROOT = "tiled/packed/entities_anims/";
    private AssetManager assetManager;
    private Map<String, Class<? extends MCEntity>> entityTypes;
    private Map<String, String> possibleEntities;
    private Map<String, MCMap> mapCache;

    private MCEntityFactory() {
        entityTypes = new HashMap<>();
        possibleEntities = new HashMap<>();
        mapCache = new HashMap<>();
    }

    public void init(AssetManager assetManager) throws DataException {
        this.assetManager = assetManager;

        entityTypes.put("character", MCCharacter.class);
        entityTypes.put("ally", MCAlly.class);

        // on recupere la liste de toutes les entités possibles
        for (FileHandle entityFile : MCUtils.listFilesByExt(ENTITY_ROOT, "tmx")) {
            String entityGenericName = entityFile.nameWithoutExtension();
            System.out.println("adding : " + entityGenericName);
            if (possibleEntities.containsKey(entityGenericName)) {
                throw new DataException("duplicate entity tmx files exist for entity name " + entityGenericName);
            }
            MCMap entityMap = new MCMap(entityFile.path(), assetManager);
            mapCache.put(entityGenericName, entityMap);
        }
    }

    public MCEntity build(MCGame parentScreen, MCTerrainMap parentMap, String entityGenericName, String entityId) throws Exception {  
        if (assetManager == null) 
            throw new IllegalStateException("must init entity factory before using it");

        MCMap entityMap = mapCache.get(entityGenericName);
        if (entityMap == null)
            throw new IllegalStateException("cant find " + entityGenericName + " in entity factory map cache");
        
        String typeStr = entityMap.getProperty("type");
        if (typeStr == null) 
            throw new DataException("cant decide of type of entity " + entityGenericName + " because its type is not filled in map properties");
        Class<? extends MCEntity> clazz = entityTypes.get(typeStr);
        if (clazz == null)
            throw new IllegalStateException("cannot find type " + typeStr + " in entity types map in factory");

        // Partie 1 : constuire l'instance
        // le bordel pour en arrive la déjà ...
        // ca au passage ca peut générer 4 exceptions différentes ptdr
        MCEntity entity = clazz
            .getDeclaredConstructor(MCGame.class, MCTerrainMap.class, String.class)
            .newInstance(parentScreen, parentMap, entityId);

        // Partie 2 : transmettre les propriétés éventuelles
        // ca javoue c'est l'entité qui gere mdr
        // vu qu'on sait pas desquelles l'entité a besoin
        entity.initFromProperties(entityMap.getProperties());

        // Partie 3 : construire les animations (lignes dans la map)
        // (ces maps la ont une seule layer)
        Array<Array<TiledMapTile>> tiles = entityMap.getLayer(0).splitInTiles();

        /*
        System.out.println("firstRow size = " + tiles.get(0).size);
        //System.out.println("firstTile = " + tiles.get(0));
        TiledMapTileLayer tml = (TiledMapTileLayer) entityMap.getLayer(0).getRawLayer();
        TiledMapTileLayer.Cell cell = ((TiledMapTileLayer.Cell) (tml.getCell(0,0)));
        System.out.println("cell = " + cell);
        System.out.println("tile = " + (cell != null ? cell.getTile() : "null"));
        */

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
            Array<TextureRegion> animRegions = new Array<>();

            for (TiledMapTile tile : row) {
                if (tile != null) {
                    if (tile.getTextureRegion() != null) 
                        animRegions.add(tile.getTextureRegion());

                    String triggerStr = tile.getProperties().get("trigger", String.class);
                    if (triggerStr != null) {
                        // ca on gerera apres
                    }
                }

            }

            Animation<TextureRegion> rawAnim = new Animation<>(frameDuration, animRegions, playMode);
            anim.setRawAnim(rawAnim);

            entity.addAnimation(animName, anim);
        }

        return entity;
    }
}
