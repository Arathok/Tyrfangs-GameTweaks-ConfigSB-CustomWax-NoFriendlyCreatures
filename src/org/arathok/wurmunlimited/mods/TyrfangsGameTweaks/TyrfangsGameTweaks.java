package org.arathok.wurmunlimited.mods.TyrfangsGameTweaks;


import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.bodys.Wounds;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.players.Player;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.noFriendlies.NoFriendliesHook;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.persistentFaithTicks.PersistentFaithHook;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.sleepBonus.SleepBonusHook;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.waxing.WaxingBehavior;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.waxing.WaxingPerformer;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.whiskyHeals.WhiskyHealsBehaviour;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.whiskyHeals.WhiskyItems;
import org.gotti.wurmunlimited.modloader.interfaces.*;
import org.gotti.wurmunlimited.modsupport.ModSupportDb;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TyrfangsGameTweaks implements WurmServerMod, PlayerLoginListener, Initable, PreInitable, Configurable, ItemTemplatesCreatedListener, ServerStartedListener, ServerPollListener, PlayerMessageListener {

    public static Logger logger = Logger.getLogger("TyrfangsGameTweaks");
    public static boolean readWaxedItems = false;
    public static Connection dbConn;
    public static boolean readFaithTicks = false;

    @Override
    public void configure(Properties properties) {
        Config.persistentFaithticks = Boolean.parseBoolean(properties.getProperty("persistentFaithticks", "true"));
        Config.customWaxingSystem = Boolean.parseBoolean(properties.getProperty("customWaxingSystem", "true"));
        Config.fixedWaxingCost = Boolean.parseBoolean(properties.getProperty("fixedWaxingCost", "true"));
        Config.whiskyHeals = Boolean.parseBoolean(properties.getProperty("whiskyHeals", "true"));
        Config.sleepMalus = Boolean.parseBoolean(properties.getProperty("sleepMalus", "true"));
        Config.noFriendlies = Boolean.parseBoolean(properties.getProperty("noFriendlies", "true"));
        Config.healPerQl = Float.parseFloat(properties.getProperty("healPerQl", "0.05"));
        Config.usageFactor = Float.parseFloat(properties.getProperty("usageFactor", "1.0"));
        Config.sleepBonusFactor = Float.parseFloat(properties.getProperty("sleepBonusFactor", "1.5"));

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

        if (message != null && message.startsWith("#hurtme") && communicator.getPlayer().getPower() >= 4) {

            communicator.sendSafeServerMessage("AraAra!");
            Player you=communicator.getPlayer();
            you.addWoundOfType(null, (byte) 3,9,true,1.0F,false,10000,0.0F,0.0F,false,false);


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

        if (!readFaithTicks) {

            dbConn = ModSupportDb.getModSupportDb();

            try {
                if (!ModSupportDb.hasTable(dbConn, "ArathoksPersistentFaithTicks")) {
                    // table create
                    try (PreparedStatement ps = dbConn.prepareStatement("CREATE TABLE ArathoksPersistentFaithTicks (playerId LONG PRIMARY KEY NOT NULL DEFAULT 0,timeOfNextTick LONG NOT NULL DEFAULT 0,numTicks LONG NOT NULL DEFAULT 0)")) {
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
                PersistentFaithHook.readFromDB(dbConn);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (NoSuchItemException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public void onServerStarted() {
        if (Config.customWaxingSystem)
        ModActions.registerBehaviourProvider(new WaxingBehavior());
        if (Config.whiskyHeals)
            ModActions.registerBehaviourProvider(new WhiskyHealsBehaviour());
    }

    @Override
    public void init() {
        WurmServerMod.super.init();
    }

    @Override
    public void preInit() {
        if (Config.sleepMalus) {
            logger.log(Level.INFO, "insertingSleepBonus");
            SleepBonusHook.insert();

        }
        if (Config.noFriendlies)
            try {
                logger.log(Level.INFO, "inserting No Friendlies for priests! >:C");
                NoFriendliesHook.insert();
            } catch (NotFoundException | CannotCompileException e) {
                e.printStackTrace();
            }
        if (Config.persistentFaithticks)
            try{
                logger.log(Level.INFO,"inserting persistent Faith ticks");
                PersistentFaithHook.insert();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

    }

    @Override
    public void onPlayerLogin(Player player) {


            logger.log(Level.INFO,"new Player login, rescanning waxed items, for their items!");
            dbConn = ModSupportDb.getModSupportDb();


        dbConn = ModSupportDb.getModSupportDb();



        try {
            WaxingPerformer.readFromDB(dbConn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (NoSuchItemException e) {
            e.printStackTrace();
        }
    }
}
