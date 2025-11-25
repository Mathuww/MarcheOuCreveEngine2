package com.walk.or.die.engine.vehicles;

import com.walk.or.die.engine.exceptions.VoluntaryCrashException;

public interface MCVehicle {
    // Interface du méchant malin
    public abstract void start();
    public abstract void stop() throws VoluntaryCrashException;
    public abstract void crash() throws VoluntaryCrashException;
}
