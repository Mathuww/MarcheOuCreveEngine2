package com.walk.or.die.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;


class Map {

    private int[][] matrice;
    private Sprite[][] sprMatrice;
    private Texture spikeText;

    public Map(int x, int y) {
        System.out.println(x);
        matrice = new int[x][y];
        sprMatrice = new Sprite[x][y];
        spikeText = new Texture("sprites/spike/bipboup.png");
    }

    private void setUpMap() {
        for (int i = 0; i < matrice.length; i++) {              
            for (int j = 0; j < matrice[i].length; j++) {
                if (matrice[i][j] == 1) {
                    sprMatrice[i][j] = new Sprite(spikeText);
                }
            }
            System.out.println();
        }
    }
}