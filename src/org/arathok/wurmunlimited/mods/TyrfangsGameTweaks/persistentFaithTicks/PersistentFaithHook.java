package org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.persistentFaithTicks;

import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.players.PlayerInfo;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.TyrfangsGameTweaks;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modsupport.ModSupportDb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.logging.Level;


public class PersistentFaithHook {

    static ClassPool classPool = HookManager.getInstance().getClassPool();
    static CtClass ctPlayerInfo;
    public static HashMap<Long, Long> listOfLastFaithTicks = new HashMap<>();
    long time = 0;
    long playerId = 0;
    long timeOfNextTick = 0;
    Connection dbConn;

    public void insertAfter(PlayerInfo thisPlayerInfo) throws SQLException // Insert this after the function
    {
        ZonedDateTime now = ZonedDateTime.now();
        LocalDate tomorrow = ZonedDateTime.now().toLocalDate().plusDays(1);
        ZoneId zone = ZoneId.of("Europe/Berlin");
        ZonedDateTime tomorrowStart = tomorrow.atStartOfDay(zone);
        long duration = java.time.Duration.between(now, tomorrowStart).toMillis();
        time = System.currentTimeMillis();
        if (thisPlayerInfo.numFaith == 5) // if the player already had 4 faith ticks
        {
            playerId = thisPlayerInfo.getPlayerId();

            timeOfNextTick = time + duration; // get their id and calculate time of the next possible faith tick put them in a Hashmap
            dbConn = ModSupportDb.getModSupportDb();
            add(dbConn, playerId, timeOfNextTick);

        }
    }

    public boolean insertBefore(PlayerInfo thisPlayerInfo)  // Insert this before the function
    {

        return listOfLastFaithTicks.containsKey(thisPlayerInfo.getPlayerId())
                && System.currentTimeMillis() < listOfLastFaithTicks.get(thisPlayerInfo.getPlayerId()); // if the player already had 4 faith ticks

    }

    public static void add(Connection dbConn, long playerId, long timeOfNextTick) throws SQLException {
        listOfLastFaithTicks.put(playerId, timeOfNextTick);
        PreparedStatement ps = dbConn.prepareStatement("insert or replace into ArathoksPersistentFaithTicks (playerId,timeOfNextTick) values (?,?)");
        ps.setLong(1, playerId);
        ps.setLong(2, timeOfNextTick);
        ps.executeUpdate();

    }

    public static void readFromDB(Connection dbConn) throws SQLException, NoSuchItemException {
        long playerId;
        long timeofNextTick;
        PreparedStatement ps = dbConn.prepareStatement("SELECT * FROM ArathoksPersistentFaithTicks");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {

            playerId = rs.getLong("playerId"); // liest quasi den Wert von der Spalte
            timeofNextTick = rs.getLong("timeofNextTick"); // liest quasi den Wert von der Spalte


            listOfLastFaithTicks.put(playerId, timeofNextTick);
        }

        TyrfangsGameTweaks.readFaithTicks = true;
        rs.close();
    }

    public static void insert() {
        try {
            ctPlayerInfo = classPool.getCtClass("com.wurmonline.server.players.PlayerInfo");
            //final boolean checkPrayerFaith() {
            TyrfangsGameTweaks.logger.log(Level.INFO, "inserting check for faith tick time");
            ctPlayerInfo.getMethod("checkPrayerFaith", "()Z")
                    .insertBefore("if(TyrfangsGameTweaks.PersistentFaitHook.insertBefore(this))return false;");
            TyrfangsGameTweaks.logger.log(Level.INFO, "inserting adding to the list");
            ctPlayerInfo.getMethod("checkPrayerFaith", "()Z")
                    .insertAfter("TyrfangsGameTweaks.PersistentFaithHook.insertAfter(this);");


        } catch (NotFoundException | CannotCompileException e) {
            throw new RuntimeException(e);
        }

    }
}
