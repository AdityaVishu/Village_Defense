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

package plugily.projects.villagedefense.kits.free;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import plugily.projects.commonsbox.minecraft.compat.VersionUtils;
import plugily.projects.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.commonsbox.minecraft.helper.ArmorHelper;
import plugily.projects.commonsbox.minecraft.helper.WeaponHelper;
import plugily.projects.villagedefense.handlers.language.Messages;
import plugily.projects.villagedefense.kits.KitRegistry;
import plugily.projects.villagedefense.kits.basekits.FreeKit;
import plugily.projects.villagedefense.utils.Utils;

/**
 * Created by Tom on 18/08/2014.
 */
public class LightTankKit extends FreeKit {

  public LightTankKit() {
    setName(getPlugin().getChatManager().colorMessage(Messages.KITS_LIGHT_TANK_NAME));
    List<String> description = Utils.splitString(getPlugin().getChatManager().colorMessage(Messages.KITS_LIGHT_TANK_DESCRIPTION), 40);
    setDescription(description.toArray(new String[0]));
    KitRegistry.registerKit(this);
  }

  @Override
  public boolean isUnlockedByPlayer(Player player) {
    return true;
  }

  @Override
  public void giveKitItems(Player player) {
    player.getInventory().addItem(WeaponHelper.getUnBreakingSword(WeaponHelper.ResourceType.WOOD, 10));
    player.getInventory().addItem(new ItemStack(XMaterial.COOKED_PORKCHOP.parseMaterial(), 8));
    ArmorHelper.setArmor(player, ArmorHelper.ArmorType.IRON);
    VersionUtils.setMaxHealth(player, 26.0);
    player.setHealth(26.0);
  }

  @Override
  public Material getMaterial() {
    return Material.LEATHER_CHESTPLATE;
  }

  @Override
  public void reStock(Player player) {
    //no restock items for this kit
  }
}
