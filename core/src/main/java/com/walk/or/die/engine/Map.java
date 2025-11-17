package com.walk.or.die.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.AtlasTmxMapLoader;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.maps.MapProperties;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.HashSet;
import java.util.Collections;

public class Map implements Disposable {
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private float unitScale;
    private Pathfinder pathfinder;


    public Map(String mapPath, OrthographicCamera camera, float unitScale, AssetManager assetManager) {
        this.camera = camera;
        this.pathfinder = new Pathfinder(this);
        this.unitScale = unitScale;
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
        /*
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(0);
        float unitScale = 1 / (layer.getTileWidth());
        */
        // on crée le renderer
        renderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);
    }

    public boolean isWalkable(int x, int y) {
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("Ground");
        if (x < 0 || x >= layer.getWidth() || y < 0 || y >= layer.getHeight()) return false;
        TiledMapTileLayer.Cell cell = layer.getCell(x, y);
        
        if (cell == null || cell.getTile() == null) return false; // vide = traversable

        MapProperties props = cell.getTile().getProperties();
        return !props.containsKey("blocked") && !props.containsKey("collision");
    }

    public void render() {
        camera.update();
        renderer.setView(camera);
        renderer.render();
    }

    @Override
    public void dispose() {
        if (renderer != null) renderer.dispose();
        if (tiledMap != null) tiledMap.dispose();
    }
}

