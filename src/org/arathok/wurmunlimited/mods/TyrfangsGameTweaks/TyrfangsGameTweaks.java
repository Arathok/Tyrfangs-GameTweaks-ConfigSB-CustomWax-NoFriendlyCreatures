package org.arathok.wurmunlimited.mods.TyrfangsGameTweaks;


import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.players.Player;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.noFriendlies.NoFriendliesHook;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.sleepBonus.SleepBonusHook;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.waxing.WaxingBehavior;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.waxing.WaxingPerformer;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.whiskyHeals.WhiskyHealsBehaviour;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.whiskyHeals.WhiskyHealsPerformer;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.whiskyHeals.WhiskyItems;
import org.gotti.wurmunlimited.modloader.interfaces.*;
import org.gotti.wurmunlimited.modsupport.ModSupportDb;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TyrfangsGameTweaks implements WurmServerMod, Initable, PreInitable, Configurable, ItemTemplatesCreatedListener, ServerStartedListener, ServerPollListener, PlayerMessageListener {

    public static Logger logger = Logger.getLogger("TyrfangsGameTweaks");
    public static boolean readWaxedItems;
    public Player aHealedPlayer;
    long nextWoundPoll = 0;
    boolean checkDB = false;
    public static Connection dbConn;

    @Override
    public void configure(Properties properties) {
        Config.fixedWaxingCost = Boolean.parseBoolean(properties.getProperty("fixedWaxingCost", "true"));
        Config.whiskyHeals = Boolean.parseBoolean(properties.getProperty("whiskyHeals", "true"));
        Config.sleepMalus = Boolean.parseBoolean(properties.getProperty("sleepMalus", "true"));
        Config.healPerQl = Float.parseFloat(properties.getProperty("healPerQl", "0.05"));
        Config.usageFactor = Float.parseFloat(properties.getProperty("usageFactor", "1"));

    }

    @Override
    public void onItemTemplatesCreated() {

        try {
            WhiskyItems.registerGauze();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onPlayerMessage(Communicator communicator, String message) {

        if (message != null && message.startsWith("#setTar") && communicator.getPlayer().getPower() >= 4) {

            communicator.sendSafeServerMessage("Turning Everything into Tar!");
            //SeedOres.setTar();

        }
        if (message != null && message.startsWith("#seedCaves") && communicator.getPlayer().getPower() >= 4) {

            communicator.sendSafeServerMessage("Making random Caves!");
            // SeedOres.setTar();

        }
        return false;
    }

    @Override
    public void onServerPoll() {


        if (!readWaxedItems) {

            dbConn = ModSupportDb.getModSupportDb();

            try {
                if (!ModSupportDb.hasTable(dbConn, "ArathoksWaxedItems")) {
                    // table create
                    try (PreparedStatement ps = dbConn.prepareStatement("CREATE TABLE ArathoksWaxedItems (itemId LONG PRIMARY KEY NOT NULL DEFAULT 0)")) {
                        ps.execute();
                    } catch (SQLException e) {
                        logger.log(Level.WARNING, "Could not create Table!");
                        throw new RuntimeException(e);
                    }

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                WaxingPerformer.readFromDB(dbConn);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (NoSuchItemException e) {
                e.printStackTrace();
            }
        }


        // Iterator and Heal


    }


    @Override
    public void onServerStarted() {

        ModActions.registerBehaviourProvider(new WaxingBehavior());
        ModActions.registerBehaviourProvider(new WhiskyHealsBehaviour());
    }

    @Override
    public void init() {
        WurmServerMod.super.init();
    }

    @Override
    public void preInit() {
        SleepBonusHook.insert();
        try {
            NoFriendliesHook.insert();
        } catch (NotFoundException | CannotCompileException e) {
            e.printStackTrace();
        }
    }
}
