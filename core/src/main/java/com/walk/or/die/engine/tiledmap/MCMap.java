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
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private float tileSize;
    private float unitScale;
    private MCPathfinder pathfinder;


    public MCMap(String mapPath, OrthographicCamera camera, AssetManager assetManager) {
        this.camera = camera;
        this.pathfinder = new MCPathfinder(this);
        loadMapWithAtlas(mapPath, assetManager);
    }

    public List<Vector2> getPath(Vector2 start, Vector2 end) {
        return pathfinder.getPath(start, end);
    }

    private void loadMapWithAtlas(String mapPath, AssetManager assetManager) {
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
        // on crée le renderer
        renderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);
    }

    public float getWidth() {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("Ground");
        return layer.getWidth();
    }

    public float getHeight() {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("Ground");
        return layer.getHeight();
    }

    public boolean isWalkable(int x, int y) {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("Ground");
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

    public void render() {
        camera.update();
        renderer.setView(camera);
        renderer.render();
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
        System.out.println("sticking to " + newX + ", " + newY);
        return new Vector2(newX, newY);
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

    public Vector2 getEntitySpawnPos(String entity) throws DataException, IllegalArgumentException {
        if ("".equals(entity)) {
            throw new IllegalArgumentException("cannot find spawn pos of empty string !!!");
        }

        MCMapLayer layer = this.getLayer("Entities");
        if (layer == null) throw new DataException("no Entities layer in Tiled map");

        MCMapObject obj = layer.getObjectByType(entity);
        if (obj == null) throw new DataException(entity + " not found in Entities");

        Vector2 pos = obj.getPosition();
        pos = getDisplayCoordsFromTiled(pos);
        return this.stickToNearestTile(pos);

    }

    public Vector2 getDisplayCoordsFromTiled(Vector2 tiledCoords) {
        float displayX = tiledCoords.x * unitScale;
        float displayY = tiledCoords.y * unitScale;
        return new Vector2(displayX, displayY);
    }

    @Override
    public void dispose() {
        if (renderer != null) renderer.dispose();
        if (tiledMap != null) tiledMap.dispose();
    }
}

