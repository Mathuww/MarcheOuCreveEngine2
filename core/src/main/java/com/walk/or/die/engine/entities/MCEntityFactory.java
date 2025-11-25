package com.walk.or.die.engine.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.walk.or.die.engine.MCUtils;
import com.walk.or.die.engine.exceptions.DataException;
import com.walk.or.die.engine.exceptions.MissingDataException;
import com.walk.or.die.engine.screens.MCGameScreen;
import com.walk.or.die.engine.tiledmap.MCGameMap;
import com.walk.or.die.engine.tiledmap.MCMap;

public class MCEntityFactory {
    private static MCEntityFactory instance = null;

    public MCEntityFactory get() {
        if (instance == null) instance = new MCEntityFactory();
        return instance;
    }

    private AssetManager assetManager;
    private Map<String, Class<? extends MCEntity>> entityTypes;
    private Map<String, String> possibleEntities;
    private Map<String, MCEntity> builtEntities;

    private MCEntityFactory() {
        entityTypes = new HashMap<>();
        possibleEntities = new HashMap<>();
        builtEntities = new HashMap<>();
    }

    public void init(AssetManager assetManager) throws DataException {
        this.assetManager = assetManager;

        entityTypes.put("character", MCCharacter.class);

        // on recupere la liste de toutes les entités possibles
        FileHandle entityFolder = Gdx.files.internal("entities_anims");
        for (FileHandle entityFile : MCUtils.listFilesByExt(entityFolder, "tmx")) {
            String entityName = entityFile.nameWithoutExtension();
            if (possibleEntities.containsKey(entityName)) {
                throw new DataException("duplicate entity tmx files exist for entity name " + entityName);
            }
            possibleEntities.put(entityName, entityFile.path());
        }
    }

    public MCEntity build(MCGameScreen parentScreen, MCGameMap parentMap, String entityName) throws Exception {
        if (assetManager == null) 
            throw new IllegalStateException("must init entity factory before using it");

        String entityPath = possibleEntities.get(entityName);
        if (entityPath == null)
            throw new MissingDataException("cant build entity with name " + entityName + " because no tmx file exists associated with it");
        

        MCMap entityMap = new MCMap(entityPath, assetManager);
        
        String typeStr = entityMap.getProperty("entityType");
        if (typeStr == null) 
            throw new DataException("cant decide of type of entity " + entityName + " because its type is not filled in map properties");
        Class<? extends MCEntity> clazz = entityTypes.get(typeStr);
        if (clazz == null)
            throw new IllegalStateException("cannot find type " + typeStr + " in entity types map in factory");

        // Partie 1 : constuire l'instance
        // le bordel pour en arrive la déjà ...
        // ca au passage ca peut générer 4 exceptions différentes ptdr
        MCEntity entity = clazz
            .getDeclaredConstructor(MCGameScreen.class, MCGameMap.class, String.class)
            .newInstance(parentScreen, parentMap, entityName);

        // Partie 2 : transmettre les propriétés éventuelles
        // ca javoue c'est l'entité qui gere mdr
        MapProperties entityProperties = entityMap.getProperties();
        Iterator<String> entityPropertiesKeys = entityProperties.getKeys();
        while (entityPropertiesKeys.hasNext()) {
            String key = entityPropertiesKeys.next();
            Object value = entityProperties.get(key);
            //entity.setProperty(key, value);
        }

        // Partie 3 : construire les animations (lignes dans la map)
        // (ces maps la ont une seule layer)
        List<List<TiledMapTile>> tiles = entityMap.getLayer(0).splitInTiles();
        for (List<TiledMapTile> row : tiles) {
            TiledMapTile firstTile = row.get(0);
            String animName = firstTile.getProperties().get("animName", String.class);
            //entity.addAnimation(animName, row);
        }

        // comme ca on rebuild pas la meme entity pour le meme nom
        builtEntities.put(entityName, entity);
        return entity;
    }
}
