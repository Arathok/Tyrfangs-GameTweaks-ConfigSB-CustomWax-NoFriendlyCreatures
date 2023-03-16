package org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.waxing;

import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WaxingBehavior implements BehaviourProvider {


    private final List<ActionEntry> wax;
    private final List<ActionEntry> unwax;
    private final WaxingPerformer waxingPerformer;
    private final UnWaxingPerformer unWaxingPerformer;

    public WaxingBehavior() {
        this.waxingPerformer = new WaxingPerformer();
        this.unWaxingPerformer = new UnWaxingPerformer();
        this.wax = Collections.singletonList(waxingPerformer.actionEntry);
        this.unwax = Collections.singletonList(unWaxingPerformer.actionEntry);
        ModActions.registerActionPerformer(waxingPerformer);
        ModActions.registerActionPerformer(unWaxingPerformer);

    }

    //, , , , ,
    //, , , , ;

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer,Item source, Item target) {

        if (WaxingPerformer.waxedItems.contains(target.getWurmId())) {

                return new ArrayList<>(unwax);
        }
        else
            if(source.getTemplateId()== ItemList.beeswax)
            return new ArrayList<>(wax);

        return null;
    }


}
