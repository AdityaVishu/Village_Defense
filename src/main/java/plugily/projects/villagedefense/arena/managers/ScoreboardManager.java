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

package plugily.projects.villagedefense.arena.managers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.clip.placeholderapi.PlaceholderAPI;
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import me.tigerhix.lib.scoreboard.common.EntryBuilder;
import me.tigerhix.lib.scoreboard.type.Entry;
import me.tigerhix.lib.scoreboard.type.Scoreboard;
import me.tigerhix.lib.scoreboard.type.ScoreboardHandler;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaState;
import plugily.projects.villagedefense.arena.options.ArenaOption;
import plugily.projects.villagedefense.handlers.language.LanguageManager;
import plugily.projects.villagedefense.user.User;

/**
 * @author Plajer
 * <p>
 * Created at 06.01.2019
 */
public class ScoreboardManager {

  private final Map<UUID, Scoreboard> boardMap = new ConcurrentHashMap<>();
  private final Map<UUID, org.bukkit.scoreboard.Scoreboard> lastBoardMap = new ConcurrentHashMap<>();
  private final org.bukkit.scoreboard.Scoreboard dummyBoard = Bukkit.getScoreboardManager().getNewScoreboard();
  private final Main plugin;
  private final String boardTitle;
  private final Arena arena;

  public ScoreboardManager(Arena arena) {
    this.arena = arena;
    this.plugin = arena.getPlugin();
    this.boardTitle = plugin.getChatManager().colorRawMessage(LanguageManager.getLanguageMessage("Scoreboard.Title"));
  }

  /**
   * Creates arena scoreboard for target user
   *
   * @param user user that represents game player
   * @see User
   */
  public void createScoreboard(User user) {
    Player player = user.getPlayer();
    lastBoardMap.put(player.getUniqueId(), player.getScoreboard());
    player.setScoreboard(dummyBoard);

    Scoreboard scoreboard = ScoreboardLib.createScoreboard(player).setHandler(new ScoreboardHandler() {
      @Override
      public String getTitle(Player player) {
        return boardTitle;
      }

      @Override
      public List<Entry> getEntries(Player player) {
        return formatScoreboard(user);
      }
    });
    scoreboard.activate();
    boardMap.put(player.getUniqueId(), scoreboard);
  }

  /**
   * Removes scoreboard of user
   *
   * @param user user that represents game player
   * @see User
   */
  public void removeScoreboard(User user) {
    Optional.ofNullable(boardMap.remove(user.getUniqueId())).ifPresent(Scoreboard::deactivate);
    Optional.ofNullable(lastBoardMap.remove(user.getUniqueId())).ifPresent(user.getPlayer()::setScoreboard);
  }

  /**
   * Forces all scoreboards to deactivate.
   */
  public void stopAllScoreboards() {
    boardMap.values().forEach(Scoreboard::deactivate);
    boardMap.clear();
  }

  private List<Entry> formatScoreboard(User user) {
    EntryBuilder builder = new EntryBuilder();
    List<String> lines;
    if(arena.getArenaState() == ArenaState.IN_GAME) {
      lines = LanguageManager.getLanguageList("Scoreboard.Content.Playing" + (arena.isFighting() ? "" : "-Waiting"));
    } else {
      //apply fix
      if(arena.getArenaState() == ArenaState.ENDING) {
        lines = LanguageManager.getLanguageList("Scoreboard.Content.Playing");
      } else {
        lines = LanguageManager.getLanguageList("Scoreboard.Content." + arena.getArenaState().getFormattedName());
      }
    }
    for(String line : lines) {
      builder.next(formatScoreboardLine(line, user));
    }
    return builder.build();
  }

  private String formatScoreboardLine(String line, User user) {
    String formattedLine = line;
    formattedLine = StringUtils.replace(formattedLine, "%TIME%", Integer.toString(arena.getTimer()));
    formattedLine = StringUtils.replace(formattedLine, "%PLAYERS%", Integer.toString(arena.getPlayers().size()));
    formattedLine = StringUtils.replace(formattedLine, "%MIN_PLAYERS%", Integer.toString(arena.getMinimumPlayers()));
    formattedLine = StringUtils.replace(formattedLine, "%PLAYERS_LEFT%", Integer.toString(arena.getPlayersLeft().size()));
    formattedLine = StringUtils.replace(formattedLine, "%VILLAGERS%", Integer.toString(arena.getVillagers().size()));
    formattedLine = StringUtils.replace(formattedLine, "%ORBS%", Integer.toString(user.getStat(StatsStorage.StatisticType.ORBS)));
    formattedLine = StringUtils.replace(formattedLine, "%WAVE%", Integer.toString(arena.getWave()));
    int zombiesLeft = arena.getZombiesLeft();
    if(zombiesLeft >= 0) {
      formattedLine = StringUtils.replace(formattedLine, "%ZOMBIES%", Integer.toString(zombiesLeft));
    }
    formattedLine = StringUtils.replace(formattedLine, "%ROTTEN_FLESH%", Integer.toString(arena.getOption(ArenaOption.ROTTEN_FLESH_AMOUNT)));
    formattedLine = StringUtils.replace(formattedLine, "%ARENA_NAME%", arena.getMapName());
    formattedLine = StringUtils.replace(formattedLine, "%ARENA_ID%", arena.getId());
    formattedLine = plugin.getChatManager().colorRawMessage(formattedLine);
    if(plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      formattedLine = PlaceholderAPI.setPlaceholders(user.getPlayer(), formattedLine);
    }
    return formattedLine;
  }

}
