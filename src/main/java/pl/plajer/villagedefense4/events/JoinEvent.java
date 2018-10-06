/*
 * Village Defense 4 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.villagedefense4.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import pl.plajer.villagedefense4.Main;
import pl.plajer.villagedefense4.api.StatsStorage;
import pl.plajer.villagedefense4.arena.ArenaRegistry;
import pl.plajer.villagedefense4.database.MySQLConnectionUtils;
import pl.plajer.villagedefense4.handlers.PermissionsManager;
import pl.plajer.villagedefense4.user.UserManager;
import pl.plajerlair.core.services.exception.ReportedException;
import pl.plajerlair.core.utils.UpdateChecker;

/**
 * Created by Tom on 10/07/2015.
 */
public class JoinEvent implements Listener {

  private Main plugin;

  public JoinEvent(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onLogin(PlayerLoginEvent e) {
    try {
      if (!plugin.isBungeeActivated() && !plugin.getServer().hasWhitelist()
          || e.getResult() != PlayerLoginEvent.Result.KICK_WHITELIST) {
        return;
      }
      if (e.getPlayer().hasPermission(PermissionsManager.getJoinFullGames())) {
        e.setResult(PlayerLoginEvent.Result.ALLOWED);
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    try {
      UserManager.registerUser(event.getPlayer().getUniqueId());
      if (plugin.isBungeeActivated()) {
        if (ArenaRegistry.getArenas().size() >= 1) {
          ArenaRegistry.getArenas().get(0).teleportToLobby(event.getPlayer());
        }
        return;
      }
      for (Player player : plugin.getServer().getOnlinePlayers()) {
        if (ArenaRegistry.getArena(player) == null) {
          continue;
        }
        player.hidePlayer(event.getPlayer());
        event.getPlayer().hidePlayer(player);
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }

  @EventHandler
  public void onJoinCheckVersion(final PlayerJoinEvent event) {
    try {
      //we want to be the first :)
      if (!plugin.isDatabaseActivated()) {
        for (StatsStorage.StatisticType s : StatsStorage.StatisticType.values()) {
          plugin.getFileStats().loadStat(event.getPlayer(), s);
        }
        return;
      }
      final Player player = event.getPlayer();
      Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> MySQLConnectionUtils.loadPlayerStats(player));
      if (plugin.getConfig().getBoolean("Update-Notifier.Enabled", true)) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
          if (event.getPlayer().hasPermission("villagedefense.updatenotify")) {
            String currentVersion = "v" + Bukkit.getPluginManager().getPlugin("VillageDefense").getDescription().getVersion();
            try {
              boolean check = UpdateChecker.checkUpdate(plugin, currentVersion, 41869);
              if (check) {
                String latestVersion = "v" + UpdateChecker.getLatestVersion();
                if (latestVersion.contains("b")) {
                  event.getPlayer().sendMessage("");
                  event.getPlayer().sendMessage(ChatColor.BOLD + "VILLAGE DEFENSE UPDATE NOTIFY");
                  event.getPlayer().sendMessage(ChatColor.RED + "BETA version of software is ready for update! Proceed with caution.");
                  event.getPlayer().sendMessage(ChatColor.YELLOW + "Current version: " + ChatColor.RED + currentVersion + ChatColor.YELLOW + " Latest version: " + ChatColor.GREEN + latestVersion);
                } else {
                  event.getPlayer().sendMessage("");
                  event.getPlayer().sendMessage(ChatColor.BOLD + "VILLAGE DEFENSE UPDATE NOTIFY");
                  event.getPlayer().sendMessage(ChatColor.GREEN + "Software is ready for update! Download it to keep with latest changes and fixes.");
                  event.getPlayer().sendMessage(ChatColor.YELLOW + "Current version: " + ChatColor.RED + currentVersion + ChatColor.YELLOW + " Latest version: " + ChatColor.GREEN + latestVersion);
                }
              }
            } catch (Exception ignored) {
            }
          }

        }, 25);
      }
    } catch (Exception ex) {
      new ReportedException(plugin, ex);
    }
  }
}
