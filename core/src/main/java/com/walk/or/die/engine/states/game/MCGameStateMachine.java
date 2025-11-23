package com.walk.or.die.engine.states.game;

import com.walk.or.die.engine.states.MCStateMachine;

public class MCGameStateMachine extends MCStateMachine<MCGameState> {
    private static MCGameStateMachine instance;

    private MCGameStateMachine() {
        super();
    }

    public static MCGameStateMachine get() {
        if (instance == null) instance = new MCGameStateMachine();
        return instance;
    }
}
