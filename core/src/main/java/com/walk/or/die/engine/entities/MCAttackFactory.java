package com.walk.or.die.engine.entities;

import java.awt.Point;
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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.walk.or.die.engine.exceptions.DataException;
import com.walk.or.die.engine.exceptions.MissingDataException;
import com.walk.or.die.engine.shared.MCUtils;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;
import com.walk.or.die.engine.tiledmap.MCMap;
import com.walk.or.die.engine.tiledmap.MCMapLayer;

public class MCAttackFactory {
    private static MCAttackFactory instance = null;

    public static MCAttackFactory get() {
        if (instance == null) instance = new MCAttackFactory();
        return instance;
    }

    private final String ATTACK_ROOT = "tiled/packed/attacks/";
    private AssetManager assetManager;
    private Map<String, String> possibleAttacks;
    private Map<String, MCMap> mapCache;

    private MCAttackFactory() {
        possibleAttacks = new HashMap<>();
        mapCache = new HashMap<>();
    }

    public void init(AssetManager assetManager) throws DataException {
        this.assetManager = assetManager;

        // on recupere la liste de toutes les entités possibles
        for (FileHandle attackFile : MCUtils.listFilesByExt(ATTACK_ROOT, "tmx")) {
            String attackName = attackFile.nameWithoutExtension();
            if (possibleAttacks.containsKey(attackName)) {
                throw new DataException("duplicate attack tmx files exist for attack name " + attackName);
            }
            MCMap attackMap = new MCMap(attackFile.path(), assetManager);
            mapCache.put(attackName, attackMap);
        }
    }

    public MCAttack build(MCEntity parent, String attackName) throws Exception {
        if (assetManager == null) 
            throw new IllegalStateException("must init attack factory before using it");

        MCMap attackMap = mapCache.get(attackName);
        if (attackMap == null)
            throw new IllegalStateException("cant find " + attackName + " in entity factory map cache");

        MapProperties attackProperties = attackMap.getProperties();

        int power = MCUtils.getIntProperty(attackProperties, "power", 1);

        Map<Point, Float> damagePattern = new HashMap<>();

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
                    if (tileType != null && tileType.equals("attackDamage")) {
                        // on calcule sa position relative à l'envoyeur
                        Vector2 relativePos = new Vector2(x, y).sub(senderPos.cpy());

                        float decreaseFactor = MCUtils.getFloatProperty(tileProps, "decreaseFactor", 0f);
                        float damageAtPos = 1 * (1 - decreaseFactor);

                        damagePattern.put(new Point(MathUtils.floor(relativePos.x), MathUtils.floor(relativePos.y)), damageAtPos);
                    }
                }
            }
        }

        MCAttack attack = new MCAttack(parent, power, damagePattern);
        attack.initFromProperties(attackProperties);
        //System.out.println(attack.toString());

        return attack;
    }
    
}
