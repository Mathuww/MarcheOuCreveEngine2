package com.walk.or.die.engine.shared;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.walk.or.die.engine.exceptions.InvalidDataException;
import com.walk.or.die.engine.exceptions.NotBeautifulFontException;
import com.walk.or.die.engine.tiledmap.MCMap;
import com.walk.or.die.engine.tiledmap.MCMapLayer;
import com.walk.or.die.engine.tiledmap.MCMapObject;

/**
 * A singleton useful to load only one time assets used in many classes.
 */
public class MCSharedAssets {
    private static MCSharedAssets instance = null;

    /**
     * Get the singleton instance.
     * @return
     */
    public static MCSharedAssets get() {
        if (instance == null) instance = new MCSharedAssets();
        return instance;
    }

    private String fontPath;
    private MCMapLayer miscTilesLayer;
    private Map<String, TiledMapTile> savedTiles = new HashMap<>();
    private Map<String, TextureRegion> savedTextures = new HashMap<>();
    private Map<String, BitmapFont> savedBitmapFonts = new HashMap<>();

    private MCSharedAssets() {}

    /**
     * Init the singleton.
     * @param miscMapPath
     * @param fontPath
     * @param drh
     * @throws Exception
     */
    public void init(String miscMapPath, String fontPath, AssetManager drh) throws Exception {
        MCMap miscTilesMap = new MCMap(miscMapPath, drh);
        miscTilesLayer = miscTilesMap.getLayer(0);

        addSavedTile("validAttackTile");
        //addSavedTile("debugTile");
        addOnePixelTexture("fallback", Color.MAGENTA);
        addOnePixelTexture("black", Color.BLACK);
        addOnePixelTexture("white", Color.WHITE);
        addOnePixelTexture("grey", new Color(0xefefefff));
        addOnePixelTexture("yellow", Color.GOLDENROD);
        addOnePixelTexture("green", Color.GREEN);
        addOnePixelTexture("red", Color.RED);

        addGradientTexture("whiteFade", Color.WHITE);

        this.fontPath = fontPath;
        addSavedBitmapFont("Minecraft");
        addSavedBitmapFont("ariBlackAlpha");
    }

    private void addOnePixelTexture(String name, Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose(); 
        addSavedTexture(name, new TextureRegion(texture));
    }

    private void addGradientTexture(String name, Color color) {
        int width = 64;
        Pixmap pixmap = new Pixmap(width, 1, Pixmap.Format.RGBA8888);
        for (int x = 0; x < width; x++) {
            float alpha = 1f - ((float) x / width);
            pixmap.setColor(color.r, color.g, color.b, alpha);
            pixmap.drawPixel(x, 0);
        }
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        addSavedTexture(name, new TextureRegion(texture));
    }

    private void addSavedTile(String nameVal) throws Exception {
        TiledMapTileLayer.Cell cell = miscTilesLayer.getCellByProperty("name", nameVal);
        if (cell == null)
            throw new InvalidDataException("cant find tile named " + nameVal + " in shared assets map (" + nameVal + ")");
        TiledMapTile tile = cell.getTile();
        if (tile == null)
            throw new IllegalStateException("cant convert cell with tile " + nameVal + " to a tile in shared assets");
        savedTiles.put(nameVal, tile);
        TextureRegion texture = tile.getTextureRegion();
        if (texture != null)
            savedTextures.put(nameVal, texture);
    }

    private void addSavedTexture(String nameVal, TextureRegion texture) {
        savedTextures.put(nameVal, texture);
    }
 
    private void addSavedBitmapFont(String filename) throws NotBeautifulFontException { 
        if (filename.contains("Arial")) {
            throw new NotBeautifulFontException(filename);
        }
        BitmapFont font = new BitmapFont(Gdx.files.internal(fontPath + filename + ".fnt"));
        // pour pas smooth la police (garder rendu pixel perfect)
        font.getRegion().getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        font.setUseIntegerPositions(true);
        savedBitmapFonts.put(filename, font);
    }

    /**
     * Get a tiled from his name.
     * @param name
     * @return
     * @throws Exception if the tile doesn't exist or the name is false.
     */
    public TiledMapTile getSavedTile(String name) throws Exception {
        TiledMapTile tile = savedTiles.get(name);
        if (tile == null)
            throw new IllegalArgumentException("cant find " + name + " in saved tiles");
        return tile;
    }

    /**
     * Get a texture from his name.
     * @param name
     * @return
     * @throws Exception if the texture doesn't exist or the name is false.
     */
    public TextureRegion getSavedTexture(String name) throws Exception {
        TextureRegion texture = savedTextures.get(name);
        if (texture == null)
            throw new IllegalStateException("cant find texture region of tile " + name);
        return texture;
    }

    /**LEFT
     * Get a font from his name.
     * @param name
     * @return
     * @throws Exception if the font doesn't exist or the name is false.
     */
    public BitmapFont getSavedFont(String name) throws Exception {
        BitmapFont font = savedBitmapFonts.get(name);
        if (font == null)
            throw new IllegalStateException("cant find font in shared assets : " + name);
        return font;
    }

    public void dispose() {
        for (TextureRegion t : savedTextures.values()) {
            if (t.getTexture() != null)
                t.getTexture().dispose();
        }
        for (BitmapFont f : savedBitmapFonts.values()) 
            f.dispose();
    }
}
