package org.arathok.wurmunlimited.mods.TyrfangsGameTweaks;


import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.players.Player;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.waxing.WaxingBehavior;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.waxing.WaxingPerformer;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.whiskyHeals.WhiskyHealsPerformer;
import org.gotti.wurmunlimited.modloader.interfaces.*;
import org.gotti.wurmunlimited.modsupport.ModSupportDb;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TyrfangsGameTweaks implements WurmServerMod, Initable, PreInitable, Configurable, ItemTemplatesCreatedListener, ServerStartedListener, ServerPollListener, PlayerMessageListener {
    public static Connection dbConn;
    public static Logger logger = Logger.getLogger("TyrfangsGameTweaks");
    public static boolean readWaxedItems;
    public Player aHealedPlayer;

    @Override
    public void configure(Properties properties) {
        Config.fixedWaxingCost = Boolean.parseBoolean(properties.getProperty("fixedWaxingCost", "true"));
    }

    @Override
    public void onItemTemplatesCreated() {

    }

    @Override
    public boolean onPlayerMessage(Communicator communicator, String message) {
        return false;
        if (message != null && message.startsWith("#setTar") && communicator.getPlayer().getPower() >= 4) {

            communicator.sendSafeServerMessage("Turning Everything into Tar!");
            SeedOres.setTar();

        }
        if (message != null && message.startsWith("#seedCaves") && communicator.getPlayer().getPower() >= 4) {

            communicator.sendSafeServerMessage("Making random Caves!");
            SeedOres.setTar();

        }
    }

    @Override
    public void onServerPoll() {
        long playerId = 0L;
        if (!readWaxedItems) {
            try {
                WaxingPerformer.readFromDB(dbConn);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

                // Iterator and Heal
                Iterator<Long> healedPlayersIterator = WhiskyHealsPerformer.healedPlayers.iterator();


                while (healedPlayersIterator.hasNext()) {

                    playerId= healedPlayersIterator.next();





            }
        }



    @Override
    public void onServerStarted() {
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
        ModActions.registerBehaviourProvider(new WaxingBehavior());
    }

    @Override
    public void init() {
        WurmServerMod.super.init();
    }

    @Override
    public void preInit() {
        WurmServerMod.super.preInit();
    }
}
