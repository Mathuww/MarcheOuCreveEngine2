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
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.MCUtils;
import com.walk.or.die.engine.exceptions.DataException;
import com.walk.or.die.engine.exceptions.MissingDataException;
import com.walk.or.die.engine.screens.MCGameScreen;
import com.walk.or.die.engine.tiledmap.MCGameMap;
import com.walk.or.die.engine.tiledmap.MCMap;
import com.walk.or.die.engine.tiledmap.MCMapLayer;

public class MCAttackFactory {
    private static MCAttackFactory instance = null;

    public MCAttackFactory get() {
        if (instance == null) instance = new MCAttackFactory();
        return instance;
    }

    private AssetManager assetManager;
    private Map<String, String> possibleAttacks;
    private Map<String, MCEntity.Attack> builtAttacks;

    private MCAttackFactory() {
        possibleAttacks = new HashMap<>();
        builtAttacks = new HashMap<>();
    }

    public void init(AssetManager assetManager) throws DataException {
        this.assetManager = assetManager;

        // on recupere la liste de toutes les entités possibles
        FileHandle attackFolder = Gdx.files.internal("attacks");
        for (FileHandle attackFile : MCUtils.listFilesByExt(attackFolder, "tmx")) {
            String attackName = attackFile.nameWithoutExtension();
            if (possibleAttacks.containsKey(attackName)) {
                throw new DataException("duplicate attack tmx files exist for attack name " + attackName);
            }
            possibleAttacks.put(attackName, attackFile.path());
        }
    }

    public void build(String attackName) throws Exception {
        if (assetManager == null) 
            throw new IllegalStateException("must init attack factory before using it");

        String attackPath = possibleAttacks.get(attackName);
        if (attackPath == null)
            throw new MissingDataException("cant build attack with name " + attackName + " because no tmx file exists associated with it");
        

        MCMap attackMap = new MCMap(attackPath, assetManager);

        MapProperties attackProperties = attackMap.getProperties();
        Iterator<String> attackPropertiesKeys = attackProperties.getKeys();
        while (attackPropertiesKeys.hasNext()) {
            String key = attackPropertiesKeys.next();
            Object value = attackProperties.get(key);
            //attack.setProperty(key, value);
        }

        Map<Vector2, Float> damagePattern;

        MCMapLayer layer = attackMap.getLayer(0);
        Vector2 senderPos = layer.getPosByProperty("type", "sender");
        if (senderPos == null) {
            throw new DataException("cant build attack " + attackName + " : no sender tile in attack map");

                }

        List<List<TiledMapTile>> tiles = attackMap.getLayer(0).splitInTiles();
        for (List<TiledMapTile> row : tiles) {
            for (TiledMapTile tile : row) {
                
            }
        }

//        //builtAttacks.put(attackName, attack);
        //return attack;
    }
}
