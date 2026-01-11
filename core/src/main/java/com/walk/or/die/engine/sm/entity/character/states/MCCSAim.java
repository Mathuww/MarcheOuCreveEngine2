package com.walk.or.die.engine.sm.entity.character.states;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCAttack;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCCharacter.HudCustomization;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.sm.entity.character.MCCharacterState;
import com.walk.or.die.engine.tiledmap.MCPathfinder;
import com.walk.or.die.engine.ui.MCUICarousel.CarouselItem;

/**
 * The state that symbolizes when the target to shoot is chosen.<br>
 * Name = "aim"
 */
public class MCCSAim extends MCCharacterState<MCCSAim.AimStateArgs> {
    private MCAttack currentAttack;
    private MCIntVector2 tile = new MCIntVector2(-1, -1);
    /**
     * Class which represents the args needed by the aim state to start.
     */
    public static class AimStateArgs extends MCCharacterState.StateArgs {
        /**
         * Constructor.
         */
        public AimStateArgs() {}
    }

    /**
     * Constructor for MCCSAim.
     * @param parent The parent MCCharacter.
     */
    public MCCSAim(MCCharacter parent) {
        super(parent);
        this.name = "aim";
    }

    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    @Override
    public void render(SpriteBatch batch) {
    }

    @Override
    public void renderEffects(SpriteBatch batch) {
        if (currentAttack != null)
            currentAttack.render(batch);
    }

    @Override
    public void enter(AimStateArgs args) {
        super.enter(args);
        loadAttackChoiceCarousel();
        parent.playAnimation("aim");
        this.bus.emit("connectMouseMoved", new MCInputManager.MouseListener(this::mouseMoved));
        MCInputManager.get().triggerMouseUpdate(); // Initialise la position de la souris
    }

    @Override
    public void exit() {
        if (currentAttack != null)
            currentAttack.display = false;
        tile = new MCIntVector2(-1, -1);
        this.bus.emit("disconnectMouseMoved", null);
        super.exit();
    }

    /**
     * Cancels the current action and returns to the idle state.
     */
    private void cancel() {
        changeState("idle", new MCCSIdle.IdleStateArgs());
    }
    
    /**
     * Processes input pressed events.
     * @param data The input command data.
     */
    @Override
    protected void inputPressed(MCInputManager.Command data) {
        //System.out.println("Input pressed detect in Idle");
        if (data instanceof MCInputManager.ClickTileCommand tileCmd) {
            if (currentAttack == null)
                return;
            MCEntity e = MCEntityManager.get().getEntityFromTile(1, tileCmd.getIntVect());
            if (e != null && e instanceof MCCharacter c) { // plus tard : remplacer par MCEnemy !!!! Mouais
                // a améliorer plus trad !!!
                if (e instanceof MCAlly) {
                    cancel();
                    return;
                }

                MCPathfinder pathfinder = MCPathfinder.get();
                List<MCIntVector2> traj = pathfinder.getBestTrajectory( // A remetter getValidTrajectory si ça marche pas...
                    parent.getTilePosition(),
                    tile);
                if (traj.size() < 2) { // y tires sur soi meme !!!! il est fou ou quoi ????
                    cancel();
                    return;
                }
                if (currentAttack.isValidTile(c.getTilePosition())) {
                    changeState("shoot", new MCCSShoot.ShootStateArgs(c, currentAttack, traj));
                    return;
                }
            }
            cancel();
        }
        else if (!(data instanceof MCInputManager.DirectionalCommand) 
            && !(data instanceof MCInputManager.CameraPanCommand)
            && !(data instanceof MCInputManager.CameraZoomCommand)
            && ! (data instanceof MCInputManager.HudCommand)
        ) {
            cancel();
        }
    }

    /**
     * Handles the mouse moved event.
     * @param pos The current mouse position.
     */
    private void mouseMoved(Vector2 pos) {
        if (currentAttack == null)
            return;
        MCIntVector2 newPos = new MCIntVector2(pos);
        if (!tile.equals(newPos)) {
            tile = newPos;

            if (!currentAttack.isValidTile(tile)) {
                currentAttack.clearTrajectory();
                return;
            }

            List<MCIntVector2> traj = MCPathfinder.get().getBestTrajectory(parent.getTilePosition(), tile); // A remetter getTrajectory si ça marche pas...
            if (traj.size() < 2) {
                currentAttack.clearTrajectory();
                return;
            }

            currentAttack.showTrajectory(traj);
        }
    }
    
    /**
     * Loads the attack choice carousel.
     */
    public void loadAttackChoiceCarousel() {
        if (parent instanceof MCAlly ally) {
            Map<String, MCAttack> attacks = parent.getAttacks();
            HudCustomization customization = ally.getHudCustomization();

            customization.reset();
            customization.carouselItems.add(new CarouselItem(
                "CANCEL", 
                () -> cancel(), 
                () -> {
                    this.currentAttack = null;
                    customization.choiceMessage = "Go back";
                    hudManager.getCharacterHud().refreshRequest(parent, false);
                }
            ));

            for (String attackName : attacks.keySet()) {
                MCAttack newAttack = attacks.get(attackName);
                customization.carouselItems.add(new CarouselItem(
                    attackName,
                    null,
                    () -> {
                        if (this.currentAttack != null)
                            this.currentAttack.display = false;
                        newAttack.computeValidTilesDisplay();
                        newAttack.display = true;
                        this.currentAttack = newAttack;
                        customization.choiceMessage = newAttack.getSummary();
                        hudManager.getCharacterHud().refreshRequest(parent, false);
                    }
                ));
            }
            customization.carouselFirstIndex = 1;
            customization.canShow = true;
            ally.notifyHudUpdate(true);
        }
    }


}