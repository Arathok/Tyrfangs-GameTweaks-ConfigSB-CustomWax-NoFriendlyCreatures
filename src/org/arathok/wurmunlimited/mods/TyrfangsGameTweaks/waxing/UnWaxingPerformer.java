package org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.waxing;

import com.wurmonline.server.FailedException;
import com.wurmonline.server.Items;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.items.NoSuchTemplateException;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.Config;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.TyrfangsGameTweaks;

import org.gotti.wurmunlimited.modsupport.actions.ActionEntryBuilder;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.sql.SQLException;

public class UnWaxingPerformer implements ActionPerformer {


    public ActionEntry actionEntry;

    public UnWaxingPerformer(){



        actionEntry = new ActionEntryBuilder((short) ModActions.getNextActionId(), "unwax", "removing wax", new int[]{
                6 /* ACTION_TYPE_NOMOVE */,
                48 /* ACTION_TYPE_ENEMY_ALWAYS */,
                35 /* Don't Care activated TARGET */,

        }).range(4).build();

        ModActions.registerAction(actionEntry);
    }





    @Override
    public short getActionId() {
        return actionEntry.getNumber();
    }

    public static boolean canUse(Creature performer, Item target) {

        return performer.isPlayer() && target.getOwnerId() == performer.getWurmId() && !target.isTraded()&& WaxingPerformer.waxedItems.contains(target.getWurmId());
    }


    @Override
    public boolean action(Action action, Creature performer, Item target, short num, float counter) { // Since we use target and source this time, only need that override
        if (!canUse(performer,target)) {
            performer.getCommunicator().sendAlertServerMessage("You are not allowed to do that");
            return propagate(action,
                    ActionPropagation.FINISH_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }


// EFFECT STUFF GOES HERE
        Items.destroyItem(target.getWurmId());
        performer.getCommunicator().sendSafeServerMessage("You peel off the layer of wax and the item crumbles to dust.");

        return propagate(action,
                    ActionPropagation.FINISH_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);



    }

    @Override
    public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter)
    {
        return action(action, performer, target, num, counter);
    } // NEEDED OR THE ITEM WILL ONLY ACTIVATE IF YOU HAVE NO ITEM ACTIVE


}
