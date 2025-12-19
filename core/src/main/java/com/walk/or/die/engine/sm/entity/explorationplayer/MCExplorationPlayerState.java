package com.walk.or.die.engine.sm.entity.explorationplayer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.walk.or.die.engine.entities.MCExplorationPlayer;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.sm.entity.MCEntityState;

public abstract class MCExplorationPlayerState<T extends MCExplorationPlayerState.StateArgs> extends MCEntityState<T, MCExplorationPlayer>  {

    public MCExplorationPlayerState(MCExplorationPlayer parent) {
        super(parent);
    }

    public MCExplorationPlayer getParent() {
        return parent;
    }

    @Override
    public void update(float delta) {}

    @Override
    public void render(SpriteBatch batch) {}

    @Override
    public void renderEffects(SpriteBatch batch) {}

    // à override pour tous les états bloquants !
    public boolean isBlocking() {
        return false;
    }
    
    protected void inputPressed(MCInputManager.Command data) {
        if (!(data instanceof MCInputManager.Command)) return;
        
        if (data instanceof MCInputManager.ClickTileCommand) {
            //System.out.println("Oh, on a clické");
        }
        else if (data instanceof MCInputManager.DirectionalCommand) {
            //System.out.println("Oh on a pressé les touches du clavier");
        }
    }

}