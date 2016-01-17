package crazypants.structures.runtime.behaviour.vspawner;

import crazypants.structures.PacketHandler;
import crazypants.structures.StructureUtils;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.ICondition;
import crazypants.structures.api.util.Point3i;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;

public class VirtualSpawnerInstance {

  static {
    PacketHandler.INSTANCE.registerMessage(PacketSpawnParticles.class, PacketSpawnParticles.class, PacketHandler.nextID(), Side.CLIENT);
  }

  private final IStructure structure;
  private final VirtualSpawnerBehaviour behaviour;
  private final Point3i worldPos;
  private final World world;

  private int remainingSpawnTries;

  private ICondition activeCondition;
  private ICondition spawnCondition;

  private boolean registered = false;
  
  private NBTTagCompound entityNBT;
  
  public VirtualSpawnerInstance(IStructure structure, VirtualSpawnerBehaviour behaviour, World world, Point3i worldPos, NBTTagCompound state) {
    this.structure = structure;
    this.behaviour = behaviour;
    this.world = world;                    
    this.worldPos = worldPos;
    
    entityNBT = StructureUtils.createEntityNBT(behaviour.getEntityTypeName(), behaviour.getEntityNbtText()); 

    if(behaviour.getActiveCondition() != null) {
      NBTTagCompound conState = state == null ? null : state.getCompoundTag("activeCondition");
      activeCondition = behaviour.getActiveCondition().createInstance(world, structure, conState);
    }
    if(behaviour.getSpawnCondition() != null) {
      NBTTagCompound conState = state == null ? null : state.getCompoundTag("spawnCondition");
      spawnCondition = behaviour.getSpawnCondition().createInstance(world, structure, conState);
    }
  }

  public void onLoad() {
    if(!registered) {      
      MinecraftForge.EVENT_BUS.register(this);
      registered = true;
    }
  }

  public void onUnload() {
    if(registered) {
      MinecraftForge.EVENT_BUS.unregister(this);
      registered = false;
    }
  }

  @SubscribeEvent
  public void update(WorldTickEvent evt) {
    if(evt.world != world) {
      return;
    }
    if(activeCondition != null && !activeCondition.isConditionMet(world, structure, worldPos)) {
      return;
    }

    spawnActiveParticles();

    if(spawnCondition != null && !spawnCondition.isConditionMet(world, structure, worldPos)) {
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
        new TargetPoint(world.provider.getDimensionId(), worldPos.x, worldPos.y, worldPos.z, 64));
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

      if(StructureUtils.canSpawnEntity(world, entityliving, behaviour.isUseVanillaSpawnChecks())) {
        entityliving.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(x,y,z)), null);
        world.spawnEntityInWorld(entityliving);
        //world.playAuxSFX(2004, worldPos.x, worldPos.y, worldPos.z, 0);
        entityliving.spawnExplosionParticle();
        return true;
      }
    }

    return false;
  }

  EntityLiving createEntity() {
    //Entity ent = EntityList.createEntityByName(behaviour.getEntityTypeName(), world);
    
    Entity ent = EntityList.createEntityFromNBT(entityNBT, world);
    if(!(ent instanceof EntityLiving)) {
      return null;
    }
    EntityLiving res = (EntityLiving)ent;
    if(behaviour.isPersistEntities()) {
      res.enablePersistence();
    }
    return res;
  }

  public NBTTagCompound getState() {
    
    NBTTagCompound res = new NBTTagCompound();
    
    NBTTagCompound acs = activeCondition.getState();
    if(acs != null && !acs.hasNoTags()) {
      res.setTag("activeCondition", acs);  
    }
    
    NBTTagCompound scs = spawnCondition.getState();
    if(scs != null && !scs.hasNoTags()) {
      res.setTag("spawnCondition", scs);  
    }        
    return res.hasNoTags() ? null : res;
  }

}