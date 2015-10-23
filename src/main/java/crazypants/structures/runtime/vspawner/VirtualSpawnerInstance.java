package crazypants.structures.runtime.vspawner;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import crazypants.structures.PacketHandler;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.ICondition;
import crazypants.structures.api.util.Point3i;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.World;

public class VirtualSpawnerInstance {

  static {
    PacketHandler.INSTANCE.registerMessage(PacketSpawnParticles.class, PacketSpawnParticles.class, PacketHandler.nextID(), Side.CLIENT);
  }

  private IStructure structure;
  private VirtualSpawnerBehaviour behaviour;
  private Point3i worldPos;
  private World world;

  private int remainingSpawnTries;

  private boolean registered = false;

  private ICondition activeCondition;
  private ICondition spawnCondition;

  public VirtualSpawnerInstance(IStructure structure, VirtualSpawnerBehaviour behaviour, World world, Point3i worldPos) {
    this.structure = structure;
    this.behaviour = behaviour;
    this.world = world;
    this.worldPos = worldPos;

    if(behaviour.getActiveCondition() != null) {
      activeCondition = behaviour.getActiveCondition().createPerStructureInstance(world, structure);
    }
    if(behaviour.getSpawnCondition() != null) {
      spawnCondition = behaviour.getSpawnCondition().createPerStructureInstance(world, structure);
    }
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
    if(evt.world != world) {
      return;
    }
    if(activeCondition != null && !activeCondition.isConditionMet(world, structure)) {
      return;
    }

    spawnActiveParticles();

    if(spawnCondition != null && !spawnCondition.isConditionMet(world, structure)) {
      return;
    }
    remainingSpawnTries = behaviour.getNumberSpawned() + behaviour.getMaxSpawnRetries();
    for (int i = 0; i < behaviour.getNumberSpawned() && remainingSpawnTries > 0; ++i) {
      if(!trySpawnEntity()) {
        break;
      }
    }

  }

  private void spawnActiveParticles() {
    if(!behaviour.isRenderParticles()) {
      return;
    }
    PacketHandler.INSTANCE.sendToAllAround(new PacketSpawnParticles(worldPos),
        new TargetPoint(world.provider.dimensionId, worldPos.x, worldPos.y, worldPos.z, 64));
  }

 
  protected boolean trySpawnEntity() {

    EntityLiving entityliving = createEntity();
    if(entityliving == null) {
      return false;
    }
    int spawnRange = behaviour.getSpawnRange();
    while (remainingSpawnTries-- > 0) {
      double x = worldPos.x + (world.rand.nextDouble() - world.rand.nextDouble()) * spawnRange;
      double y = worldPos.y + world.rand.nextInt(3) - 1;
      double z = worldPos.z + (world.rand.nextDouble() - world.rand.nextDouble()) * spawnRange;
      entityliving.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360.0F, 0.0F);

      if(canSpawnEntity(entityliving)) {
        entityliving.onSpawnWithEgg(null);
        world.spawnEntityInWorld(entityliving);
        //world.playAuxSFX(2004, worldPos.x, worldPos.y, worldPos.z, 0);
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

  EntityLiving createEntity() {
    Entity ent = EntityList.createEntityByName(behaviour.getEntityTypeName(), world);
    if(!(ent instanceof EntityLiving)) {
      return null;
    }
    EntityLiving res = (EntityLiving)ent;
    if(behaviour.isPersistEntities()) {
      res.func_110163_bv();
    }
    return res;
  }

}