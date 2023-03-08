package org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.whiskyHeals;

import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WhiskyHealsBehaviour implements BehaviourProvider {



    private final List<ActionEntry> desinfect;

    private final WhiskyHealsPerformer whiskyHealsPerformer;

    public WhiskyHealsBehaviour() {
        this.whiskyHealsPerformer = new WhiskyHealsPerformer();
        this.desinfect = Collections.singletonList(whiskyHealsPerformer.actionEntry);
        ModActions.registerActionPerformer(whiskyHealsPerformer);

    }

    //, , , , ,
    //, , , , ;

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item target) {

        if (target.getTemplateId()== WhiskyItems.gauzeId) {

            return new ArrayList<>(desinfect);
        }
        else
            return null;

    }


}



