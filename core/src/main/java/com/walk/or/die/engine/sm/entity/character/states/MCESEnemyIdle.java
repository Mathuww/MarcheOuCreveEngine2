package com.walk.or.die.engine.sm.entity.character.states;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEnemy;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.sm.entity.character.MCCharacterState;
import com.walk.or.die.engine.tiledmap.MCPathfinder;

public class MCESEnemyIdle extends MCCharacterState<MCESIdle.IdleStateArgs> {

    public MCESEnemyIdle(MCCharacter parent) {
        super(parent);
        this.name = "idle";
    }

    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    @Override
    public void enter(MCESIdle.IdleStateArgs args) {
        parent.keep = false;
        parent.playAnimation("idle");
        super.enter(args);
    }

    @Override
    public void exit() {
        parent.keep = true;
        super.exit();
    }
    
    @Override
    protected void inputPressed(MCInputManager.Command data) {
        super.inputPressed(data);
    }

    public void play(MCIntVector2 pos) {
        changeState("click_move", new MCESClickMove.MoveStateArgs(pos, MCPathfinder.get().getPath(parent.getTilePosition(), pos)));
    }

    public void shoot(MCAlly ally, List<MCIntVector2> traj) {
        changeState("shoot", new MCESShoot.ShootStateArgs((MCCharacter)ally, parent.getAttack(), traj));
    }

}
