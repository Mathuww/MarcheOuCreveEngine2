package com.walk.or.die.engine.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapProperties;

/**
 * A class that contains lots of methods useful through the game.
 */
public abstract class MCUtils {
    /**
     * Gets a list of files ending with ".tmx".
     * @param folder The folder to search in.
     * @param ext This parameter is ignored.
     * @return A list of file handles.
     */
    public static List<FileHandle> listFilesByExt(String folder, String ext) {
        //System.out.println("listfilesbyext on folder " + folder);

        if (!folder.endsWith("/")) {
            folder += "/";
        }

        List<FileHandle> res = new ArrayList<>();
        FileHandle manifest = Gdx.files.internal("assets.txt");

        for (String line : manifest.readString().split("\\R")) {
            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.startsWith(folder) && line.endsWith(".tmx")) {
                //System.out.println(line);
                res.add(Gdx.files.internal(line));
            }
        }

        return res;
    }

    /**
     * Gets an integer property from map properties.
     * @param props The map properties.
     * @param key The key of the property.
     * @param defaultVal The default value if the property is not found.
     * @return The integer property value.
     */
    public static int getIntProperty(MapProperties props, String key, int defaultVal) {
        Number n = props.get(key, Number.class);
        return (n != null) ? n.intValue() : defaultVal;
    }

    /**
     * Gets a float property from map properties.
     * @param props The map properties.
     * @param key The key of the property.
     * @param defaultVal The default value if the property is not found.
     * @return The float property value.
     */
    public static float getFloatProperty(MapProperties props, String key, float defaultVal) {
        Number n = props.get(key, Number.class);
        return (n != null) ? n.floatValue() : defaultVal;
    }

    /**
     * Returns a random line from a file.
     * @param nameType The path to the file.
     * @return A random line from the file.
     */
    public static String getRandomLineFromFile(String nameType) {
        int n;
        Random r = new Random();
        List<String> names = new ArrayList<>();
        FileHandle file = Gdx.files.internal(nameType);

        for (String line : file.readString().split("\\R")) {
            line = line.trim();
            if (line.isEmpty()) continue;
            names.add(line);
        }
        n = r.nextInt(names.size());

        return names.get(n);
    }
}