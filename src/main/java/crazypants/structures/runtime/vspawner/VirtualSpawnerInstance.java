package crazypants.structures.runtime.vspawner;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import crazypants.structures.PacketHandler;
import crazypants.structures.api.util.Point3i;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class VirtualSpawnerInstance {

//  static {
//    PacketHandler.INSTANCE.registerMessage(PacketSpawnParticles.class, PacketSpawnParticles.class, PacketHandler.nextID(), Side.CLIENT);
//  }

  private VirtualSpawnerBehaviour behaviour;
  private Point3i worldPos;
  private World world;

  private int ticksTillNextSpawn = 20;
  private int remainingSpawnTries;

  private boolean registered = false;

  public VirtualSpawnerInstance(VirtualSpawnerBehaviour behaviour, World world, Point3i worldPos) {
    this.behaviour = behaviour;
    this.world = world;
    this.worldPos = worldPos;
  }

  public void onLoad() {
    if(!registered) {
      FMLCommonHandler.instance().bus().register(this);
      registered = true;
    }
  }

  public void onUnload() {
    FMLCommonHandler.instance().bus().unregister(this);
  }

  @SubscribeEvent
  public void update(WorldTickEvent evt) {
    if(evt.world != world || !isActivated()) {
      return;
    }

    spawnParticles();

    if(ticksTillNextSpawn == -1) {
      resetTimer();
    }
    if(ticksTillNextSpawn > 0) {
      --ticksTillNextSpawn;
      return;
    }

    remainingSpawnTries = behaviour.getNumberSpawned() + behaviour.getMaxSpawnRetries();
    for (int i = 0; i < behaviour.getNumberSpawned() && remainingSpawnTries > 0; ++i) {
      if(!trySpawnEntity()) {
        break;
      }
    }

  }

  private void spawnParticles() {
    if(!behaviour.isRenderParticles()) {
      return;
    }    
    PacketHandler.INSTANCE.sendToAllAround(new PacketSpawnParticles(worldPos), new TargetPoint(world.provider.dimensionId, worldPos.x, worldPos.y, worldPos.z, 64));    
  }

  private void resetTimer() {
    if(behaviour.getMaxSpawnDelay() <= behaviour.getMinSpawnDelay()) {
      this.ticksTillNextSpawn = behaviour.getMinSpawnDelay();
    } else {
      int i = behaviour.getMaxSpawnDelay() - behaviour.getMinSpawnDelay();
      ticksTillNextSpawn = behaviour.getMinSpawnDelay() + world.rand.nextInt(i);
    }
  }

  private boolean isActivated() {
    if(behaviour.getMinPlayerDistance() > 0) {
      return world.getClosestPlayer(worldPos.x + 0.5D, worldPos.y + 0.5D, worldPos.z + 0.5D, behaviour.getMinPlayerDistance()) != null;
    }
    return true;
  }

  protected boolean trySpawnEntity() {    
    Entity entity = createEntity(behaviour.isPersistEntities());
    if(!(entity instanceof EntityLiving)) {
      return false;
    }
    EntityLiving entityliving = (EntityLiving) entity;
    int spawnRange = behaviour.getSpawnRange();

    if(behaviour.getMaxNearbyEntities() > 0) {
      int range = spawnRange * 4;
      //int nearbyEntities = world.getEntitiesWithinAABB(entity.getClass(),
      int nearbyEntities = world.getEntitiesWithinAABB(EntityLiving.class,
          AxisAlignedBB.getBoundingBox(
              worldPos.x - range, worldPos.y - range, worldPos.z - range,
              worldPos.x + range, worldPos.y + range, worldPos.z + range))
          .size();

      if(nearbyEntities >= behaviour.getMaxNearbyEntities()) {
        return false;
      }
    }

    while (remainingSpawnTries-- > 0) {
      double x = worldPos.x + (world.rand.nextDouble() - world.rand.nextDouble()) * spawnRange;
      double y = worldPos.y + world.rand.nextInt(3) - 1;
      double z = worldPos.z + (world.rand.nextDouble() - world.rand.nextDouble()) * spawnRange;
      entity.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360.0F, 0.0F);

      if(canSpawnEntity(entityliving)) {
        entityliving.onSpawnWithEgg(null);
        world.spawnEntityInWorld(entityliving);
        world.playAuxSFX(2004, worldPos.x, worldPos.y, worldPos.z, 0);
        entityliving.spawnExplosionParticle();
        return true;
      }
    }

    return false;
  }

  protected boolean canSpawnEntity(EntityLiving entityliving) {
    boolean spaceClear = world.checkNoEntityCollision(entityliving.boundingBox)
        && world.getCollidingBoundingBoxes(entityliving, entityliving.boundingBox).isEmpty()
        && (!world.isAnyLiquid(entityliving.boundingBox) || entityliving.isCreatureType(EnumCreatureType.waterCreature, false));
    if(spaceClear && behaviour.isUseVanillaSpawnChecks()) {
      //Full checks for lighting, dimension etc 
      spaceClear = entityliving.getCanSpawnHere();
    }
    return spaceClear;
  }

  Entity createEntity(boolean persistEntity) {
    Entity ent = EntityList.createEntityByName(behaviour.getEntityTypeName(), world);
//    if(persistEntity && behaviour.getMinPlayerDistance() <= 0 && behaviour.getDespawnTimeSeconds() > 0 && ent instanceof EntityLiving) {
//      ent.getEntityData().setLong(VirtualSpawnerBehaviour.KEY_DESPAWN_TIME, world.getTotalWorldTime() + behaviour.getDespawnTimeSeconds() * 20);
    if(persistEntity && ent instanceof EntityLiving) {
      ((EntityLiving) ent).func_110163_bv();
    }
    return ent;
  }

}