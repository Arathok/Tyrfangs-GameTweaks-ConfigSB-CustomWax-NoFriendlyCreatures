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
    static CtClass ctCreatureTemplateCreator;
    public static void insert() throws NotFoundException, CannotCompileException {
        TyrfangsGameTweaks.logger.log(Level.INFO, "Hooking no friendlies into base Game");
        ctCreatureTemplateCreator = classPool.getCtClass("com.wurmonline.server.creatures.CreatureTemplateCreator");
        //private static void createHyenaLabilaTemplate(int id, String name, String plural, String longDesc, Skills skills) throws IOException {
        ctCreatureTemplateCreator.getMethod("createHyenaLabilaTemplate", "(ILjava/lang/String;java/lang/String;java/lang/String;Lcom/wurmonline/server/skills/Skills;)V").setBody(" {\n" +
                "            skills.learnTemp(102, 20.0F);\n" +
                "            skills.learnTemp(104, 45.0F);\n" +
                "            skills.learnTemp(103, 35.0F);\n" +
                "            skills.learnTemp(100, 8.0F);\n" +
                "            skills.learnTemp(101, 10.0F);\n" +
                "            skills.learnTemp(105, 40.0F);\n" +
                "            skills.learnTemp(106, 2.0F);\n" +
                "            skills.learnTemp(10052, 40.0F);\n" +
                "            int[] types = { 6, 7, 41, 25, 13, 3, 29, 36, 39 };\n" +
                "            CreatureTemplate temp = CreatureTemplateFactory.getInstance().createCreatureTemplate(id, name, plural, longDesc, \"model.creature.quadraped.hyena.rabid\", types, (byte)3, skills, (short)10, (byte)0, (short)40, (short)20, (short)100, \"sound.death.dog\", \"sound.death.dog\", \"sound.combat.hit.dog\", \"sound.combat.hit.dog\", 0.6F, 10.0F, 0.0F, 12.0F, 0.0F, 0.0F, 1.2F, 300, new int[0], 10, 94, (byte)87);\n" +
                "            temp.setHandDamString(\"claw\");\n" +
                "            temp.setAlignment(-50.0F);\n" +
                "            temp.setMaxAge(5);\n" +
                "            temp.setArmourType(ArmourTemplate.ARMOUR_TYPE_CLOTH);\n" +
                "            temp.setBaseCombatRating(14.0F);\n" +
                "            temp.combatDamageType = 1;\n" +
                "            temp.setMaxGroupAttackSize(8);\n" +
                "            temp.setMaxPercentOfCreatures(0.01F);\n" +
                "        }");


        ctCreatureTemplateCreator = classPool.getCtClass("com.wurmonline.server.creatures.CreatureTemplateCreator");
        //private static void createBoarFoTemplate(int id, String name, String plural, String longDesc, Skills skills) throws IOException {{
        ctCreatureTemplateCreator.getMethod("createBoarFoTemplate", "(ILjava/lang/String;java/lang/String;java/lang/String;Lcom/wurmonline/server/skills/Skills;)V").setBody("{\n" +
                "    skills.learnTemp(102, 30.0F);\n" +
                "    skills.learnTemp(104, 35.0F);\n" +
                "    skills.learnTemp(103, 40.0F);\n" +
                "    skills.learnTemp(100, 2.0F);\n" +
                "    skills.learnTemp(101, 8.0F);\n" +
                "    skills.learnTemp(105, 34.0F);\n" +
                "    skills.learnTemp(106, 3.0F);\n" +
                "    skills.learnTemp(10052, 40.0F);\n" +
                "    int[] types = {6, 7, 41, 13, 3, 27, 36, 39 };\n" +
                "    CreatureTemplate temp = CreatureTemplateFactory.getInstance().createCreatureTemplate(id, name, plural, longDesc, \"model.creature.quadraped.boar.wild\", types, (byte)3, skills, (short)10, (byte)0, (short)50, (short)50, (short)150, \"sound.death.pig\", \"sound.death.pig\", \"sound.combat.hit.pig\", \"sound.combat.hit.pig\", 0.6F, 6.0F, 0.0F, 7.0F, 10.0F, 0.0F, 1.2F, 300, new int[] { 92, 140, 303 }, 10, 94, (byte)84);\n" +
                "    temp.setHandDamString(\"kick\");\n" +
                "    temp.setAlignment(10.0F);\n" +
                "    temp.setMaxAge(5);\n" +
                "    temp.setArmourType(ArmourTemplate.ARMOUR_TYPE_CLOTH);\n" +
                "    temp.setBaseCombatRating(14.0F);\n" +
                "    temp.combatDamageType = 0;\n" +
                "    temp.setMaxGroupAttackSize(4);\n" +
                "    temp.setMaxPercentOfCreatures(0.01F);\n" +
                "  }");

        ctCreatureTemplateCreator = classPool.getCtClass("com.wurmonline.server.creatures.CreatureTemplateCreator");
        // private static void createGorillaMagranonTemplate(int id, String name, String plural, String longDesc, Skills skills) throws IOException {
        ctCreatureTemplateCreator.getMethod("createGorillaMagranonTemplate", "(ILjava/lang/String;java/lang/String;java/lang/String;Lcom/wurmonline/server/skills/Skills;)V").setBody("{\n" +
                "    skills.learnTemp(102, 40.0F);\n" +
                "    skills.learnTemp(104, 25.0F);\n" +
                "    skills.learnTemp(103, 40.0F);\n" +
                "    skills.learnTemp(100, 8.0F);\n" +
                "    skills.learnTemp(101, 10.0F);\n" +
                "    skills.learnTemp(105, 30.0F);\n" +
                "    skills.learnTemp(106, 7.0F);\n" +
                "    skills.learnTemp(10052, 40.0F);\n" +
                "    int[] types = {6, 7,  13, 3, 30, 27, 36, 39, 45 };\n" +
                "    CreatureTemplate temp = CreatureTemplateFactory.getInstance().createCreatureTemplate(id, name, plural, longDesc, \"model.creature.humanoid.gorilla.mountain\", types, (byte)0, skills, (short)10, (byte)0, (short)210, (short)50, (short)50, \"sound.death.gorilla\", \"sound.death.gorilla\", \"sound.combat.hit.gorilla\", \"sound.combat.hit.gorilla\", 0.6F, 6.0F, 0.0F, 10.0F, 0.0F, 0.0F, 1.2F, 300, new int[] { 303, 308, 308 }, 10, 94, (byte)78);\n" +
                "    temp.setHandDamString(\"claw\");\n" +
                "    temp.setAlignment(10.0F);\n" +
                "    temp.setMaxAge(10);\n" +
                "    temp.setArmourType(ArmourTemplate.ARMOUR_TYPE_CLOTH);\n" +
                "    temp.setBaseCombatRating(14.0F);\n" +
                "    temp.combatDamageType = 0;\n" +
                "    temp.setMaxGroupAttackSize(6);\n" +
                "    temp.hasHands = true;\n" +
                "    temp.setMaxPercentOfCreatures(0.01F);\n" +
                "  }");

    }
}




