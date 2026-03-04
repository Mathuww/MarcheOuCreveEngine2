package com.walk.or.die.engine.vehicles;

import com.walk.or.die.engine.exceptions.VoluntaryCrashException;

/**
 * A truly necessary interface to simulate complex vehicles.
 */
public interface MCVehicle {
    /**
     * Starts the vehicle.
     */
    public abstract void start();
    /**
     * Stops the vehicle.
     * @throws VoluntaryCrashException Throws if the vehicle crashes voluntarily during the stop operation.
     */
    public abstract void stop() throws VoluntaryCrashException;
    /**
     * Crashes the vehicle.
     * @throws VoluntaryCrashException Throws to indicate a voluntary crash has occurred.
     */
    public abstract void crash() throws VoluntaryCrashException;
}