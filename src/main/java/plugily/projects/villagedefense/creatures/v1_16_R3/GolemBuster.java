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

package plugily.projects.villagedefense.creatures.v1_16_R3;

import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.server.v1_16_R3.DamageSource;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.EntityIronGolem;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EntityVillager;
import net.minecraft.server.v1_16_R3.EntityZombie;
import net.minecraft.server.v1_16_R3.GenericAttributes;
import net.minecraft.server.v1_16_R3.PathfinderGoalBreakDoor;
import net.minecraft.server.v1_16_R3.PathfinderGoalFloat;
import net.minecraft.server.v1_16_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_16_R3.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_16_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_16_R3.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_16_R3.PathfinderGoalZombieAttack;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import plugily.projects.villagedefense.creatures.CreatureUtils;

/**
 * Created by Tigerpanzer_02 on 05/11/2020.
 */
public class GolemBuster extends EntityZombie {

  public GolemBuster(org.bukkit.World world) {
    this(((CraftWorld) world).getHandle());
  }

  public GolemBuster(World world) {
    super(world);

    GoalSelectorCleaner.clearSelectors(this);
    getNavigation().q().b(true);

    goalSelector.a(0, new PathfinderGoalFloat(this));
    goalSelector.a(1, new PathfinderGoalBreakDoor(this, enumDifficulty -> true));
    goalSelector.a(2, new PathfinderGoalZombieAttack(this, CreatureUtils.getZombieSpeed(), false));
    goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, CreatureUtils.getZombieSpeed()));
    goalSelector.a(5, new PathfinderGoalBreakDoorFaster(this));
    goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityIronGolem.class, 8.0F)); // this one to look at IronGolem
    goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    //this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this));
    targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityPlayer.class, false));
    targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, false));
    targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillager.class, false));
    targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true)); // this one to target
    setHealth(5);
    p().a(GenericAttributes.FOLLOW_RANGE, 200.0D);
  }


  @Override
  public boolean damageEntity(DamageSource damagesource, float f) {
    if(damagesource != null && damagesource.getEntity() != null && damagesource.getEntity().getBukkitEntity().getType() == EntityType.IRON_GOLEM) {
      this.die();
      org.bukkit.inventory.ItemStack[] itemStack = new org.bukkit.inventory.ItemStack[]{new org.bukkit.inventory.ItemStack(org.bukkit.Material.ROTTEN_FLESH)};
      Bukkit.getServer().getPluginManager().callEvent(new EntityDeathEvent((LivingEntity) getBukkitEntity(), new ArrayList<>(Arrays.asList(itemStack)), expToDrop));
      IronGolem golem = (IronGolem) damagesource.getEntity().getBukkitEntity();
      golem.getWorld().spawnEntity(golem.getLocation(), EntityType.PRIMED_TNT);
      return true;
    }
    super.damageEntity(damagesource, f);
    return false;
  }
}
