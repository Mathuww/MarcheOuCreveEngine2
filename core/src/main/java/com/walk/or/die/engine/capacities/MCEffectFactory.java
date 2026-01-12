package com.walk.or.die.engine.capacities;

import com.walk.or.die.engine.entities.MCCharacter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MCEffectFactory {
    private static MCEffectFactory instance = null;

    private final Map<String, Function<MCCharacter, MCEffects>> effectMap;

    public static MCEffectFactory get() {
        if (instance == null)
            instance = new MCEffectFactory();
        return instance;
    }

    private MCEffectFactory() {
        effectMap = new HashMap<>();
        effectMap.put("decreaseShield", (parent) -> new MCShieldEffect(parent));
        effectMap.put("totalShield", (parent) -> new MCDecreaseShieldEffect(parent));
        effectMap.put("speedShoot", (parent) -> new MCSpeedShoot(parent));
        effectMap.put("speedUp", (parent) -> new MCSpeedUpEffect(parent));
        effectMap.put("strength", (parent) -> new MCStrengthEffect(parent));
    }


    public MCEffects buildEffect(MCCharacter parent, String effectShortName) {
        Function<MCCharacter, MCEffects> constructor = effectMap.get(effectShortName);

        if (constructor == null) {
            System.err.println("Effect " + effectShortName + " cant be added because it doesnt exist");
            return null; 
        }

        return constructor.apply(parent);
    }
}