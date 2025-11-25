package com.walk.or.die.engine.entities.attacks;

import java.util.Map;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.entities.MCEntity;

public class MCGenericAttack extends MCEntity.Attack {
    public MCGenericAttack(MCEntity parent, int power, Map<Vector2, Float> pattern) {
        super(parent, power, pattern);
    }

    public void initFromProperties(MapProperties props) {
        
    }
}
