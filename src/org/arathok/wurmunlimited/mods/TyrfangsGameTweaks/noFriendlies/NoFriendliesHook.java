package org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.noFriendlies;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.TyrfangsGameTweaks;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import java.util.logging.Level;


public class NoFriendliesHook {
    static ClassPool classPool = HookManager.getInstance().getClassPool();
    static CtClass ctDeities;
    public static void insert() throws NotFoundException, CannotCompileException {
        TyrfangsGameTweaks.logger.log(Level.INFO, "Hooking no friendlies into base Game");
        ctDeities = classPool.getCtClass("com.wurmonline.server.deities.Deity");
        //private static void createHyenaLabilaTemplate(int id, String name, String plural, String longDesc, Skills skills) throws IOException {
        ctDeities.getMethod("setBefriendCreature", "(Z)V")
                .setBody("{this.befriendCreature=false}");
        ctDeities.getMethod("setBefriendMonster", "(Z)V")
                .setBody("{this.befriendMonster=false}");
    }


}




