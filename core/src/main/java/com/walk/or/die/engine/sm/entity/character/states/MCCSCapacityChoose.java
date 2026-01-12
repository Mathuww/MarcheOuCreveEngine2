package com.walk.or.die.engine.sm.entity.character.states;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.capacities.MCEffects;
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
 * The state that symbolizes when the capacity to use is chosen.<br>
 * Name = "capacityChoose"
 */
public class MCCSCapacityChoose extends MCCharacterState<MCCSCapacityChoose.CapaChooseArgs> {
    private MCEffects currentCapacity;
    private MCIntVector2 tile = new MCIntVector2(-1, -1);
    /**
     * Class which represents the args needed by the capa. choose state to start.
     */
    public static class CapaChooseArgs extends MCCharacterState.StateArgs {
        /**
         * Constructor.
         */
        public CapaChooseArgs() {}
    }

    /**
     * Constructor for MCCSCapacityChoose.
     * @param parent The parent MCCharacter.
     */
    public MCCSCapacityChoose(MCCharacter parent) {
        super(parent);
        this.name = "capacityChoose";
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
        if (currentCapacity != null)
            currentCapacity.render(batch);
    }

    @Override
    public void enter(CapaChooseArgs args) {
        super.enter(args);
        loadCapacityChoiceCarousel();

    }

    @Override
    public void exit() {
        /* if (currentCapacity != null)
            currentCapacity.display = false; */
        tile = new MCIntVector2(-1, -1);
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
    }
    
    /**
     * Loads the capacity choice carousel.
     */
    public void loadCapacityChoiceCarousel() {
        if (parent instanceof MCAlly ally) {
            List<MCEffects> capacities = parent.getLaunchableEffects();
            HudCustomization customization = ally.getHudCustomization();

            customization.reset();
            customization.carouselItems.add(new CarouselItem(
                "CANCEL", 
                () -> cancel(), 
                () -> {
                    this.currentCapacity = null;
                    customization.choiceMessage = "Go back";
                    hudManager.getCharacterHud().refreshRequest(parent, false);
                }
            ));

            for (MCEffects newCapa : capacities) {
                customization.carouselItems.add(new CarouselItem(
                    newCapa.getDisplayName(),
                    () -> {
                        ally.getTurnState().capacityUsed();
                        // perform l'effet
                        List<MCCharacter> affectedCharacters = 
                            newCapa.getAffectedCharactersFrom(parent);
                        for (MCCharacter c : affectedCharacters) {
                            //System.out.println("adding effect " + newCapa.getClass().getName() + " to  " + c.getId());
                            c.addEffect(newCapa.copy(c));
                        }
                        changeState("idle", new MCCSIdle.IdleStateArgs());
                    },
                    () -> {
                        customization.choiceMessage = newCapa.getSummary();
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