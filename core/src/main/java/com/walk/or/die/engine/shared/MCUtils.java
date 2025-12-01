package com.walk.or.die.engine.shared;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapProperties;

public abstract class MCUtils {
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

    public static int getIntProperty(MapProperties props, String key, int defaultVal) {
        Number n = props.get(key, Number.class);
        return (n != null) ? n.intValue() : defaultVal;
    }

    public static float getFloatProperty(MapProperties props, String key, float defaultVal) {
        Number n = props.get(key, Number.class);
        return (n != null) ? n.floatValue() : defaultVal;
    }
}
