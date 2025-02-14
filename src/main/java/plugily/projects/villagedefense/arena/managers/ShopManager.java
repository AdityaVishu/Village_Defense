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
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import plugily.projects.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.commonsbox.minecraft.item.ItemUtils;
import plugily.projects.commonsbox.minecraft.misc.stuff.ComplementAccessor;
import plugily.projects.commonsbox.minecraft.serialization.LocationSerializer;
import plugily.projects.inventoryframework.gui.GuiItem;
import plugily.projects.inventoryframework.gui.type.ChestGui;
import plugily.projects.inventoryframework.pane.StaticPane;
import plugily.projects.villagedefense.ConfigPreferences.Option;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.api.StatsStorage;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.ArenaRegistry;
import plugily.projects.villagedefense.arena.options.ArenaOption;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.user.User;
import plugily.projects.villagedefense.utils.Debugger;
import plugily.projects.villagedefense.utils.Utils;
import plugily.projects.villagedefense.utils.constants.Constants;

/**
 * Created by Tom on 16/08/2014.
 */
public class ShopManager {

  private final String defaultGolemItemName;
  private final String defaultWolfItemName;

  private final Main plugin;
  private final FileConfiguration config;
  private ChestGui gui;
  private final Arena arena;

  public ShopManager(Arena arena) {
    plugin = arena.getPlugin();
    config = ConfigUtils.getConfig(plugin, Constants.Files.ARENAS.getName());
    this.arena = arena;
    FileConfiguration languageConfig = ConfigUtils.getConfig(plugin, Constants.Files.LANGUAGE.getName());
    defaultGolemItemName = languageConfig.getString("In-Game.Messages.Shop-Messages.Golem-Item-Name");
    defaultWolfItemName = languageConfig.getString("In-Game.Messages.Shop-Messages.Wolf-Item-Name");
    if(config.isSet("instances." + arena.getId() + ".shop")) {
      registerShop();
    }
  }

  public ChestGui getShop() {
    return gui;
  }

  /**
   * Default name of golem spawn item from language.yml
   *
   * @return the default golem item name
   */
  public String getDefaultGolemItemName() {
    return defaultGolemItemName;
  }

  /**
   * Default name of wolf spawn item from language.yml
   *
   * @return the default wolf item name
   */
  public String getDefaultWolfItemName() {
    return defaultWolfItemName;
  }

  public void openShop(Player player) {
    Arena arena = ArenaRegistry.getArena(player);
    if(arena == null) {
      return;
    }
    if(gui == null) {
      player.sendMessage(plugin.getChatManager().colorMessage(Messages.SHOP_MESSAGES_NO_SHOP_DEFINED));
      return;
    }
    gui.show(player);
  }

