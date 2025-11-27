package com.walk.or.die.engine.shared;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.walk.or.die.engine.exceptions.DataException;
import com.walk.or.die.engine.tiledmap.MCMap;
import com.walk.or.die.engine.tiledmap.MCMapLayer;
import com.walk.or.die.engine.tiledmap.MCMapObject;

public class MCSharedAssets {
    private static MCSharedAssets instance = null;

    public static MCSharedAssets get() {
        if (instance == null) instance = new MCSharedAssets();
        return instance;
    }

    private MCMapLayer miscTilesLayer;
    private Map<String, TiledMapTile> savedTiles = new HashMap<>();

    private MCSharedAssets() {}

    public void init(String miscMapPath, AssetManager drh) throws Exception {
        MCMap miscTilesMap = new MCMap(miscMapPath, drh);
        miscTilesLayer = miscTilesMap.getLayer(0);

        addSavedTile("validAttackTile");
    }

    private void addSavedTile(String nameVal) throws Exception {
        TiledMapTileLayer.Cell cell = miscTilesLayer.getCellByProperty("name", nameVal);
        if (cell == null)
            throw new DataException("cant find tile named " + nameVal + " in shared assets map (" + nameVal + ")");
        TiledMapTile tile = cell.getTile();
        if (tile == null)
            throw new IllegalStateException("cant convert cell with tile " + nameVal + " to a tile in shared assets");
        savedTiles.put(nameVal, tile);
    }

    public TiledMapTile getSavedTile(String name) throws Exception {
        TiledMapTile tile = savedTiles.get(name);
        if (tile == null)
            throw new IllegalArgumentException("cant find " + name + " in saved tiles");
        return tile;
    }

    public TextureRegion getSavedTexture(String name) throws Exception {
        TiledMapTile tile = getSavedTile(name);
        TextureRegion texture = tile.getTextureRegion();
        if (texture == null)
            throw new IllegalStateException("cant find texture region of tile " + name);
        return texture;
    }
}
