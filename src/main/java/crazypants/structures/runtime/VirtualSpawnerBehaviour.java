package crazypants.structures.runtime;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.IBehaviour;
import crazypants.structures.api.util.Point3i;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class VirtualSpawnerBehaviour implements IBehaviour {

  private static final String KEY_SPAWN_TIME = "VirtualSpawnerBehaviour_spawnTime";
  
  private Point3i structureLocalPosition = new Point3i();

  private String entityTypeName = "Pig";

  private int minSpawnDelay = 200;
  private int maxSpawnDelay = 800;
  private int minPlayerDistance = 16;

  private int spawnCount = 4;
  private int maxSpawnRetries = 3;
  private int spawnRange = 4;
  private int maxNearbyEntities = 6;
  
  private boolean useVanillaSpawnChecks = true;

  private Map<InstanceKey, SpawnerInstance> instances = new HashMap<InstanceKey, SpawnerInstance>();

  private int despawnTimeSeconds = 120;


  @Override
  public void onStructureGenerated(World world, IStructure structure) {
    onStructureLoaded(world, structure);
  }

  @Override
  public void onStructureLoaded(World world, IStructure structure) {    
    InstanceKey key = new InstanceKey(structure, this);
    SpawnerInstance instance = new SpawnerInstance(this, world, key.worldPos);
    instances.put(key, instance);
    instance.onLoad();
  }

  @Override
  public void onStructureUnloaded(World world, IStructure structure) {
    InstanceKey key = new InstanceKey(structure, this);
    SpawnerInstance instance = instances.get(key);
    if(instance != null) {
      instance.onUnload();
      instances.remove(key);
    }
  }

 
  
  

  public static class SpawnerInstance {

    private VirtualSpawnerBehaviour behaviour;
    private Point3i worldPos;
    private World world;

    private int ticksTillNextSpawn = 20;
    private int remainingSpawnTries;
    
    private boolean registered = false;;

    public SpawnerInstance(VirtualSpawnerBehaviour behaviour, World world, Point3i worldPos) {
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

      if(ticksTillNextSpawn == -1) {
        resetTimer();
      }
      if(ticksTillNextSpawn > 0) {
        --ticksTillNextSpawn;
        return;
      }
      
      remainingSpawnTries = behaviour.spawnCount + behaviour.maxSpawnRetries;
      for (int i = 0; i < behaviour.spawnCount && remainingSpawnTries > 0; ++i) {        
        if(!trySpawnEntity()) {                              
          break;
        }
      }

    }

    private void resetTimer() {
      if(behaviour.maxSpawnDelay <= behaviour.minSpawnDelay) {
        this.ticksTillNextSpawn = behaviour.minSpawnDelay;
      } else {
        int i = behaviour.maxSpawnDelay - behaviour.minSpawnDelay;
        ticksTillNextSpawn = behaviour.minSpawnDelay + world.rand.nextInt(i);
      }
    }

    private boolean isActivated() {      
      if(behaviour.minPlayerDistance > 0) {
        return world.getClosestPlayer(worldPos.x + 0.5D, worldPos.y + 0.5D, worldPos.z + 0.5D, behaviour.minPlayerDistance) != null;
      }
      return true;
    }
    
    protected boolean trySpawnEntity() {
      Entity entity = createEntity(true);
      if(!(entity instanceof EntityLiving)) {
        System.out.println("VirtualSpawnerBehaviour.SpawnerInstance.trySpawnEntity: Failed to create entity");
        return false;
      }
      EntityLiving entityliving = (EntityLiving) entity;
      int spawnRange = behaviour.spawnRange;

      if(behaviour.maxNearbyEntities > 0) {
        int nearbyEntities = world.getEntitiesWithinAABB(entity.getClass(),
            AxisAlignedBB.getBoundingBox(
                worldPos.x - spawnRange*2, worldPos.y - 4, worldPos.z - spawnRange*2,
                worldPos.x + spawnRange*2, worldPos.y + 4, worldPos.z + spawnRange*2)).size();

        if(nearbyEntities >= behaviour.maxNearbyEntities) {
//          System.out.println("VirtualSpawnerBehaviour.SpawnerInstance.trySpawnEntity: To many entities " + worldPos);
          return false;
        }
      }

      while(remainingSpawnTries-- > 0) {
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
      if(spaceClear && behaviour.useVanillaSpawnChecks) {
        //Full checks for lighting, dimension etc 
        spaceClear = entityliving.getCanSpawnHere();
      }
      return spaceClear;
    }
    
    Entity createEntity(boolean forceAlive) {
      Entity ent = EntityList.createEntityByName(behaviour.entityTypeName, world);
      if(forceAlive && behaviour.minPlayerDistance <= 0 && behaviour.despawnTimeSeconds > 0 && ent instanceof EntityLiving) {
         ent.getEntityData().setLong(KEY_SPAWN_TIME, world.getTotalWorldTime());
        ((EntityLiving) ent).func_110163_bv();
      }
      return ent;
    }

  }

  public static class InstanceKey {

    private final String structureUid;
    private final Point3i worldPos;

    public InstanceKey(IStructure s, VirtualSpawnerBehaviour b) {
      structureUid = s.getUid();
      worldPos = s.transformLocalToWorld(b.structureLocalPosition);
    }

    public InstanceKey(String structureUid, Point3i worldPos) {
      this.structureUid = structureUid;
      this.worldPos = worldPos;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((structureUid == null) ? 0 : structureUid.hashCode());
      result = prime * result + ((worldPos == null) ? 0 : worldPos.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if(this == obj)
        return true;
      if(obj == null)
        return false;
      if(getClass() != obj.getClass())
        return false;
      InstanceKey other = (InstanceKey) obj;
      if(structureUid == null) {
        if(other.structureUid != null)
          return false;
      } else if(!structureUid.equals(other.structureUid))
        return false;
      if(worldPos == null) {
        if(other.worldPos != null)
          return false;
      } else if(!worldPos.equals(other.worldPos))
        return false;
      return true;
    }

  }

}
