package com.walk.or.die.engine.capacities;

import com.walk.or.die.engine.entities.MCCharacter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A factory for creating various character effects.
 * This class implements the singleton pattern to manage and build effects.
 */
public class MCEffectFactory {
    /**
     * The singleton instance of the effect factory.
     */
    private static MCEffectFactory instance = null;

    /**
     * Maps effect short names to functions that construct the corresponding effect.
     */
    private final Map<String, Function<MCCharacter, MCEffects>> effectMap;

    /**
     * Gets the singleton instance of the effect factory.
     *
     * @return The singleton instance of the effect factory.
     */
    public static MCEffectFactory get() {
        if (instance == null)
            instance = new MCEffectFactory();
        return instance;
    }

    /**
     * Constructs a new `MCEffectFactory` and initializes the effect map.
     * This is a private constructor to enforce the singleton pattern.
     */
    private MCEffectFactory() {
        effectMap = new HashMap<>();
        effectMap.put("decreaseShield", (parent) -> new MCShieldEffect(parent));
        effectMap.put("totalShield", (parent) -> new MCDecreaseShieldEffect(parent));
        effectMap.put("speedShoot", (parent) -> new MCSpeedShoot(parent));
        effectMap.put("speedUp", (parent) -> new MCSpeedUpEffect(parent));
        effectMap.put("strength", (parent) -> new MCStrengthEffect(parent));
    }

    /**
     * Builds an effect based on the provided short name.
     * If the effect name does not exist in the factory's map, an error message is printed to stderr.
     *
     * @param parent The character to which the effect is applied.
     * @param effectShortName The short name of the effect to build.
     * @return The created effect, or {@code null} if the effect does not exist.
     */
    public MCEffects buildEffect(MCCharacter parent, String effectShortName) {
        Function<MCCharacter, MCEffects> constructor = effectMap.get(effectShortName);

        if (constructor == null) {
            System.err.println("Effect " + effectShortName + " cant be added because it doesnt exist");
            return null; 
        }

        return constructor.apply(parent);
    }
}