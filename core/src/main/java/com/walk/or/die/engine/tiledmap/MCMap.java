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
     * Creates a new MCMap.
     * @param mapPath The path of the map in your folders.
     * @param assetManager The asset manager.
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
     * Loads a map with the path of its atlas.
     * @param mapPath The path to the map.
     * @param assetManager The asset manager.
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
     * Gets the width of the map.
     * @return The width of the map.
     */
    public float getWidth() {
        return maxLayerWidth;
    }

    /**
     * Gets the height of the map.
     * @return The height of the map.
     */
    public float getHeight() {
        return maxLayerHeight;
    }

    /**
     * Gets the tiled map.
     * @return The tiled map.
     */
    public TiledMap getTiledMap() {
        return this.tiledMap;
    }

    /**
     * Gets the tile size of the map.
     * @return The tile size of the map.
     */
    public float getTileSize() {
        return this.tileSize;
    }

    /**
     * Gets the scale of a unit.
     * @return The scale of a unit.
     */
    public float getUnitScale() {
        return this.unitScale;
    }

    /**
     * Gets the vector of the tile where pos is.
     * @param pos The position vector.
     * @return The vector of the tile where pos is.
     * @see MCIntVector2
     */
    public MCIntVector2 stickToNearestTile(Vector2 pos) {
        int newX = MathUtils.round(pos.x);
        int newY = MathUtils.round(pos.y);
        //System.out.println("sticking to " + newX + ", " + newY);
        return new MCIntVector2(newX, newY);
    }

    /**
     * Gets the tiled map properties.
     * @return The tiled map properties.
     */
    public MapProperties getProperties() {
        return this.tiledMap.getProperties();
    }

    /**
     * Gets a specific property of the tiled map with its type.
     * @param <T> The type of the property.
     * @param name The name of the property.
     * @param type The class type of the property.
     * @return The property of the specified type.
     */
    public <T> T getProperty(String name, Class<T> type) {
        return this.tiledMap.getProperties().get(name, type);
    }

    /**
     * Gets a specific property of the tiled map.
     * @param name The name of the property.
     * @return The property as a String.
     */
    public String getProperty(String name) {
        return getProperty(name, String.class);
    }

    /**
     * Gets a tileset from its id.
     * @param id The id of the tileset.
     * @return The tileset.
     * @see MCTileSet
     */
    public MCTileSet getTileSet(int id) {
        return new MCTileSet(this.tiledMap.getTileSets().getTileSet(id));
    }

    /**
     * Gets a tileset from its name.
     * @param name The name of the tileset.
     * @return The tileset.
     * @see MCTileSet
     */
    public MCTileSet getTileSet(String name) {
        return new MCTileSet(this.tiledMap.getTileSets().getTileSet(name));
    }

    /**
     * Gets a layer from its id.
     * @param id The id of the layer.
     * @return The map layer.
     * @see MCMapLayer
     */
    public MCMapLayer getLayer(int id) {
        return new MCMapLayer(this.tiledMap.getLayers().get(id));
    }

    /**
     * Gets a layer from its name.
     * @param name The name of the layer.
     * @return The map layer.
     * @see MCMapLayer
     */
    public MCMapLayer getLayer(String name) {
        return new MCMapLayer(this.tiledMap.getLayers().get(name));
    }

    /**
     * Returns the display point of a tile from its tiled coords.
     * @param tiledCoords The tile coordinates.
     * @return The display coordinates.
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