/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2021  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.villagedefense.handlers.reward;

import java.util.logging.Level;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

/**
 * @author Plajer
 * <p>
 * Created at 23.11.2018
 */
public class Reward {

  private final RewardType type;
  private final RewardExecutor executor;
  private String executableCode;
  private final double chance;
  private int waveExecute = -1;

  public Reward(RewardType type, String rawCode, int waveExecute) {
    this(type, rawCode);
    this.waveExecute = waveExecute;
  }

  public Reward(RewardType type, String rawCode) {
    this.type = type;
    String processedCode = rawCode;

    //set reward executor based on provided code
    if(rawCode.contains("p:")) {
      executor = RewardExecutor.PLAYER;
      processedCode = StringUtils.replace(processedCode, "p:", "");
    } else if(rawCode.contains("script:")) {
      executor = RewardExecutor.SCRIPT;
      processedCode = StringUtils.replace(processedCode, "script:", "");
    } else {
      executor = RewardExecutor.CONSOLE;
    }

    //search for chance modifier
    if(processedCode.contains("chance(")) {
      int loc = processedCode.indexOf(')');
      //modifier is invalid
      if(loc == -1) {
        Bukkit.getLogger().log(Level.WARNING, "[Village Defense] rewards.yml configuration is broken! Make sure you did not forget using ) character in chance condition! Command: {0}", rawCode);
        //invalid code, 0% chance to execute
        chance = 0.0;
        return;
      }
      String chanceStr = processedCode;
      chanceStr = chanceStr.substring(0, loc).replaceAll("[^0-9]+", "");
      processedCode = StringUtils.replace(processedCode, "chance(" + chanceStr + "):", "");
      chance = Double.parseDouble(chanceStr);
    } else {
      chance = 100.0;
    }
    executableCode = processedCode;
  }

  public RewardExecutor getExecutor() {
    return executor;
  }

  public String getExecutableCode() {
    return executableCode;
  }

  public double getChance() {
    return chance;
  }

  public int getWaveExecute() {
    return waveExecute;
  }

  public RewardType getType() {
    return type;
  }

  public enum RewardType {
    END_GAME("endgame"), END_WAVE("endwave"), START_WAVE("startwave"), ZOMBIE_KILL("zombiekill"),
    PLAYER_DEATH("playerDeath"), VILLAGER_DEATH("villagerDeath");

    private final String path;

    RewardType(String path) {
      this.path = path;
    }

    public String getPath() {
      return path;
    }
  }

  public enum RewardExecutor {
    CONSOLE, PLAYER, SCRIPT
  }

}
