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
     * Gets a list of the files ending by ".tmx".
     * @param folder the folder to search in
     * @param ext We doesn't care about this parameter
     * @return a list of file handles.
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
     * @param props the map properties
     * @param key the key of the property
     * @param defaultVal the default value if the property is not found
     * @return the integer property value.
     */
    public static int getIntProperty(MapProperties props, String key, int defaultVal) {
        Number n = props.get(key, Number.class);
        return (n != null) ? n.intValue() : defaultVal;
    }

    /**
     * Gets a float property from map properties.
     * @param props the map properties
     * @param key the key of the property
     * @param defaultVal the default value if the property is not found
     * @return the float property value.
     */
    public static float getFloatProperty(MapProperties props, String key, float defaultVal) {
        Number n = props.get(key, Number.class);
        return (n != null) ? n.floatValue() : defaultVal;
    }

    /**
     * Returns a random line in a file. The function name is clear, why do you need a description ?<br>
     * Here is a list of all its relevant uses: <br>
     * - Adds a little spice to your life and your code<br>
     * - Makes the game crash<br>
     * - Drives anyone who tries to understand your code crazy<br>
     * We hope it will be useful!
     * @param nameType the name type
     * @return a random line from the file.
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