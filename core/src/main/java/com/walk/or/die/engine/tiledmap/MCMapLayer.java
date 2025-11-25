package com.walk.or.die.engine.tiledmap;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;


public class MCMapLayer {
    private MapLayer ml;

    public MCMapLayer(MapLayer ml) {
        this.ml = ml;
    }

    public MapLayer getRawLayer() {
        return ml;
    }

    public MCMapObject getObjectByType(String name) {
        for (MapObject obj : this.ml.getObjects()) {
            Object type = obj.getProperties().get("type");
            if (name.equals(type)) {
                return new MCMapObject(obj);
            }
        }

        return null;
    }

    public TiledMapTileLayer.Cell getCell(int x, int y) {
        if (!(ml instanceof TiledMapTileLayer)) {
            throw new IllegalStateException("cant split a map layer in tiles if its not a tiledmaptilelayer instance");
        }
        TiledMapTileLayer tml = (TiledMapTileLayer) ml;

        TiledMapTileLayer.Cell cell = tml.getCell(x, y);
        return cell;
    }

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
                    if (tile.getProperties().get(name, String.class).equals(value)) {
                        return new Vector2(x, y);
                    }
                }
            }
        }

        return null;
    }

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
                    if (tile.getProperties().get(name, String.class).equals(value)) {
                        return cell;
                    }
                }
            }
        }

        return null;
    }

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
