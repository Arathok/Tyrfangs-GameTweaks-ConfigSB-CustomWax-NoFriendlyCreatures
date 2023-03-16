package org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.whiskyHeals;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.bodys.Wounds;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.Config;
import org.gotti.wurmunlimited.modsupport.actions.ActionEntryBuilder;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

public class WhiskyHealsPerformer implements ActionPerformer {

    public ActionEntry actionEntry;
    int  lastsecond=0;
    int realHeal=0;
    float maxhealingPool = 0;
    float healingPerTick=0;
    Wound theWorstWound;
    public WhiskyHealsPerformer() {


        actionEntry = new ActionEntryBuilder((short) ModActions.getNextActionId(), "desinfect wound", "desinfecting", new int[]{
                6 /* ACTION_TYPE_NOMOVE */,
                48 /* ACTION_TYPE_ENEMY_ALWAYS */,
                36 /* USE SOURCE AND TARGET */,

        }).range(4).build();

        ModActions.registerAction(actionEntry);
    }


    @Override
    public short getActionId() {
        return actionEntry.getNumber();
    }

    public static boolean canUse(Creature performer, Item source) {

        return performer.isPlayer() && source.getOwnerId() == performer.getWurmId() && !source.isTraded() && source.getTemplateId() == WhiskyItems.gauzeId;
    }

// Performer for selecting a single wound.
    @Override
    public boolean action(Action action, Creature performer, Item source, Wound target, short num, float counter) { // Since we use target and source this time, only need that override
        if (!canUse(performer, source)) {
            performer.getCommunicator().sendAlertServerMessage("You are not allowed to do that");
            return propagate(action,
                    ActionPropagation.FINISH_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }


// EFFECT STUFF GOES HERE
        float usedUpGauze=100*Config.usageFactor;
        if(source.getWeightGrams()<usedUpGauze)
        {
            performer.getCommunicator().sendSafeServerMessage("You don't have enough disinfecting gauze to treat the wound!");
            return propagate(action,
                    ActionPropagation.FINISH_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);

        }
        if (counter == 1.0F) {

            performer.getCommunicator().sendSafeServerMessage("You start desinfecting the wound.");
            maxhealingPool = (source.getCurrentQualityLevel() * Config.healPerQl)*10;
            healingPerTick= maxhealingPool/10.0F;
            realHeal = ((int) healingPerTick)*(635);
            target.modifySeverity(-realHeal);

            performer.sendActionControl(action.getActionEntry().getActionString(),true,100); // tenths of seconds
            action.setTimeLeft(100);
            source.setWeight(source.getWeightGrams()-(int)usedUpGauze,true);
            return propagate(action,
                    ActionPropagation.CONTINUE_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }


        if (action.currentSecond()>lastsecond)
        {


            target.modifySeverity(-realHeal);

            return propagate(action,
                    ActionPropagation.CONTINUE_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);

        }

        if (action.currentSecond()>=10)
        {
            return propagate(action,
                    ActionPropagation.FINISH_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }
        lastsecond=action.currentSecond();
        return propagate(action,
                ActionPropagation.CONTINUE_ACTION,
                ActionPropagation.NO_SERVER_PROPAGATION,
                ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
    }

    // Performer for oneself clicking the bodySilouette
    @Override
    public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) { // Since we use target and source this time, only need that override

        if (!canUse(performer, source)&&!target.isBodyPart()) {
            performer.getCommunicator().sendAlertServerMessage("You are not allowed to do that");
            return propagate(action,
                    ActionPropagation.FINISH_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }

// EFFECT STUFF GOES HERE
        float usedUpGauze=100*Config.usageFactor;
        if(source.getWeightGrams()<usedUpGauze)
        {
            performer.getCommunicator().sendSafeServerMessage("You don't have enough disinfecting gauze to treat the wound!");
            return propagate(action,
                    ActionPropagation.FINISH_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);

        }
        if (counter == 1.0F) {
            if(performer.getBody().getWounds().getWounds()!=null) {
                theWorstWound = performer.getBody().getWounds().getWounds()[1];   //first pick any wound of the performer
                for (Wound aWound : performer.getBody().getWounds().getWounds())  // check all wounds
                {
                    if (aWound.getSeverity() > theWorstWound.getSeverity())        // and if they are worse than the current picked wound replace that
                        theWorstWound = aWound;                                   // result is a wound that's definitely the worst
                }
            }


            performer.getCommunicator().sendSafeServerMessage("You start desinfecting the wound.");
            maxhealingPool = (source.getCurrentQualityLevel() * Config.healPerQl)*10;
            healingPerTick= maxhealingPool/10.0F;
            realHeal = ((int) healingPerTick)*(635);
            theWorstWound.modifySeverity(-realHeal);

            performer.sendActionControl(action.getActionEntry().getActionString(),true,100); // tenths of seconds
            action.setTimeLeft(100);
            source.setWeight(source.getWeightGrams()-(int)usedUpGauze,true);
            return propagate(action,
                    ActionPropagation.CONTINUE_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }


        if (action.currentSecond()>lastsecond)
        {


            theWorstWound.modifySeverity(-realHeal);

            return propagate(action,
                    ActionPropagation.CONTINUE_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);

        }

        if (action.currentSecond()>=10)
        {
            return propagate(action,
                    ActionPropagation.FINISH_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }
        lastsecond=action.currentSecond();
        return propagate(action,
                ActionPropagation.CONTINUE_ACTION,
                ActionPropagation.NO_SERVER_PROPAGATION,
                ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
    }

    // Performer for oneself clicking another player
    @Override
    public boolean action(Action action, Creature performer, Item source, Creature target, short num, float counter) { // Since we use target and source this time, only need that override

        if (!canUse(performer, source)) {
            performer.getCommunicator().sendAlertServerMessage("You are not allowed to do that");
            return propagate(action,
                    ActionPropagation.FINISH_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }

// EFFECT STUFF GOES HERE
        float usedUpGauze=100*Config.usageFactor;
        if(source.getWeightGrams()<usedUpGauze)
        {
            performer.getCommunicator().sendSafeServerMessage("You don't have enough disinfecting gauze to treat the wound!");
            return propagate(action,
                    ActionPropagation.FINISH_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);

        }
        if (counter == 1.0F) {

            theWorstWound= target.getBody().getWounds().getWounds()[1];   //first pick any wound of the performer
            for (Wound aWound:target.getBody().getWounds().getWounds())  // check all wounds
            {
                if(aWound.getSeverity()>theWorstWound.getSeverity())        // and if they are worse than the current picked wound replace that
                    theWorstWound=aWound;                                   // result is a wound that's definitely the worst
            }

            performer.getCommunicator().sendSafeServerMessage("You start desinfecting the wound.");
            maxhealingPool = (source.getCurrentQualityLevel() * Config.healPerQl)*10;
            healingPerTick= maxhealingPool/10.0F;
            realHeal = ((int) healingPerTick)*(635);
            theWorstWound.modifySeverity(-realHeal);

            performer.sendActionControl(action.getActionEntry().getActionString(),true,100); // tenths of seconds
            action.setTimeLeft(100);
            source.setWeight(source.getWeightGrams()-(int)usedUpGauze,true);
            return propagate(action,
                    ActionPropagation.CONTINUE_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }


        if (action.currentSecond()>lastsecond)
        {


            theWorstWound.modifySeverity(-realHeal);

            return propagate(action,
                    ActionPropagation.CONTINUE_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);

        }

        if (action.currentSecond()>=10)
        {
            return propagate(action,
                    ActionPropagation.FINISH_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }
        lastsecond=action.currentSecond();
        return propagate(action,
                ActionPropagation.CONTINUE_ACTION,
                ActionPropagation.NO_SERVER_PROPAGATION,
                ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
    }

}



