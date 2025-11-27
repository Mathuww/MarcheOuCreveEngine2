package com.walk.or.die.engine.tiledmap;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.utils.Disposable;
import com.walk.or.die.engine.exceptions.DataException;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.maps.MapProperties;
import java.util.List;

public class MCMap implements Disposable {
    protected TiledMap tiledMap;
    protected float tileSize;
    protected float unitScale;


    public MCMap(String mapPath, AssetManager assetManager) {
        loadMapWithAtlas(mapPath, assetManager);
    }

    protected void loadMapWithAtlas(String mapPath, AssetManager assetManager) {
        // On configure le loader pour Tiled‑map packée
        assetManager.setLoader(TiledMap.class,
            new AtlasTmxMapLoader(new InternalFileHandleResolver()));
        // on charge la map via AssetManager
        assetManager.load(mapPath, TiledMap.class);
        assetManager.finishLoadingAsset(mapPath);
        tiledMap = assetManager.get(mapPath, TiledMap.class);
        // on choisit l'unit scale selon la taille de tile 
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        this.tileSize = layer.getTileWidth();
        this.unitScale = 1 / ((float)this.tileSize);
        System.out.println("tile size : " + this.tileSize + " so unit scale is " + this.unitScale);
    }

    public float getWidth() {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        return layer.getWidth();
    }

    public float getHeight() {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        return layer.getHeight();
    }

    public TiledMap getTiledMap() {
        return this.tiledMap;
    }

    public float getTileSize() {
        return this.tileSize;
    }

    public float getUnitScale() {
        return this.unitScale;
    }

    public Vector2 stickToNearestTile(Vector2 pos) {
        float newX = MathUtils.round(pos.x);
        float newY = MathUtils.round(pos.y);
        //System.out.println("sticking to " + newX + ", " + newY);
        return new Vector2(newX, newY);
    }

    public MapProperties getProperties() {
        return this.tiledMap.getProperties();
    }

    public <T> T getProperty(String name, Class<T> type) {
        return this.tiledMap.getProperties().get(name, type);
    }

    public String getProperty(String name) {
        return getProperty(name, String.class);
    }

    public MCTileSet getTileSet(int id) {
        return new MCTileSet(this.tiledMap.getTileSets().getTileSet(id));
    }

    public MCTileSet getTileSet(String name) {
        return new MCTileSet(this.tiledMap.getTileSets().getTileSet(name));
    }

    public MCMapLayer getLayer(int id) {
        return new MCMapLayer(this.tiledMap.getLayers().get(id));
    }

    public MCMapLayer getLayer(String name) {
        return new MCMapLayer(this.tiledMap.getLayers().get(name));
    }

    public Vector2 getDisplayCoordsFromTiled(Vector2 tiledCoords) {
        float displayX = tiledCoords.x * unitScale;
        float displayY = tiledCoords.y * unitScale;
        return new Vector2(displayX, displayY);
    }

    @Override
    public void dispose() {
        if (tiledMap != null) tiledMap.dispose();
    }

}

