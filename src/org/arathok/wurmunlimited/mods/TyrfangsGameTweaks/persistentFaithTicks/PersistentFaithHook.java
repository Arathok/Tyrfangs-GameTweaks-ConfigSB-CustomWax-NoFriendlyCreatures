package org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.persistentFaithTicks;

import com.wurmonline.server.NoSuchItemException;
import com.wurmonline.server.Players;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.players.PlayerInfo;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.TyrfangsGameTweaks;
import org.fourthline.cling.support.avtransport.callback.Play;
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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;


public class PersistentFaithHook {

    static ClassPool classPool = HookManager.getInstance().getClassPool();
    static CtClass ctPlayerInfo;
    public static List<PlayerFaithInfo> listOfLastFaithTicks = new LinkedList<>();
    static long time = 0;
    static long playerId = 0;
    static long timeOfNextTick = 0;
    static long numTicks =0;
    static Connection dbConn;

    public static void insertAfter(PlayerInfo thisPlayerInfo) throws SQLException // Insert this after the function
    {
        TyrfangsGameTweaks.logger.log(Level.INFO,"running Insert After Block");
        PlayerFaithInfo pfi=new PlayerFaithInfo();
        ZonedDateTime now = ZonedDateTime.now();
        LocalDate tomorrow = ZonedDateTime.now().toLocalDate().plusDays(1);
        ZoneId zone = ZoneId.of("Europe/Berlin");
        ZonedDateTime tomorrowStart = tomorrow.atStartOfDay(zone);
        long duration = java.time.Duration.between(now, tomorrowStart).toMillis();
        time = System.currentTimeMillis();
        pfi.numTicks=thisPlayerInfo.numFaith;
        int maxFaithnum=0;
        if (thisPlayerInfo.isFlagSet(81))
        {
            maxFaithnum=6;
        }
        else maxFaithnum=5;
        if (thisPlayerInfo.numFaith == maxFaithnum) // if the player already had 4 faith ticks
        {
            pfi.playerId = thisPlayerInfo.getPlayerId();

            pfi.timeOfNextTick = time + duration; // get their id and calculate time of the next possible faith tick put them in a Hashmap
       //     TyrfangsGameTweaks.logger.log(Level.INFO,"next tick"+(time+duration));
      //      TyrfangsGameTweaks.logger.log(Level.INFO,"Adding to DB:" + pfi.playerId +", "+pfi.timeOfNextTick+", "+pfi.numTicks );
            dbConn = ModSupportDb.getModSupportDb();
            add(dbConn, pfi);

        }
        else
        {
            pfi.playerId = thisPlayerInfo.getPlayerId();

            pfi.timeOfNextTick = time; // get their id and calculate time of the next possible faith tick put them in a Hashmap
            dbConn = ModSupportDb.getModSupportDb();
        //    TyrfangsGameTweaks.logger.log(Level.INFO,"Adding to DB:" + pfi.playerId +", "+pfi.timeOfNextTick+", "+pfi.numTicks );
            add(dbConn, pfi);
        }

    }

    public static boolean insertBefore(PlayerInfo thisPlayerInfo)  // Insert this before the function
    {
        boolean playerFound = false;
        long wurmId = thisPlayerInfo.getPlayerId();
        long timeonexttick = 0;
      //  TyrfangsGameTweaks.logger.log(Level.INFO,"looking up player in table of Faith ticks...");
        for (PlayerFaithInfo aPlayerFaithInfo:listOfLastFaithTicks)
        {
            if (aPlayerFaithInfo.playerId==wurmId)
            {
                playerFound=true;
                timeonexttick=aPlayerFaithInfo.timeOfNextTick;
          //      TyrfangsGameTweaks.logger.log(Level.INFO,"found Player "+thisPlayerInfo.getName());
            }
        }
        //TyrfangsGameTweaks.logger.log(Level.INFO,"current Time: "+System.currentTimeMillis()+" next Tick: "+ timeonexttick);
        return playerFound && System.currentTimeMillis() < timeonexttick; // if the player already had 4 faith ticks

    }

    public static void add(Connection dbConn, PlayerFaithInfo pfi) throws SQLException {
        listOfLastFaithTicks.add(pfi);
        PreparedStatement ps = dbConn.prepareStatement("insert or replace into ArathoksPersistentFaithTicks (playerId,timeOfNextTick,numTicks) values (?,?,?)");
        ps.setLong(1, pfi.playerId);
        ps.setLong(2, pfi.timeOfNextTick);
        ps.setLong(3, pfi.numTicks);
        TyrfangsGameTweaks.logger.log(Level.INFO,"Adding to DB:" + pfi.playerId +", "+pfi.timeOfNextTick+", "+pfi.numTicks );
        ps.executeUpdate();

    }

    public static void readFromDB(Connection dbConn) throws SQLException, NoSuchItemException {
        PlayerFaithInfo pfi = new PlayerFaithInfo();
        PreparedStatement ps = dbConn.prepareStatement("SELECT * FROM ArathoksPersistentFaithTicks");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {

            pfi.playerId = rs.getLong("playerId"); // liest quasi den Wert von der Spalte
            pfi.timeOfNextTick = rs.getLong("timeofNextTick"); // liest quasi den Wert von der Spalte
            pfi.numTicks =rs.getLong("numTicks");
            Player p =Players.getInstance().getPlayerOrNull(pfi.playerId);
            PlayerInfo pi = p.getSaveFile();
            pi.numFaith=(byte)pfi.numTicks;
            listOfLastFaithTicks.add(pfi);
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
                    .insertBefore("if(org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.persistentFaithTicks.PersistentFaithHook.insertBefore(this))return false;");
            TyrfangsGameTweaks.logger.log(Level.INFO, "inserting adding to the list");
            ctPlayerInfo.getMethod("checkPrayerFaith", "()Z")
                    .insertAfter("org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.persistentFaithTicks.PersistentFaithHook.insertAfter(this);");


        } catch (NotFoundException | CannotCompileException e) {
            throw new RuntimeException(e);
        }

    }
}
