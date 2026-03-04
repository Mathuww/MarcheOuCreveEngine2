package com.walk.or.die.engine.shared;

import java.util.HashMap;
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

/**
 * A singleton useful to load assets that are used in many classes only once.
 */
public class MCSharedAssets {
    /**
     * The singleton instance of MCSharedAssets.
     */
    private static MCSharedAssets instance = null;

    /**
     * Gets the singleton instance.
     * @return The singleton instance.
     */
    public static MCSharedAssets get() {
        if (instance == null) instance = new MCSharedAssets();
        return instance;
    }

    /**
     * The path to the font files.
     */
    private String fontPath;
    /**
     * The TiledMapLayer containing miscellaneous tiles.
     */
    private MCMapLayer miscTilesLayer;
    /**
     * A map storing saved TiledMapTile objects, indexed by name.
     */
    private Map<String, TiledMapTile> savedTiles = new HashMap<>();
    /**
     * A map storing saved TextureRegion objects, indexed by name.
     */
    private Map<String, TextureRegion> savedTextures = new HashMap<>();
    /**
     * A map storing saved BitmapFont objects, indexed by name.
     */
    private Map<String, BitmapFont> savedBitmapFonts = new HashMap<>();

    /**
     * Constructs a new MCSharedAssets instance.
     */
    private MCSharedAssets() {}

    /**
     * Initializes the singleton.
     * @param miscMapPath The path to the miscellaneous map.
     * @param fontPath The path to the font.
     * @param drh The asset manager.
     * @throws Exception If an error occurs during initialization.
     */
    public void init(String miscMapPath, String fontPath, AssetManager drh) throws Exception {
        MCMap miscTilesMap = new MCMap(miscMapPath, drh);
        miscTilesLayer = miscTilesMap.getLayer(0);

        addSavedTile("validAttackTile");
        addSavedTile("trajectoryTile");
        //addSavedTile("debugTile");
        addOnePixelTexture("empty", new Color(1f, 1f, 1f, 0.25f));
        addOnePixelTexture("fallback", Color.MAGENTA);
        addOnePixelTexture("black", Color.BLACK);
        addOnePixelTexture("white", Color.WHITE);
        addOnePixelTexture("grey", new Color(0xefefefff));
        addOnePixelTexture("yellow", Color.GOLDENROD);
        addOnePixelTexture("green", Color.GREEN);
        addOnePixelTexture("red", Color.RED);

        addGradientTexture("whiteFade", Color.WHITE);

        this.fontPath = fontPath;
        addSavedBitmapFont("ariBlackAlpha", "ariBlackAlpha", true);
        addSavedBitmapFont("ariBlackAlpha", "ariBlackAlphaFP", false);
    }

    /**
     * Adds a one-pixel texture.
     * @param name The name of the texture.
     * @param color The color of the texture.
     */
    private void addOnePixelTexture(String name, Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose(); 
        addSavedTexture(name, new TextureRegion(texture));
    }

    /**
     * Adds a gradient texture.
     * @param name The name of the texture.
     * @param color The color of the texture.
     */
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

    /**
     * Adds a saved tile.
     * @param nameVal The name of the tile.
     * @throws Exception If the tile cannot be found or an error occurs during the process.
     */
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

    /**
     * Adds a saved texture.
     * @param nameVal The name of the texture.
     * @param texture The texture region.
     */
    private void addSavedTexture(String nameVal, TextureRegion texture) {
        savedTextures.put(nameVal, texture);
    }
 
    /**
     * Adds a saved bitmap font.
     * @param filename The name of the file.
     * @param petName The pet name for the font.
     * @param intPos Indicates whether to use integer positions for rendering.
     * @throws NotBeautifulFontException If the font is not considered beautiful.
     */
    private void addSavedBitmapFont(String filename, String petName, boolean intPos) throws NotBeautifulFontException { 
        if (filename.contains("Arial")) {
            throw new NotBeautifulFontException(filename);
        }
        BitmapFont font = new BitmapFont(Gdx.files.internal(fontPath + filename + ".fnt"));
        // pour pas smooth la police (garder rendu pixel perfect)
        font.getRegion().getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        font.setUseIntegerPositions(intPos);
        savedBitmapFonts.put(petName, font);
    }

    /**
     * Gets a tile by its name.
     * @param name The name of the tile.
     * @return The tiled map tile.
     * @throws Exception If the tile does not exist or the name is invalid.
     */
    public TiledMapTile getSavedTile(String name) throws Exception {
        TiledMapTile tile = savedTiles.get(name);
        if (tile == null)
            throw new IllegalArgumentException("cant find " + name + " in saved tiles");
        return tile;
    }

    /**
     * Gets a texture by its name.
     * @param name The name of the texture.
     * @return The texture region.
     * @throws Exception If the texture does not exist or the name is invalid.
     */
    public TextureRegion getSavedTexture(String name) throws Exception {
        TextureRegion texture = savedTextures.get(name);
        if (texture == null)
            throw new IllegalStateException("cant find texture region of tile " + name);
        return texture;
    }

    /**
     * Gets a font by its name.
     * @param name The name of the font.
     * @return The bitmap font.
     * @throws Exception If the font does not exist or the name is invalid.
     */
    public BitmapFont getSavedFont(String name) throws Exception {
        BitmapFont font = savedBitmapFonts.get(name);
        if (font == null)
            throw new IllegalStateException("cant find font in shared assets : " + name);
        return font;
    }

    /**
     * Disposes of all saved textures and fonts.
     */
    public void dispose() {
        for (TextureRegion t : savedTextures.values()) {
            if (t.getTexture() != null)
                t.getTexture().dispose();
        }
        for (BitmapFont f : savedBitmapFonts.values()) 
            f.dispose();
    }
}