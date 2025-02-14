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

package plugily.projects.villagedefense.kits.premium;

import java.util.List;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import plugily.projects.commonsbox.minecraft.compat.VersionUtils;
import plugily.projects.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.commonsbox.minecraft.helper.ArmorHelper;
import plugily.projects.commonsbox.minecraft.helper.WeaponHelper;
import plugily.projects.villagedefense.handlers.PermissionsManager;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.kits.KitRegistry;
import plugily.projects.villagedefense.kits.basekits.PremiumKit;
import plugily.projects.villagedefense.user.User;
import plugily.projects.villagedefense.utils.Utils;

/**
 * Created by Tom on 1/12/2015.
 */
public class MedicKit extends PremiumKit implements Listener {

  public MedicKit() {
    setName(getPlugin().getChatManager().colorMessage(Messages.KITS_MEDIC_NAME));
    List<String> description = Utils.splitString(getPlugin().getChatManager().colorMessage(Messages.KITS_MEDIC_DESCRIPTION), 40);
    setDescription(description.toArray(new String[0]));
    getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    KitRegistry.registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return PermissionsManager.isPremium(player) || player.hasPermission("villagedefense.kit.medic");
  }

  @Override
  public void giveKitItems(Player player) {
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.STONE, 10));
    ArmorHelper.setColouredArmor(Color.WHITE, player);
    player.getInventory().addItem(new ItemStack(XMaterial.COOKED_PORKCHOP.parseMaterial(), 8));
    player.getInventory().addItem(Utils.getPotion(PotionType.REGEN, 1, true));
  }

  @Override
  public Material getMaterial() {
    return Material.GHAST_TEAR;
  }

  @Override
  public void reStock(Player player) {
    //no restock items for this kit
  }

  @EventHandler
  public void onCreatureHit(EntityDamageByEntityEvent e) {
    if(!(e.getEntity() instanceof Creature) || !(e.getDamager() instanceof Player)) {
      return;
    }
    User user = getPlugin().getUserManager().getUser((Player) e.getDamager());
    if(!(user.getKit() instanceof MedicKit) || Math.random() > 0.1) {
      return;
    }
    healNearbyPlayers(e.getDamager());
  }

  private void healNearbyPlayers(Entity en) {
    for(Entity entity : en.getNearbyEntities(5, 5, 5)) {
      if(!(entity instanceof Player)) {
        continue;
      }
      Player player = (Player) entity;
      if(VersionUtils.getMaxHealth(player) > (player.getHealth() + 1)) {
        player.setHealth(player.getHealth() + 1);
      } else {
        player.setHealth(VersionUtils.getMaxHealth(player));
      }
      VersionUtils.sendParticles("HEART", player, player.getLocation(), 20);
    }
  }

}
