package com.walk.or.die.engine.tiledmap;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.utils.Disposable;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.entities.MCEntityFactory;
import com.walk.or.die.engine.exceptions.DataException;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MCTerrainMap extends MCMap {
    private OrthogonalTiledMapRenderer renderer;
    private MCGame parent;

    public MCTerrainMap(String mapPath, AssetManager assetManager) {
        super(mapPath, assetManager);
    }
    
    @Override
    public void loadMapWithAtlas(String mapPath, AssetManager assetManager) {
        super.loadMapWithAtlas(mapPath, assetManager);
        // on crée le renderer
        renderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);
    }

    public boolean isWalkable(int x, int y) {
        
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        if (layer == null) return false;
        if (x < 0 || x >= layer.getWidth() || y < 0 || y >= layer.getHeight()) return false;
        TiledMapTileLayer.Cell cell = layer.getCell(x, y);
        
        if (cell == null || cell.getTile() == null) return false; // vide = non traversable

        MapProperties props = cell.getTile().getProperties();
        if (props.containsKey("blocked") || props.containsKey("collision")) {
            return false;
        }
        return true;
    }
           
    public boolean isProtect(int x, int y) {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        if (layer == null) return false;
        if (x < 0 || x >= layer.getWidth() || y < 0 || y >= layer.getHeight()) return false;
        TiledMapTileLayer.Cell cell = layer.getCell(x, y);
        
        if (cell == null || cell.getTile() == null) return false;

        MapProperties props = cell.getTile().getProperties();
        if (props.containsKey("blocked") || props.containsKey("collision")) {
            return true;
        }
        return false;
    }

    public MCMapObject getPlayerSpawnObject() throws DataException {
        MCMapLayer layer = this.getLayer("Entities");
        if (layer == null) throw new DataException("no Entities layer in Tiled map");

        // normalement y en a un seul 
        List<MCMapObject> objs = layer.getObjectsByProperty("type", "player_spawn");
        MCMapObject obj;
        try {
            obj = objs.get(0); 
        } catch (Exception e) {
            throw new DataException("player spawn not found in Entities");
        } 

        return obj;
    }

    // n'a vocation à être appelée que pour start.tmx
    public String getPlayerEntityType() throws DataException {
        MCMapObject obj = getPlayerSpawnObject();

        String playerEntityName = obj.getRawObject().getProperties().get("name", String.class);
        if (playerEntityName == null)
            throw new DataException("in start.tmx, player entity name need to filled in property 'name' for tile with type player_spawn");
        return playerEntityName;
    }

    public Vector2 getPlayerSpawnPos() throws DataException {
        MCMapObject obj = getPlayerSpawnObject();

        Vector2 pos = obj.getPosition();
        pos = getDisplayCoordsFromTiled(pos);
        return this.stickToNearestTile(pos);
    }

    public Array<MCEntity> spawnEntities(MCGame game) throws Exception {
        MCMapLayer layer = this.getLayer("Entities");
        if (layer == null) throw new DataException("no Entities layer in game map");

        MCEntityFactory entityFact = MCEntityFactory.get();

        Array<MCEntity> entityArray = new Array<>();
        Map<String, Integer> entityCounter = new HashMap<>();

        MapObjects objects = layer.getObjects();
        for (MapObject rawObj : objects) {
            MapProperties props = rawObj.getProperties();
            if (!props.containsKey("name")) continue;
            String entityName = props.get("name", String.class);
            if (entityName != null) {
                if (props.containsKey("type")
                    && props.get("type", String.class).equals("player_spawn")) {
                    continue; // le player spawn est géré à part
                }

                int count;
                if (entityCounter.containsKey(entityName)) {
                    count = entityCounter.get(entityName);
                    count += 1;
                } else {
                    count = 1;
                }
                entityCounter.put(entityName, count);
                MCEntity entity = entityFact.build(game, this, entityName, entityName + "_ " + String.format("%03d", count));

                MCMapObject obj = new MCMapObject(rawObj);
                Vector2 pos = obj.getPosition();
                pos = getDisplayCoordsFromTiled(pos);
                pos = this.stickToNearestTile(pos);

                entity.setPosition(pos.x, pos.y);
                entityArray.add(entity);
            }
        }

        return entityArray;
    }

    public void render(OrthographicCamera camera) {
        //camera.update();
        renderer.setView(camera);
        renderer.render();
    }

    @Override
    public void dispose() {
        if (renderer != null) renderer.dispose();
        super.dispose();
    }
}

