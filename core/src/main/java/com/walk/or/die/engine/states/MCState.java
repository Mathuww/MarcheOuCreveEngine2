package com.walk.or.die.engine.states;

import com.walk.or.die.engine.MCEventBus;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.input.MCInputManager.ClickTileCommand;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.input.MCInputManager.DirectionalCommand;
import java.util.List;
import java.util.function.Consumer;
import java.util.ArrayList;

public abstract class MCState<T extends MCState.StateArgs> {
    public static class StateArgs {}

    protected String name;

    public MCState() {}
    
    public String getName() {
        return name;
    }

    public abstract void update(float delta);

    public abstract void enter(T args);

    public abstract void exit();

    protected <U> void listen(String eventName, Consumer<U> listener) {
        MCEventBus bus = MCEventBus.get();
        bus.on(this, eventName, listener);
    }

    protected abstract void changeState(String newState, StateArgs args);
}