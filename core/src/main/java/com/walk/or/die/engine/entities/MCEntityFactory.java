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
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.utils.Array;
import com.walk.or.die.engine.MCUtils;
import com.walk.or.die.engine.exceptions.DataException;
import com.walk.or.die.engine.exceptions.MissingDataException;
import com.walk.or.die.engine.screens.MCGameScreen;
import com.walk.or.die.engine.tiledmap.MCGameMap;
import com.walk.or.die.engine.tiledmap.MCMap;

public class MCEntityFactory {
    private static MCEntityFactory instance = null;

    public static MCEntityFactory get() {
        if (instance == null) instance = new MCEntityFactory();
        return instance;
    }

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

        // on recupere la liste de toutes les entités possibles
        FileHandle entityFolder = Gdx.files.internal("entities_anims");
        for (FileHandle entityFile : MCUtils.listFilesByExt(entityFolder, "tmx")) {
            String entityGenericName = entityFile.nameWithoutExtension();
            if (possibleEntities.containsKey(entityGenericName)) {
                throw new DataException("duplicate entity tmx files exist for entity name " + entityGenericName);
            }
            possibleEntities.put(entityGenericName, entityFile.path());
        }
    }

    public MCEntity build(MCGameScreen parentScreen, MCGameMap parentMap, String entityGenericName, String entityId) throws Exception {  
        if (assetManager == null) 
            throw new IllegalStateException("must init entity factory before using it");

        String entityPath = possibleEntities.get(entityGenericName);
        if (entityPath == null)
            throw new MissingDataException("cant build entity with name " + entityGenericName + " because no tmx file exists associated with it");
        
        MCMap entityMap;
        if (mapCache.containsKey(entityPath))
            entityMap = mapCache.get(entityPath);
        else {
            entityMap = new MCMap(entityPath, assetManager);
            mapCache.put(entityPath, entityMap);
        }
        
        String typeStr = entityMap.getProperty("entityType");
        if (typeStr == null) 
            throw new DataException("cant decide of type of entity " + entityGenericName + " because its type is not filled in map properties");
        Class<? extends MCEntity> clazz = entityTypes.get(typeStr);
        if (clazz == null)
            throw new IllegalStateException("cannot find type " + typeStr + " in entity types map in factory");

        // Partie 1 : constuire l'instance
        // le bordel pour en arrive la déjà ...
        // ca au passage ca peut générer 4 exceptions différentes ptdr
        MCEntity entity = clazz
            .getDeclaredConstructor(MCGameScreen.class, MCGameMap.class, String.class)
            .newInstance(parentScreen, parentMap, entityId);

        // Partie 2 : transmettre les propriétés éventuelles
        // ca javoue c'est l'entité qui gere mdr
        // vu qu'on sait pas desquelles l'entité a besoin
        entity.initFromProperties(entityMap.getProperties());

        // Partie 3 : construire les animations (lignes dans la map)
        // (ces maps la ont une seule layer)
        Array<Array<TiledMapTile>> tiles = entityMap.getLayer(0).splitInTiles();
        for (Array<TiledMapTile> row : tiles) {
            TiledMapTile firstTile = row.get(0);
            String animName = firstTile.getProperties().get("animName", String.class);
            String playModeStr = firstTile.getProperties().get("playMode", String.class);
            int fps = firstTile.getProperties().get("fps", Integer.class);
            float frameDuration = 1f / fps;

            Animation.PlayMode playMode = Animation.PlayMode.NORMAL;
            if (playModeStr != null) 
                playMode = Animation.PlayMode.valueOf(playModeStr);

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
