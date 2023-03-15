package org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.sleepBonus;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.Config;
import org.arathok.wurmunlimited.mods.TyrfangsGameTweaks.TyrfangsGameTweaks;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import java.util.logging.Level;

public class SleepBonusHook {
    static ClassPool classPool = HookManager.getInstance().getClassPool();
    static CtClass ctSkill;

    public static void insert() {
        /*
        B             byte          signed byte
        C             char          Unicode character
         D             double        double-precision floating-point value
        F             float         single-precision floating-point value
        I             int           integer
        J             long          long integer
        L<classname>; reference     an instance of class
        S             short         signed short
        Z             boolean       true or false
        [             reference     one array dimension
         */
        TyrfangsGameTweaks.logger.log(Level.INFO, "Hooking Sleepbonus half efficency into base Game");
        try {
            ctSkill = classPool.getCtClass("com.wurmonline.server.skills.Skill");
            //protected void alterSkill(double advanceMultiplicator, boolean decay, float times, boolean useNewSystem, double skillDivider) {
            ctSkill.getMethod("alterSkill", "(DZFZD)V")
                    .setBody("{ if (this.parent.hasSkillGain) {\n" +
                            "      $3 = Math.min((com.wurmonline.server.skills.SkillSystem.getTickTimeFor(getNumber()) > 0L || \n" +
                            "          getNumber() == 10033) ? 100.0F : 30.0F, $3);\n" +
                            "      $1 *= ($3 * com.wurmonline.server.Servers.localServer.getSkillGainRate());\n" +
                            "      this.lastUsed = System.currentTimeMillis();\n" +
                            "      boolean isplayer = false;\n" +
                            "      long pid = this.parent.getId();\n" +
                            "      if (com.wurmonline.server.WurmId.getType(pid) == 0)\n" +
                            "        isplayer = true; \n" +
                            "      double oldknowledge = this.knowledge;\n" +
                            "      if ($2) {\n" +
                            "        if (isplayer) {\n" +
                            "          if (this.knowledge <= 70.0D)\n" +
                            "            return; \n" +
                            "          double villageMod = 1.0D;\n" +
                            "          try {\n" +
                            "            com.wurmonline.server.players.Player player = com.wurmonline.server.Players.getInstance().getPlayer(pid);\n" +
                            "            villageMod = player.getVillageSkillModifier();\n" +
                            "          } catch (com.wurmonline.server.NoSuchPlayerException nsp) {\n" +
                            "            logger.log(java.util.logging.Level.WARNING, \"Player with id \" + this.id + \" is decaying skills while not online?\", (Throwable)nsp);\n" +
                            "          } \n" +
                            "          this.knowledge = Math.max(1.0D, this.knowledge + $1 * villageMod);\n" +
                            "        } else {\n" +
                            "          this.knowledge = Math.max(1.0D, this.knowledge + $1);\n" +
                            "        } \n" +
                            "      } else {\n" +
                            "        $1 *= skillMod;\n" +
                            "        if (this.number == 10086 && com.wurmonline.server.Servers.localServer.isChallengeOrEpicServer() && \n" +
                            "          !com.wurmonline.server.Server.getInstance().isPS())\n" +
                            "          $1 *= 2.0D; \n" +
                            "        if (isplayer)\n" +
                            "          try {\n" +
                            "            com.wurmonline.server.players.Player player = com.wurmonline.server.Players.getInstance().getPlayer(pid);\n" +
                            "            $1 *= (1.0F + com.wurmonline.server.players.ItemBonus.getSkillGainBonus((com.wurmonline.server.creatures.Creature)player, getNumber()));\n" +
                            "            int currstam = player.getStatus().getStamina();\n" +
                            "            float staminaMod = 1.0F;\n" +
                            "            if (currstam <= 400)\n" +
                            "              staminaMod = 0.1F; \n" +
                            "            if (player.getCultist() != null && player.getCultist().levelElevenSkillgain())\n" +
                            "              staminaMod *= 1.25F; \n" +
                            "            if (player.getDeity() != null) {\n" +
                            "              if (player.mustChangeTerritory() && !player.isFighting()) {\n" +
                            "                staminaMod = 0.1F;\n" +
                            "                if (com.wurmonline.server.Server.rand.nextInt(100) == 0)\n" +
                            "                  player.getCommunicator().sendAlertServerMessage(\"You sense a lack of energy. Rumours have it that \" + \n" +
                            "                      (player.getDeity()).name + \" wants \" + player\n" +
                            "                      .getDeity().getHisHerItsString() + \" champions to move between kingdoms and seek out the enemy.\"); \n" +
                            "              } \n" +
                            "              if (player.getDeity().isLearner()) {\n" +
                            "                if (player.getFaith() > 20.0F && player.getFavor() >= 10.0F)\n" +
                            "                  staminaMod += 0.1F; \n" +
                            "              } else if (player.getDeity().isWarrior()) {\n" +
                            "                if (player.getFaith() > 20.0F && player.getFavor() >= 20.0F)\n" +
                            "                  if (isFightingSkill())\n" +
                            "                    staminaMod += 0.25F;  \n" +
                            "              } \n" +
                            "            } \n" +
                            "            staminaMod += Math.max(player.getStatus().getNutritionlevel() / 10.0F - 0.05F, 0.0F);\n" +
                            "            if (player.isFighting() && currstam <= 400)\n" +
                            "              staminaMod = 0.0F; \n" +
                            "            $1 *= staminaMod;\n" +
                            "            if (player.getEnemyPresense() > com.wurmonline.server.players.Player.minEnemyPresence && \n" +
                            "              !ignoresEnemy())\n" +
                            "              $1 *= 0.800000011920929D; \n" +
                            "            if (this.knowledge < this.minimum || (this.basicPersonal && this.knowledge < 20.0D))\n" +
                            "              $1 *= 3.0D; \n" +
                            "            if (player.hasSleepBonus())\n" +
                            "              $1 *= "+ Config.sleepBonusFactor+"; \n" + // Hier Sleep Bonus!Faktor
                            "            int taffinity = this.affinity + (com.wurmonline.server.skills.AffinitiesTimed.isTimedAffinity(pid, getNumber()) ? 1 : 0);\n" +
                            "            $1 *= (1.0F + taffinity * 0.1F);\n" +
                            "            if ((player.getMovementScheme()).samePosCounts > 20)\n" +
                            "              $1 = 0.0D; \n" +
                            "            if (!player.isPaying() && this.knowledge >= 20.0D) {\n" +
                            "              $1 = 0.0D;\n" +
                            "              if (!player.isPlayerAssistant() && com.wurmonline.server.Server.rand.nextInt(500) == 0)\n" +
                            "                player.getCommunicator().sendNormalServerMessage(\"You may only gain skill beyond level 20 if you have a premium account.\", (byte)2); \n" +
                            "            } \n" +
                            "            if (this.number == 10055 || this.number == 10053 || this.number == 10054)\n" +
                            "              if (player.loggerCreature1 > 0L)\n" +
                            "                logger.log(java.util.logging.Level.INFO, player\n" +
                            "                    \n" +
                            "                    .getName() + \" advancing \" + \n" +
                            "                    Math.min(1.0D, $1 * this.knowledge / $5) + \"!\");  \n" +
                            "          } catch (com.wurmonline.server.NoSuchPlayerException nsp) {\n" +
                            "            $1 = 0.0D;\n" +
                            "            logger.log(java.util.logging.Level.WARNING, \"Player with id \" + this.id + \" is learning skills while not online?\", (Throwable)nsp);\n" +
                            "          }  \n" +
                            "        if ($4) {\n" +
                            "          double maxSkillRate = 40.0D;\n" +
                            "          double rateMod = 1.0D;\n" +
                            "          short sType = com.wurmonline.server.skills.SkillSystem.getTypeFor(this.number);\n" +
                            "          if (sType == 1 || sType == 0) {\n" +
                            "            maxSkillRate = 60.0D;\n" +
                            "            rateMod = 0.8D;\n" +
                            "          } \n" +
                            "          double skillRate = Math.min(maxSkillRate, $5 * (1.0D + this.knowledge / (100.0D - 90.0D * this.knowledge / 110.0D)) * rateMod);\n" +
                            "          this\n" +
                            "            .knowledge = Math.max(1.0D, this.knowledge + Math.min(1.0D, $1 * this.knowledge / skillRate));\n" +
                            "        } else {\n" +
                            "          this.knowledge = Math.max(1.0D, this.knowledge + Math.min(1.0D, $1 * this.knowledge));\n" +
                            "        } \n" +
                            "        if (this.minimum < this.knowledge)\n" +
                            "          this.minimum = this.knowledge; \n" +
                            "        checkTitleChange(oldknowledge, this.knowledge);\n" +
                            "      } \n" +
                            "      try {\n" +
                            "        if ((oldknowledge != this.knowledge && (this.saveCounter == 0 || this.knowledge > 50.0D)) || $2)\n" +
                            "          saveValue(isplayer); \n" +
                            "        this.saveCounter = (byte)(this.saveCounter + 1);\n" +
                            "        if (this.saveCounter == 10)\n" +
                            "          this.saveCounter = 0; \n" +
                            "      } catch (java.io.IOException ex) {\n" +
                            "        logger.log(java.util.logging.Level.WARNING, \"Failed to save skill \" + \n" +
                            "            getName() + \"(\" + getNumber() + \") for creature \" + this.parent.getId(), ex);\n" +
                            "      } \n" +
                            "      if (pid != -10L)\n" +
                            "        if (isplayer)\n" +
                            "          try {\n" +
                            "            com.wurmonline.server.players.Player holder = com.wurmonline.server.Players.getInstance().getPlayer(pid);\n" +
                            "            float weakMod = 1.0F;\n" +
                            "            double bonusKnowledge = this.knowledge;\n" +
                            "            float ws = holder.getBonusForSpellEffect((byte)41);\n" +
                            "            if (ws > 0.0F)\n" +
                            "              weakMod = 0.8F; \n" +
                            "            if (this.number == 102 && this.knowledge < 40.0D) {\n" +
                            "              float x = holder.getBonusForSpellEffect((byte)25);\n" +
                            "              if (x > 0.0F) {\n" +
                            "                double diff = 40.0D - this.knowledge;\n" +
                            "                bonusKnowledge = this.knowledge + diff * x / 100.0D;\n" +
                            "              } else {\n" +
                            "                float hs = holder.getBonusForSpellEffect((byte)40);\n" +
                            "                if (hs > 0.0F) {\n" +
                            "                  double diff = 40.0D - this.knowledge;\n" +
                            "                  bonusKnowledge = this.knowledge + diff * hs / 100.0D;\n" +
                            "                } \n" +
                            "              } \n" +
                            "            } \n" +
                            "            bonusKnowledge *= weakMod;\n" +
                            "            if (isplayer) {\n" +
                            "              int diff = (int)this.knowledge - (int)oldknowledge;\n" +
                            "              if (diff > 0)\n" +
                            "                holder.achievement(371, diff); \n" +
                            "            } \n" +
                            "            if (!this.parent.paying && !this.basicPersonal) {\n" +
                            "              bonusKnowledge = Math.min(20.0D, bonusKnowledge);\n" +
                            "            } else if (!this.parent.paying && bonusKnowledge > 20.0D) {\n" +
                            "              bonusKnowledge = Math.min(getKnowledge(0.0D), bonusKnowledge);\n" +
                            "            } \n" +
                            "            holder.getCommunicator().sendUpdateSkill(this.number, (float)bonusKnowledge, isTemporary() ? 0 : this.affinity);\n" +
                            "            if (this.number != 2147483644 && this.number != 2147483642)\n" +
                            "              holder.resetInactivity(true); \n" +
                            "          } catch (com.wurmonline.server.NoSuchPlayerException nsp) {\n" +
                            "            logger.log(java.util.logging.Level.WARNING, pid + \":\" + nsp.getMessage(), (Throwable)nsp);\n" +
                            "          }   \n" +
                            "    } \n" +
                            "  }");

        } catch (NotFoundException e) {
            TyrfangsGameTweaks.logger.log(Level.WARNING, "No such class", e);
            e.printStackTrace();
        } catch (CannotCompileException e) {
            TyrfangsGameTweaks.logger.log(Level.SEVERE, "Could not Compile the injection code", e);
            e.printStackTrace();
        }
    }

}
