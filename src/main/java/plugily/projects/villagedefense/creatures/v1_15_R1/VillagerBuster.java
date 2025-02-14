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

package plugily.projects.villagedefense.creatures.v1_15_R1;


import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.server.v1_15_R1.Entity;
import net.minecraft.server.v1_15_R1.EntityHuman;
import net.minecraft.server.v1_15_R1.EntityIronGolem;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.EntityVillager;
import net.minecraft.server.v1_15_R1.EntityZombie;
import net.minecraft.server.v1_15_R1.GenericAttributes;
import net.minecraft.server.v1_15_R1.PathfinderGoalBreakDoor;
import net.minecraft.server.v1_15_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_15_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_15_R1.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_15_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_15_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_15_R1.PathfinderGoalZombieAttack;
import net.minecraft.server.v1_15_R1.World;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import plugily.projects.villagedefense.creatures.CreatureUtils;

/**
 * Created by Tom on 15/08/2014.
 */
public class VillagerBuster extends EntityZombie {

  public VillagerBuster(org.bukkit.World world) {
    this(((CraftWorld) world).getHandle());
  }

  public VillagerBuster(World world) {
    super(world);

    GoalSelectorCleaner.clearSelectors(this);
    getNavigation().q().b(true);


    goalSelector.a(0, new PathfinderGoalFloat(this));
    goalSelector.a(1, new PathfinderGoalBreakDoor(this, enumDifficulty -> true));

    goalSelector.a(2, new PathfinderGoalZombieAttack(this, CreatureUtils.getZombieSpeed(), false));
    goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, CreatureUtils.getZombieSpeed()));
    goalSelector.a(5, new PathfinderGoalBreakDoorFaster(this));
    goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityVillager.class, 8.0F)); // this one to look at human
    goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    //this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this));
    targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityPlayer.class, false));
    targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, false));
    targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillager.class, true));// this one to target
    targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, false));


    setHealth(10);
    p(true);
  }

  @Override
  protected void initAttributes() {
    super.initAttributes();
    getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(200.0D);
  }

  @Override
  public boolean B(Entity entity) {
    if(entity.getBukkitEntity().getType() == EntityType.VILLAGER) {
      this.die();
      Bukkit.getServer().getPluginManager().callEvent(new EntityDeathEvent((LivingEntity) getBukkitEntity(), new ArrayList<>(Collections.singletonList(new ItemStack(Material.ROTTEN_FLESH))), 6));
      org.bukkit.entity.Entity bukkitEntity = entity.getBukkitEntity();
      bukkitEntity.getWorld().spawnEntity(bukkitEntity.getLocation(), EntityType.PRIMED_TNT);
      return false;
    }
    return super.B(entity);
  }
}
