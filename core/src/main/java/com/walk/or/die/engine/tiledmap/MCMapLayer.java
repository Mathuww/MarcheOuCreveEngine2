package com.walk.or.die.engine.tiledmap;

import java.util.ArrayList;
import java.util.List;

//import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * The class which gives us an interface for MapLayer from gdx
 * @see MapLayer
 */
public class MCMapLayer {
    private MapLayer ml;

    /**
     * The constructor
     * @param ml
     */
    public MCMapLayer(MapLayer ml) {
        this.ml = ml;
    }

    /**
     * Return the MapLayer
     * @return
     */
    public MapLayer getRawLayer() {
        return ml;
    }

    /**
     * Get all the objects on the layer
     * @return
     */
    public MapObjects getObjects() {
        return this.ml.getObjects();
    }

    /**
     * Get all the objects with a specified property
     * @param name
     * @param value
     * @return A list of these objects
     * @see MCMapObject
     */
    public List<MCMapObject> getObjectsByProperty(String name, String value) {
        List<MCMapObject> ret = new ArrayList<>();
        for (MapObject obj : this.getObjects()) {
            MapProperties props = obj.getProperties();
            if (!props.containsKey(name)) continue;
            Object prop = obj.getProperties().get(name);
            if (prop == null) continue;
            if (prop.equals(value)) {
                ret.add(new MCMapObject(obj));
            }
        }

        return ret;
    }

    /**
     * Return the specified cell
     * @param x
     * @param y
     * @return the cell
     * @throws IllegalStateException if CMapLayer doesn't contain a TiledMapTileLayer
     */
    public TiledMapTileLayer.Cell getCell(int x, int y) throws IllegalStateException{
        if (!(ml instanceof TiledMapTileLayer)) {
            throw new IllegalStateException("cant split a map layer in tiles if its not a tiledmaptilelayer instance");
        }
        TiledMapTileLayer tml = (TiledMapTileLayer) ml;

        TiledMapTileLayer.Cell cell = tml.getCell(x, y);
        return cell;
    }

    /**
     * Get the position of the first tile with the given property
     * @param name of the property
     * @param value of the property
     * @return
     * @throws IllegalStateException if CMapLayer doesn't contain a TiledMapTileLayer
     */
    public Vector2 getPosByProperty(String name, String value) {
         if (!(ml instanceof TiledMapTileLayer)) {
            throw new IllegalStateException("cant split a map layer in tiles if its not a tiledmaptilelayer instance");
        }

        TiledMapTileLayer tml = (TiledMapTileLayer) ml;

        for (int y = 0; y < tml.getHeight(); y++) {
            for (int x = 0; x < tml.getWidth(); x++) {
                TiledMapTileLayer.Cell cell = tml.getCell(x, y);
                if (cell != null && cell.getTile() != null) {
                    TiledMapTile tile = cell.getTile();
                    String s = tile.getProperties().get(name, String.class);
                    if (s != null && s.equals(value)) {
                        return new Vector2(x, y);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Get the first cell with the given property
     * @param name of the property
     * @param value of the property
     * @return
     * @throws IllegalStateException if CMapLayer doesn't contain a TiledMapTileLayer
     */
    public TiledMapTileLayer.Cell getCellByProperty(String name, String value) {
         if (!(ml instanceof TiledMapTileLayer)) {
            throw new IllegalStateException("cant split a map layer in tiles if its not a tiledmaptilelayer instance");
        }

        TiledMapTileLayer tml = (TiledMapTileLayer) ml;

        for (int y = 0; y < tml.getHeight(); y++) {
            for (int x = 0; x < tml.getWidth(); x++) {
                TiledMapTileLayer.Cell cell = tml.getCell(x, y);
                if (cell != null && cell.getTile() != null) {
                    TiledMapTile tile = cell.getTile();
                    String s = tile.getProperties().get(name, String.class);
                    if (s != null && s.equals(value)) {
                        return cell;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Return the layer split in a matrix of tiles
     * @return
     * @throws IllegalStateException if CMapLayer doesn't contain a TiledMapTileLayer
     */
    public Array<Array<TiledMapTile>> splitInTiles() {
        if (!(ml instanceof TiledMapTileLayer)) {
            throw new IllegalStateException("cant split a map layer in tiles if its not a tiledmaptilelayer instance");
        }

        TiledMapTileLayer tml = (TiledMapTileLayer) ml;

        Array<Array<TiledMapTile>> rows = new Array<>();

        for (int y = 0; y < tml.getHeight(); y++) {
            Array<TiledMapTile> row = new Array<>();
            for (int x = 0; x < tml.getWidth(); x++) {
                TiledMapTileLayer.Cell cell = tml.getCell(x, y);
                if (cell != null && cell.getTile() != null) {
                    TiledMapTile region = cell.getTile();
                    row.add(region);
                } else {
                    row.add(null); // ou alors mettre une région vide! mais ici, on est pas la pour s'embarasser
                    // avec de la paperasse bureaucratique.
                }
            }
            rows.add(row);
        }

        return rows;
    }

}
