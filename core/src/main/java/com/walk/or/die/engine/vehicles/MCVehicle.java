package com.walk.or.die.engine.vehicles;

import com.walk.or.die.engine.exceptions.VoluntaryCrashException;

/**
 * A truly necessary interface to simulate complex vehicles
 */
public interface MCVehicle {
    /**
     * Start
     */
    public abstract void start();
    /**
     * Stop (No one stop us)
     * @throws VoluntaryCrashException
     */
    public abstract void stop() throws VoluntaryCrashException;
    /**
     * Crash (crashing is a valid solution when someone try to stop you)
     * @throws VoluntaryCrashException
     */
    public abstract void crash() throws VoluntaryCrashException;
}
