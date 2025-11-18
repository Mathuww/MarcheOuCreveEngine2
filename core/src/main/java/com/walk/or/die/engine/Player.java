package com.walk.or.die.engine;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private Map map;

    private Rectangle hitbox;
    private TextureRegion currentRegion;
    private Sprite sprite;

    private float SIZE = 1f;

    private int hp;

    public Player(Map map, Vector2 spawn) throws DataException {
        this.map = map;
        
        TiledMap tiledMap = this.map.getTiledMap();
        TiledMapTileSet tileSet = tiledMap.getTileSets().getTileSet("player");
        if (tileSet == null) {
            throw new DataException("Tileset player does not exist in map");
        }

        TextureRegion baseRegion = null;
        for (TiledMapTile tile : tileSet) {
            Object type = tile.getProperties().get("type");
            if ("player".equals(type)) {
                baseRegion = tile.getTextureRegion();
                System.out.println("found player tile in tileset");
                break;
            }
        }

        if (baseRegion == null) {
            throw new DataException("player tile not found in player tileset");
        }

        currentRegion = baseRegion;

        sprite = new Sprite(currentRegion);

        sprite.setSize(SIZE, SIZE);

        hitbox = new Rectangle(spawn.x, spawn.y, sprite.getWidth(), sprite.getHeight());
        sprite.setPosition(spawn.x, spawn.y);

        this.hp = 100;
    }


    public void update(float delta) {
        // utile pour ajouter des anims par la suite hihihi
        sprite.setPosition(hitbox.x, hitbox.y);
        sprite.setRegion(currentRegion);
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }


    public float getX() {
        return this.hitbox.x;
    }

    public float getY() {
        return this.hitbox.y;
    }

    public void setX(float x) {
        this.hitbox.x = x;
    }

    public void setY(float y) {
        this.hitbox.y = y;
    }

    public float getSize() {
        return sprite.getWidth();
    }

    public Vector2 getPosition() {
        return new Vector2(this.hitbox.x, this.hitbox.y);
    }

    public void setPosition(float x, float y) {
        this.hitbox.setPosition(x, y);
    }


}