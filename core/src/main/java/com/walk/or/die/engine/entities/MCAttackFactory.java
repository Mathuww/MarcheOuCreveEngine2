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
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.walk.or.die.engine.MCUtils;
import com.walk.or.die.engine.entities.attacks.MCGenericAttack;
import com.walk.or.die.engine.exceptions.DataException;
import com.walk.or.die.engine.exceptions.MissingDataException;
import com.walk.or.die.engine.screens.MCGameScreen;
import com.walk.or.die.engine.tiledmap.MCGameMap;
import com.walk.or.die.engine.tiledmap.MCMap;
import com.walk.or.die.engine.tiledmap.MCMapLayer;

public class MCAttackFactory {
    private static MCAttackFactory instance = null;

    public static MCAttackFactory get() {
        if (instance == null) instance = new MCAttackFactory();
        return instance;
    }

    private AssetManager assetManager;
    private Map<String, Class<? extends MCEntity.Attack>> attackTypes;
    private Map<String, String> possibleAttacks;
    private Map<String, MCMap> mapCache;

    private MCAttackFactory() {
        possibleAttacks = new HashMap<>();
        mapCache = new HashMap<>();
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

        // faudra déclarer les classes des attaques possibles et nom associé 
        // dans la propriété "type" de l'attaque icci
        attackTypes.put("generic", MCGenericAttack.class);
    }

    public MCEntity.Attack build(MCEntity parent, String attackName) throws Exception {
        if (assetManager == null) 
            throw new IllegalStateException("must init attack factory before using it");

        String attackPath = possibleAttacks.get(attackName);
        if (attackPath == null)
            throw new MissingDataException("cant build attack with name " + attackName + " because no tmx file exists associated with it");
        
        MCMap attackMap;
        if (mapCache.containsKey(attackPath))
            attackMap = mapCache.get(attackPath);
        else {
            attackMap = new MCMap(attackPath, assetManager);
            mapCache.put(attackPath, attackMap);
        }

        MapProperties attackProperties = attackMap.getProperties();

        String typeStr = attackProperties.get("attackType", String.class);
        if (typeStr == null) 
            throw new DataException("cant decide of type of attack " + attackName + " because its type is not filled in map properties");
        Class<? extends MCEntity.Attack> clazz = attackTypes.get(typeStr);
        if (clazz == null)
            throw new IllegalStateException("cannot find type " + typeStr + " in entity types map in factory");

        Integer power = attackProperties.get("power", Integer.class);
        if (power == null) 
            throw new DataException("missing power in attack map " + attackName);

        Map<Vector2, Float> damagePattern = new HashMap<>();

        MCMapLayer layer = attackMap.getLayer(0);
        Vector2 senderPos = layer.getPosByProperty("type", "sender");
        if (senderPos == null)
            throw new DataException("cant build attack " + attackName + " : no sender tile in attack map");


        if (!(layer.getRawLayer() instanceof TiledMapTileLayer))
            throw new IllegalStateException("the MapLayer in attack Map layer 0 is not a TiledMapTileLayer");
        TiledMapTileLayer rawLayer = (TiledMapTileLayer) layer.getRawLayer();

        for (int y = 0; y < rawLayer.getHeight(); y++) {
            Array<TiledMapTile> row = new Array<>();
            for (int x = 0; x < rawLayer.getWidth(); x++) {
                TiledMapTileLayer.Cell cell = rawLayer.getCell(x, y);
                if (cell != null && cell.getTile() != null) {
                    TiledMapTile tile = cell.getTile();
                    MapProperties tileProps = tile.getProperties();
                    String tileType = tileProps.get("type", String.class);
                    if (tileType.equals("attackDamage")) {
                        // on calcule sa position relative à l'envoyeur
                        Vector2 relativePos = senderPos.cpy().sub(new Vector2(x, y));
                        float damageAtPos = 1;

                        Float decreaseFactor = tileProps.get("decreaseFactor", Float.class);
                        if (decreaseFactor != null) {
                            damageAtPos = 1 * (1 - decreaseFactor);
                        }

                        damagePattern.put(relativePos, damageAtPos);
                    }
                }
            }
        }

        MCEntity.Attack attack = clazz
            .getDeclaredConstructor(MCEntity.class, Integer.class, HashMap.class)
            .newInstance(parent, power, damagePattern);
        attack.initFromProperties(attackProperties);

        return attack;
    }
}
