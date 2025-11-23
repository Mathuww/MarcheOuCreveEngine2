package com.walk.or.die.engine.sm.entity;

import java.util.ArrayList;

import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.sm.MCStateMachine;

public class MCEntityStateMachine extends MCStateMachine<MCEntityState> {
    protected MCEntity parent;
    
    public MCEntityStateMachine(MCEntity parent) {
        super();
        this.parent = parent;
    };
}
