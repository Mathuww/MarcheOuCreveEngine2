package com.walk.or.die.engine;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public abstract class MCUtils {
    public static List<FileHandle> listFilesByExt(FileHandle folder, String ext) {
        List<FileHandle> res = new ArrayList<>();
        for (FileHandle f : folder.list()) {
            if (f.isDirectory()) {
                res.addAll(listFilesByExt(f, ext));
            } else if (f.extension().equalsIgnoreCase(ext)) {
                res.add(f);
            }
        }
        return res;
    }
}
