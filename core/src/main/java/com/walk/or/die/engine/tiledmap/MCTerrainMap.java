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
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.entities.MCEntityFactory;
import com.walk.or.die.engine.exceptions.InvalidDataException;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.shared.MCUtils;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The class which contains the maps we play on.
 * @see MCMap
 */
public class MCTerrainMap extends MCMap {
    private OrthogonalTiledMapRenderer renderer;
    private MCGame parent;

    /**
     * The constructor.
     * @param mapPath The path of the map.
     * @param assetManager The asset manager.
     */
    public MCTerrainMap(String mapPath, AssetManager assetManager) {
        super(mapPath, assetManager);
    }
    
    @Override
    public void loadMapWithAtlas(String mapPath, AssetManager assetManager) {
        super.loadMapWithAtlas(mapPath, assetManager);
        // on crée le renderer
        renderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);
    }

    /**
     * Gets if the tile itself at the given position is walkable.
     * A tile isn't walkable if it is void on all layers,
     * or if it is "blocked" at the highest layer
     * that isn't void at this tile.
     * @param pos The position to check.
     * @return True if the tile is walkable, false otherwise.
     */
    public boolean isWalkable(MCIntVector2 pos) {
        // si ca excede la limite max
        if (pos.x < 0 || pos.x >= getWidth() || pos.y < 0 || pos.y >= getHeight())
            return false;
        // tester toutes les layers
        boolean foundNotVoidLayer = false;
        for (int i = tiledMap.getLayers().getCount() - 1 ; i >= 0; i--) {
            MapLayer layer = tiledMap.getLayers().get(i);
            //System.out.println(layer.getName());
            if (layer instanceof TiledMapTileLayer tileLayer) {
                TiledMapTileLayer.Cell cell = tileLayer.getCell(pos.x, pos.y);
        
                if (cell == null || cell.getTile() == null) 
                    continue;

                MapProperties props = cell.getTile().getProperties();
                if (!foundNotVoidLayer) {
                    foundNotVoidLayer = true;
                    Boolean blocked = props.get("blocked", Boolean.class);
                    if (blocked != null && blocked) {
                        return false;
                    }
                }
            }
        }
        if (!foundNotVoidLayer)
            return false;
        return true;
    }
    
           
    /**
     * Gets if the tile itself at the given position protects bullets.
     * A tile is protecting if the highest non void layer has property "blocked".
     * @param pos The position to check.
     * @return True if the tile is protecting, false otherwise.
     */
    public boolean isProtect(MCIntVector2 pos) {
        // si ca excede la limite max
        if (pos.x < 0 || pos.x >= getWidth() || pos.y < 0 || pos.y >= getHeight())
            return false;
        // tester toutes les layers
        boolean foundNotVoidLayer = false;
        for (int i = tiledMap.getLayers().getCount() - 1 ; i >= 0; i--) {
            MapLayer layer = tiledMap.getLayers().get(i);
            //System.out.println(layer.getName());
            if (layer instanceof TiledMapTileLayer tileLayer) {
                TiledMapTileLayer.Cell cell = tileLayer.getCell(pos.x, pos.y);
        
                if (cell == null || cell.getTile() == null) 
                    continue;

                MapProperties props = cell.getTile().getProperties();
                if (!foundNotVoidLayer) {
                    foundNotVoidLayer = true;
                    Boolean blocked = props.get("blocked", Boolean.class);
                    if (blocked != null && blocked) {
                        return true;
                    }
                }
            }
        }
        if (!foundNotVoidLayer)
            return false;
        return false;
    }

    /**
     * Creates all the entities on the map.
     * @param game The game instance.
     * @return A set of spawned entities.
     * @throws Exception If an error occurs during entity creation.
     * @see MCEntity
     */
    public Set<MCEntity> spawnEntities(MCGame game) throws Exception {
        MCMapLayer primaryLayer = this.getLayer("Primary_Entities");
        MCMapLayer portalLayer  = this.getLayer("Portals");

        if (primaryLayer == null)
            throw new InvalidDataException("no Primary Entities layer in game map");
        if (portalLayer == null)
            throw new InvalidDataException("no Portal Entities layer in game map");

        MCEntityFactory entityFact = MCEntityFactory.get();

        Set<MCEntity> entityArray = new HashSet<>();
        Map<String, Integer> entityCounter = new HashMap<>();

        MapObjects objects = new MapObjects();

        for (MapObject obj : primaryLayer.getObjects()) {
            objects.add(obj);
        }

        // Portals => Integration avec un autre layer
        for (MapObject portal : portalLayer.getObjects()) {
            objects.add(portal);
        }

        for (MapObject rawObj : objects) {
            MapProperties props = rawObj.getProperties();
            if (!props.containsKey("name")) continue;
            String entityName = props.get("name", String.class);
            if (entityName != null) {
                 /* if (props.containsKey("type")
                    && props.get("type", String.class).equals("player_spawn")) {
                    continue; // le player spawn est géré à part
                } */

                int count;
                if (entityCounter.containsKey(entityName)) {
                    count = entityCounter.get(entityName);
                    count += 1;
                } else {
                    count = 1;
                }
                entityCounter.put(entityName, count); // POURQUOI ??

                MCEntity entity = entityFact.build(game, this, entityName, entityName + "_" + String.format("%03d", count), props);

                if (entity instanceof MCCharacter character) {
                    String entityDisplayName = props.get("displayName", String.class);
                    if (entityDisplayName != null) 
                        character.setDisplayName(entityDisplayName);
                    else 
                        character.setDisplayName(MCUtils.getRandomLineFromFile("random_names/" + entityName + ".txt"));
                }

                MCMapObject obj = new MCMapObject(rawObj);
                Vector2 pos = obj.getPosition();
                //System.out.println("working on entity qui a l'object tiled de pos " + pos.x + ", " + pos.y);
                pos = getDisplayCoordsFromTiled(pos);
                //System.out.println("display coords from tiled renvoie " + pos.x + ", " + pos.y);
                MCIntVector2 tilePos = this.stickToNearestTile(pos);
                //System.out.println("on obtient avec stick to nearest " + tilePos.toString());

                entity.setPosition(tilePos.toGdxVect());
                entityArray.add(entity);
            }
        }

        return entityArray;
    }

    /**
     * Renders the map.
     * @param camera The camera to use for rendering.
     */
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