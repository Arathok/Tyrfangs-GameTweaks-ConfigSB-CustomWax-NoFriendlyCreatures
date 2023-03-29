package org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.whiskyHeals;

import com.wurmonline.server.Players;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.bodys.Wound;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.NotOwnedException;
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
    public List<ActionEntry> getBehavioursFor(Creature performer,Item source,Wound target) {

        if (source.getTemplateId()== WhiskyItems.gauzeId) {

            return new ArrayList<>(desinfect);
        }
        else
            return null;

    }

    public List<ActionEntry> getBehavioursFor(Creature performer,Item source,Creature target) {

        if (source.getTemplateId()== WhiskyItems.gauzeId&&target.getBody().getWounds()!=null) {
            return new ArrayList<>(desinfect);
        }
        else
            return null;

    }

    public List<ActionEntry> getBehavioursFor(Creature performer,Item source,Item target) {

        if (source.getTemplateId()== WhiskyItems.gauzeId&& Players.getInstance().getPlayerOrNull(target.getOwnerId()).getBody().getWounds().getWounds()!=null) {

            return new ArrayList<>(desinfect);
        }
        else
            return null;

    }


}