  private void registerShop() {
    if(!validateShop()) {
      return;
    }
    ItemStack[] contents = ((Chest) LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".shop"))
        .getBlock().getState()).getInventory().getContents();
    int size = Utils.serializeInt(contents.length) / 9;
    ChestGui gui = new ChestGui(size, plugin.getChatManager().colorMessage(Messages.SHOP_MESSAGES_SHOP_GUI_NAME));
    gui.setOnGlobalClick(event -> event.setCancelled(true));
    StaticPane pane = new StaticPane(9, size);
    for (int slot = 0; slot < contents.length; slot++) {
      ItemStack itemStack = contents[slot];
      if(itemStack == null || itemStack.getType() == Material.REDSTONE_BLOCK) {
        continue;
      }

      String costString = "";
      ItemMeta meta = itemStack.getItemMeta();
      //seek for item price
      if(meta != null && meta.hasLore()) {
        for(String s : ComplementAccessor.getComplement().getLore(meta)) {
          if(s.contains(plugin.getChatManager().colorMessage(Messages.SHOP_MESSAGES_CURRENCY_IN_SHOP)) || s.contains("orbs")) {
            costString = ChatColor.stripColor(s).replaceAll("&[0-9a-zA-Z]", "").replaceAll("[^0-9]", "");
            break;
          }
        }
      }

      int cost;
      try {
        cost = Integer.parseInt(costString);
      } catch(NumberFormatException e) {
        Debugger.debug(Level.WARNING, "No price set for shop item in arena {0} skipping item!", arena.getId());
        continue;
      }

      pane.addItem(new GuiItem(itemStack, e -> {
        Player player = (Player) e.getWhoClicked();

        if(!arena.getPlayers().contains(player)) {
          return;
        }

        User user = plugin.getUserManager().getUser(player);
        int orbs = user.getStat(StatsStorage.StatisticType.ORBS);

        if(cost > orbs) {
          player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.SHOP_MESSAGES_NOT_ENOUGH_ORBS));
          return;
        }

        if(ItemUtils.isItemStackNamed(itemStack)) {
          String name = ComplementAccessor.getComplement().getDisplayName(itemStack.getItemMeta());
          int spawnedAmount = 0;

          if(name.contains(plugin.getChatManager().colorMessage(Messages.SHOP_MESSAGES_GOLEM_ITEM_NAME))
              || name.contains(defaultGolemItemName)) {
            List<IronGolem> golems = arena.getIronGolems();

            if(plugin.getConfigPreferences().getOption(Option.CAN_BUY_GOLEMSWOLVES_IF_THEY_DIED)) {
              golems = golems.stream().filter(IronGolem::isDead).collect(Collectors.toList());
            }

            String spawnedName = plugin.getChatManager().colorMessage(Messages.SPAWNED_GOLEM_NAME).replace("%player%", player.getName());

            for(IronGolem golem : golems) {
              if(spawnedName.equals(golem.getCustomName())) {
                spawnedAmount++;
              }
            }

            int spawnLimit = plugin.getConfig().getInt("Golems-Spawn-Limit", 15);
            if(spawnedAmount >= spawnLimit) {
              player.sendMessage(plugin.getChatManager().colorMessage(Messages.SHOP_MESSAGES_MOB_LIMIT_REACHED)
                  .replace("%amount%", Integer.toString(spawnLimit)));
              return;
            }

            arena.spawnGolem(arena.getStartLocation(), player);
            player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.GOLEM_SPAWNED));
            user.setStat(StatsStorage.StatisticType.ORBS, orbs - cost);
            arena.addOptionValue(ArenaOption.TOTAL_ORBS_SPENT, cost);
            return;
          }

          if(name.contains(plugin.getChatManager().colorMessage(Messages.SHOP_MESSAGES_WOLF_ITEM_NAME))
              || name.contains(defaultWolfItemName)) {
            List<Wolf> wolves = arena.getWolves();

            if(plugin.getConfigPreferences().getOption(Option.CAN_BUY_GOLEMSWOLVES_IF_THEY_DIED)) {
              wolves = wolves.stream().filter(Wolf::isDead).collect(Collectors.toList());
            }

            String spawnedName = plugin.getChatManager().colorMessage(Messages.SPAWNED_WOLF_NAME).replace("%player%", player.getName());

            for(Wolf wolf : wolves) {
              if(spawnedName.equals(wolf.getCustomName())) {
                spawnedAmount++;
              }
            }

            int spawnLimit = plugin.getConfig().getInt("Wolves-Spawn-Limit", 20);
            if(spawnedAmount >= spawnLimit) {
              player.sendMessage(plugin.getChatManager().colorMessage(Messages.SHOP_MESSAGES_MOB_LIMIT_REACHED)
                  .replace("%amount%", Integer.toString(spawnLimit)));
              return;
            }

            arena.spawnWolf(arena.getStartLocation(), player);
            player.sendMessage(plugin.getChatManager().getPrefix() + plugin.getChatManager().colorMessage(Messages.WOLF_SPAWNED));
            user.setStat(StatsStorage.StatisticType.ORBS, orbs - cost);
            arena.addOptionValue(ArenaOption.TOTAL_ORBS_SPENT, cost);
            return;
          }
        }

        ItemStack stack = itemStack.clone();
        ItemMeta itemMeta = stack.getItemMeta();

        if(itemMeta != null) {
          if(itemMeta.hasLore()) {
            ComplementAccessor.getComplement().setLore(itemMeta, ComplementAccessor.getComplement().getLore(itemMeta).stream().filter(lore ->
                !lore.contains(plugin.getChatManager().colorMessage(Messages.SHOP_MESSAGES_CURRENCY_IN_SHOP)))
                .collect(Collectors.toList()));
          }

          stack.setItemMeta(itemMeta);
        }

        player.getInventory().addItem(stack);
        user.setStat(StatsStorage.StatisticType.ORBS, orbs - cost);
        arena.addOptionValue(ArenaOption.TOTAL_ORBS_SPENT, cost);
      }), slot % 9, slot / 9);
    }
    gui.addPane(pane);
    this.gui = gui;
  }

  private boolean validateShop() {
    String shop = config.getString("instances." + arena.getId() + ".shop", "");
    if(shop.isEmpty() || !shop.contains(",")) {
      Debugger.debug(Level.WARNING, "There is no shop for arena {0}! Aborting registering shop!", arena.getId());
      return false;
    }
    Location location = LocationSerializer.getLocation(shop);
    //todo are these still revelant checks
    if(location.getWorld() == null || !(location.getBlock().getState() instanceof Chest)) {
      Debugger.debug(Level.WARNING, "Shop failed to load, invalid location for location {0}", LocationSerializer.locationToString(location));
      return false;
    }
    return true;
  }

}
