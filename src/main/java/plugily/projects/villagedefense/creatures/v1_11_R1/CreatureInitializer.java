package plugily.projects.villagedefense.creatures.v1_11_R1;

import net.minecraft.server.v1_11_R1.EntityInsentient;
import net.minecraft.server.v1_11_R1.EntityTypes;
import net.minecraft.server.v1_11_R1.GenericAttributes;
import net.minecraft.server.v1_11_R1.MinecraftKey;
import net.minecraft.server.v1_11_R1.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_11_R1.CraftWorld;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import plugily.projects.villagedefense.creatures.BaseCreatureInitializer;

public class CreatureInitializer implements BaseCreatureInitializer {

    public CreatureInitializer() {
        registerEntity("VillageZombie", 54, FastZombie.class);
        registerEntity("VillageZombie", 54, BabyZombie.class);
        registerEntity("VillageZombie", 54, PlayerBuster.class);
        registerEntity("VillageZombie", 54, GolemBuster.class);
        registerEntity("VillageZombie", 54, HardZombie.class);
        registerEntity("VillageZombie", 54, TankerZombie.class);
        registerEntity("VillageZombie", 54, VillagerSlayer.class);
        registerEntity("VillageVillager", 120, RidableVillager.class);
        registerEntity("VillageVillagerGolem", 99, RidableIronGolem.class);
        registerEntity("VillageWolf", 95, WorkingWolf.class);
        registerEntity("VillageZombie", 54, VillagerBuster.class);
    }

    private void registerEntity(String name, int id, Class<? extends EntityInsentient> customClass) {
        final MinecraftKey key = new MinecraftKey(name);
        EntityTypes.b.a(id, key, customClass);
        EntityTypes.d.add(key);
    }

    private World getWorld(Location location) {
        return ((CraftWorld) location.getWorld()).getHandle();
    }

    @Override
    public Villager spawnVillager(Location location) {
        RidableVillager ridableVillager = new RidableVillager(location.getWorld());
        ridableVillager.setPosition(location.getX(), location.getY(), location.getZ());
        getWorld(location).addEntity(ridableVillager, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Villager villager = (Villager) ridableVillager.getBukkitEntity();
        villager.setRemoveWhenFarAway(false);
        return villager;
    }

    @Override
    public Wolf spawnWolf(Location location) {
        WorkingWolf wolf = new WorkingWolf(location.getWorld());
        wolf.setPosition(location.getX(), location.getY(), location.getZ());
        getWorld(location).addEntity(wolf, CreatureSpawnEvent.SpawnReason.CUSTOM);
        wolf.setInvisible(false);
        return (Wolf) wolf.getBukkitEntity();
    }

    @Override
    public IronGolem spawnGolem(Location location) {
        RidableIronGolem ironGolem = new RidableIronGolem(location.getWorld());
        ironGolem.setPosition(location.getX(), location.getY(), location.getZ());
        getWorld(location).addEntity(ironGolem, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return (IronGolem) ironGolem.getBukkitEntity();
    }

    @Override
    public Zombie spawnFastZombie(Location location) {
        World world = getWorld(location);
        FastZombie fastZombie = new FastZombie(world);
        fastZombie.setPosition(location.getX(), location.getY(), location.getZ());
        world.addEntity(fastZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Zombie zombie = (Zombie) fastZombie.getBukkitEntity();
        zombie.setRemoveWhenFarAway(false);
        return zombie;
    }

    @Override
    public Zombie spawnBabyZombie(Location location) {
        World world = getWorld(location);
        BabyZombie fastZombie = new BabyZombie(world);
        fastZombie.setPosition(location.getX(), location.getY(), location.getZ());
        Zombie zombie = (Zombie) fastZombie.getBukkitEntity();
        world.addEntity(fastZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        zombie.setRemoveWhenFarAway(false);
        return zombie;
    }

    @Override
    public Zombie spawnHardZombie(Location location) {
        World world = getWorld(location);
        HardZombie hardZombie = new HardZombie(world);
        hardZombie.setPosition(location.getX(), location.getY(), location.getZ());
        Zombie zombie = (Zombie) hardZombie.getBukkitEntity();
        world.addEntity(hardZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        zombie.setRemoveWhenFarAway(false);
        return zombie;
    }

    @Override
    public Zombie spawnPlayerBuster(Location location) {
        World world = getWorld(location);
        PlayerBuster playerBuster = new PlayerBuster(world);
        playerBuster.setPosition(location.getX(), location.getY(), location.getZ());
        Zombie zombie = (Zombie) playerBuster.getBukkitEntity();
        world.addEntity(playerBuster, CreatureSpawnEvent.SpawnReason.CUSTOM);
        zombie.setRemoveWhenFarAway(false);
        return zombie;
    }

    @Override
    public Zombie spawnGolemBuster(Location location) {
        World world = getWorld(location);
        GolemBuster golemBuster = new GolemBuster(world);
        golemBuster.setPosition(location.getX(), location.getY(), location.getZ());
        Zombie zombie = (Zombie) golemBuster.getBukkitEntity();
        world.addEntity(golemBuster, CreatureSpawnEvent.SpawnReason.CUSTOM);
        zombie.setRemoveWhenFarAway(false);
        return zombie;
    }

    @Override
    public Zombie spawnVillagerBuster(Location location) {
        World world = getWorld(location);
        VillagerBuster villagerBuster = new VillagerBuster(world);
        villagerBuster.setPosition(location.getX(), location.getY(), location.getZ());
        Zombie zombie = (Zombie) villagerBuster.getBukkitEntity();
        world.addEntity(villagerBuster, CreatureSpawnEvent.SpawnReason.CUSTOM);
        zombie.setRemoveWhenFarAway(false);
        return zombie;
    }

    @Override
    public Zombie spawnKnockbackResistantZombies(Location location) {
        World world = getWorld(location);
        TankerZombie tankerZombie = new TankerZombie(world);
        tankerZombie.getAttributeInstance(GenericAttributes.c).setValue(Double.MAX_VALUE);
        tankerZombie.setPosition(location.getX(), location.getY(), location.getZ());
        Zombie zombie = (Zombie) tankerZombie.getBukkitEntity();
        world.addEntity(tankerZombie, CreatureSpawnEvent.SpawnReason.CUSTOM);
        zombie.setRemoveWhenFarAway(false);
        return zombie;
    }

    @Override
    public Zombie spawnVillagerSlayer(Location location) {
        World world = getWorld(location);
        VillagerSlayer villagerSlayer = new VillagerSlayer(world);
        villagerSlayer.setPosition(location.getX(), location.getY(), location.getZ());
        Zombie zombie = (Zombie) villagerSlayer.getBukkitEntity();
        world.addEntity(villagerSlayer, CreatureSpawnEvent.SpawnReason.CUSTOM);
        zombie.setRemoveWhenFarAway(false);
        return zombie;
    }
}
