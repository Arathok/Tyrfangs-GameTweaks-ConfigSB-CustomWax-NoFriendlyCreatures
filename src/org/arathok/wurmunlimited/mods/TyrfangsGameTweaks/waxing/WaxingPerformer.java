package org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.waxing;

import com.wurmonline.server.Items;
import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.Config;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.TyrfangsGameTweaks;

import org.gotti.wurmunlimited.modsupport.actions.ActionEntryBuilder;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class WaxingPerformer implements ActionPerformer {

    public static List<Long> waxedItems = new LinkedList<>();
    public ActionEntry actionEntry;

    public WaxingPerformer() {


        actionEntry = new ActionEntryBuilder((short) ModActions.getNextActionId(), "wax", "waxing", new int[]{
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

    public static boolean canUse(Creature performer, Item source, Item target) {

        return performer.isPlayer() && source.getOwnerId() == performer.getWurmId() && !source.isTraded() && source.getTemplateId() == ItemList.beeswax
                &&(target.isFood()||target.isCooked()||target.isHerb()||target.isSpice());
    }


    @Override
    public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) { // Since we use target and source this time, only need that override
        if (!canUse(performer, source,target)) {
            performer.getCommunicator().sendAlertServerMessage("You are not allowed to do that");
            return propagate(action,
                    ActionPropagation.FINISH_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }


// EFFECT STUFF GOES HERE
        float weight = ((target.getVolume() * 0.95F) / 4); // Config
        if (Config.fixedWaxingCost)
            weight = 10;
        if (weight < 1.0F)
            weight = 1.01F;
        if (source.getWeightGrams() > (int) weight) {

            source.setWeight(source.getWeightGrams() - (int) weight, true);

        } else {
            performer.getCommunicator().sendSafeServerMessage("You don't have enough wax to encase this item!");
            return propagate(action,
                    ActionPropagation.FINISH_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);

        }

        target.setHasNoDecay(true);
        target.setIsNoEatOrDrink(true);
        target.sendUpdate();
        target.savePermissions();
        waxedItems.add(target.getWurmId());
        try {
            add(TyrfangsGameTweaks.dbConn, target.getWurmId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return propagate(action,
                ActionPropagation.FINISH_ACTION,
                ActionPropagation.NO_SERVER_PROPAGATION,
                ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
    }


    public static void readFromDB(Connection dbConn) throws SQLException, NoSuchItemException {
        long itemId;
        PreparedStatement ps = dbConn.prepareStatement("SELECT * FROM ArathoksWaxedItems");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {

            itemId = rs.getLong("itemId"); // liest quasi den Wert von der Spalte

            Optional<Item> test = Items.getItemOptional(itemId);

            if (test.isPresent()) {
                waxedItems.add(itemId);
            }


        }
        TyrfangsGameTweaks.readWaxedItems = true;
        rs.close();
    }


    public static void add(Connection dbConn, long itemId) throws SQLException {
        waxedItems.add(itemId);
        PreparedStatement ps = dbConn.prepareStatement("insert or replace into ArathoksWaxedItems (itemID) values (?)");
        ps.setLong(1, itemId);
        ps.executeUpdate();

    }

    public static void remove(Connection dbConn, long itemId) throws SQLException {
        waxedItems.remove(itemId);
        PreparedStatement psDeleteRow = dbConn.prepareStatement("DELETE FROM ArathoksWaxedItems WHERE itemId = " + itemId);
        psDeleteRow.execute();
        psDeleteRow.close();
    }

}
