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

package plugily.projects.villagedefense.kits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import plugily.projects.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.kits.basekits.Kit;
import plugily.projects.villagedefense.kits.free.KnightKit;
import plugily.projects.villagedefense.kits.free.LightTankKit;
import plugily.projects.villagedefense.kits.level.ArcherKit;
import plugily.projects.villagedefense.kits.level.GolemFriendKit;
import plugily.projects.villagedefense.kits.level.HardcoreKit;
import plugily.projects.villagedefense.kits.level.HealerKit;
import plugily.projects.villagedefense.kits.level.LooterKit;
import plugily.projects.villagedefense.kits.level.MediumTankKit;
import plugily.projects.villagedefense.kits.level.PuncherKit;
import plugily.projects.villagedefense.kits.level.RunnerKit;
import plugily.projects.villagedefense.kits.level.TerminatorKit;
import plugily.projects.villagedefense.kits.level.WorkerKit;
import plugily.projects.villagedefense.kits.level.ZombieFinderKit;
import plugily.projects.villagedefense.kits.premium.BlockerKit;
import plugily.projects.villagedefense.kits.premium.CleanerKit;
import plugily.projects.villagedefense.kits.premium.DogFriendKit;
import plugily.projects.villagedefense.kits.premium.HeavyTankKit;
import plugily.projects.villagedefense.kits.premium.MedicKit;
import plugily.projects.villagedefense.kits.premium.NakedKit;
import plugily.projects.villagedefense.kits.premium.PremiumHardcoreKit;
import plugily.projects.villagedefense.kits.premium.ShotBowKit;
import plugily.projects.villagedefense.kits.premium.TeleporterKit;
import plugily.projects.villagedefense.kits.premium.TornadoKit;
import plugily.projects.villagedefense.kits.premium.WizardKit;
import plugily.projects.villagedefense.utils.constants.Constants;

/**
 * Kit registry class for registering new kits.
 *
 * @author TomTheDeveloper
 */
public class KitRegistry {

  private static final List<Kit> kits = new ArrayList<>();
  private static Kit defaultKit;
  private static Main plugin;
  private static final List<Class<?>> classKitNames = Arrays.asList(LightTankKit.class, ZombieFinderKit.class, ArcherKit.class, PuncherKit.class, HealerKit.class, LooterKit.class, RunnerKit.class,
      MediumTankKit.class, WorkerKit.class, GolemFriendKit.class, TerminatorKit.class, HardcoreKit.class, CleanerKit.class, TeleporterKit.class, HeavyTankKit.class, ShotBowKit.class,
      DogFriendKit.class, PremiumHardcoreKit.class, TornadoKit.class, BlockerKit.class, MedicKit.class, NakedKit.class, WizardKit.class);

  private KitRegistry() {
  }

  public static void init(Main plugin) {
    KitRegistry.plugin = plugin;
    setupGameKits();
  }

  /**
   * Method for registering new kit
   *
   * @param kit Kit to register
   */
  public static void registerKit(Kit kit) {
    kits.add(kit);
  }

  /**
   * Return default game kit
   *
   * @return default game kit
   */
  public static Kit getDefaultKit() {
    return defaultKit;
  }

  /**
   * Sets default game kit
   *
   * @param defaultKit default kit to set, must be FreeKit
   */
  public static void setDefaultKit(Kit defaultKit) {
    KitRegistry.defaultKit = defaultKit;
  }

  /**
   * Returns all available kits
   *
   * @return list of all registered kits
   */
  public static List<Kit> getKits() {
    return kits;
  }

  private static void setupGameKits() {
    KnightKit knightkit = new KnightKit();
    FileConfiguration config = ConfigUtils.getConfig(plugin, Constants.Files.KITS.getName());
    for(Class<?> kitClass : classKitNames) {
      if(config.getBoolean("Enabled-Game-Kits." + kitClass.getSimpleName().replace("Kit", ""))) {
        try {
          Class.forName(kitClass.getName()).getDeclaredConstructor().newInstance();
        } catch(Exception e) {
          plugin.getLogger().log(Level.SEVERE, "Fatal error while registering existing game kit! Report this error to the developer!");
          plugin.getLogger().log(Level.SEVERE, "Cause: " + e.getMessage() + " (kitClass " + kitClass.getName() + ")");
        }
      }
    }
    setDefaultKit(knightkit);
  }

}
