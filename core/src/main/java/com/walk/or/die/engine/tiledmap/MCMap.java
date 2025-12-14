package com.walk.or.die.engine.tiledmap;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.utils.Disposable;
import com.walk.or.die.engine.exceptions.InvalidDataException;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import java.util.List;

/**
 * The class which reads a tiledMap from an atlas.
 */
public class MCMap implements Disposable {
    /**
     * The map from tiled.
     * @see TiledMap
     */
    protected TiledMap tiledMap;
    protected float tileSize;
    protected float unitScale;
    private int maxLayerWidth = 0;
    private int maxLayerHeight = 0;

    /**
     * Create a new MCMap.
     * @param mapPath path of the map in your folders.
     * @param assetManager
     */
    public MCMap(String mapPath, AssetManager assetManager) {
        loadMapWithAtlas(mapPath, assetManager);

        // on compute les w & h max
        for (MapLayer layer : tiledMap.getLayers()) {
            if (layer instanceof TiledMapTileLayer tileLayer) {
                if (tileLayer.getWidth() > maxLayerWidth)
                    maxLayerWidth = tileLayer.getWidth();
                if (tileLayer.getHeight() > maxLayerHeight)
                    maxLayerHeight = tileLayer.getHeight();
            }
        }
    }

    /**
     * Load a map with the path of his atlas.
     * @param mapPath
     * @param assetManager
     */
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

        //System.out.println("tile size : " + this.tileSize + " so unit scale is " + this.unitScale);
    }

    /**
     * @return the width of the map.
     */
    public float getWidth() {
        return maxLayerWidth;
    }

    /**
     * @return the height of the map.
     */
    public float getHeight() {
        return maxLayerHeight;
    }

    /**
     * @return the tiled map.
     */
    public TiledMap getTiledMap() {
        return this.tiledMap;
    }

    /**
     * @return the tile size of the map.
     */
    public float getTileSize() {
        return this.tileSize;
    }

    /**
     * @return the scale of a unit.
     */
    public float getUnitScale() {
        return this.unitScale;
    }

    /**
     * @param pos
     * @return the vector of the tile where pos is.
     * @see MCIntVector2
     */
    public MCIntVector2 stickToNearestTile(Vector2 pos) {
        int newX = MathUtils.round(pos.x);
        int newY = MathUtils.round(pos.y);
        //System.out.println("sticking to " + newX + ", " + newY);
        return new MCIntVector2(newX, newY);
    }

    /**
     * @return the tiled map properties.
     */
    public MapProperties getProperties() {
        return this.tiledMap.getProperties();
    }

    /**
     * Get a specific property of the tiled map with his type.
     * @param <T>
     * @param name
     * @param type
     * @return
     */
    public <T> T getProperty(String name, Class<T> type) {
        return this.tiledMap.getProperties().get(name, type);
    }

    /**
     * Get a specific property of the tiled map.
     * @param name
     * @return
     */
    public String getProperty(String name) {
        return getProperty(name, String.class);
    }

    /**
     * Get a tileset from his id.
     * @param id
     * @return
     * @see MCTileSet
     */
    public MCTileSet getTileSet(int id) {
        return new MCTileSet(this.tiledMap.getTileSets().getTileSet(id));
    }

    /**
     * Get a tileset from his name.
     * @param name
     * @return
     * @see MCTileSet
     */
    public MCTileSet getTileSet(String name) {
        return new MCTileSet(this.tiledMap.getTileSets().getTileSet(name));
    }

    /**
     * Get a layer from his id.
     * @param id
     * @return
     * @see MCMapLayer
     */
    public MCMapLayer getLayer(int id) {
        return new MCMapLayer(this.tiledMap.getLayers().get(id));
    }

    /**
     * Get a layer from his name
     * @param name
     * @return
     * @see MCMapLayer
     */
    public MCMapLayer getLayer(String name) {
        return new MCMapLayer(this.tiledMap.getLayers().get(name));
    }

    /**
     * Return the display point of a tile from his tiled coords
     * @param tiledCoords
     * @return
     */
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

