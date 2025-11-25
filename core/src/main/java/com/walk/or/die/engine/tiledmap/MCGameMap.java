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

public class MCGameMap extends MCMap {
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private MCPathfinder pathfinder;

    public MCGameMap(String mapPath, OrthographicCamera camera, AssetManager assetManager) {
        super(mapPath, assetManager);
        this.camera = camera;
        this.pathfinder = new MCPathfinder(this);
    }

    public List<Vector2> getPath(Vector2 start, Vector2 end) {
        return pathfinder.getPath(start, end);
    }

    @Override
    protected void loadMapWithAtlas(String mapPath, AssetManager assetManager) {
        super.loadMapWithAtlas(mapPath, assetManager);
        // on crée le renderer
        renderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);
    }

    public void render() {
        camera.update();
        renderer.setView(camera);
        renderer.render();
    }

    @Override
    public void dispose() {
        if (renderer != null) renderer.dispose();
        super.dispose();
    }
}

