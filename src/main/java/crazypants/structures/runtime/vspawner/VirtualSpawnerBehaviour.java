package crazypants.structures.runtime.vspawner;

import java.util.HashMap;
import java.util.Map;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.IBehaviour;
import crazypants.structures.api.util.Point3i;
import net.minecraft.world.World;

public class VirtualSpawnerBehaviour implements IBehaviour {

  static final String KEY_DESPAWN_TIME = "VirtualSpawnerBehaviour_spawnTime";  
  
  private Point3i structureLocalPosition = new Point3i();

  private String entityTypeName = "Pig";

  private int minSpawnDelay = 200;
  private int maxSpawnDelay = 800;
  private int minPlayerDistance = 16;

  private int spawnCount = 4;
  private int maxSpawnRetries = 3;
  private int spawnRange = 4;
  private int maxNearbyEntities = 6;
  
  private int despawnTimeSeconds = 120;
  
  private boolean useVanillaSpawnChecks = true;
  
  private boolean renderParticles = true;

  private Map<InstanceKey, VirtualSpawnerInstance> instances = new HashMap<InstanceKey, VirtualSpawnerInstance>();

  public VirtualSpawnerBehaviour() {
    //Required to insure this is registered
    DespawnController.getInstance();
  }
  
  @Override
  public void onStructureGenerated(World world, IStructure structure) {
    onStructureLoaded(world, structure);
  }

  @Override
  public void onStructureLoaded(World world, IStructure structure) {    
    InstanceKey key = new InstanceKey(structure, this);
    VirtualSpawnerInstance instance = new VirtualSpawnerInstance(this, world, key.worldPos);
    instances.put(key, instance);
    instance.onLoad();
  }

  @Override
  public void onStructureUnloaded(World world, IStructure structure) {
    InstanceKey key = new InstanceKey(structure, this);
    VirtualSpawnerInstance instance = instances.get(key);
    if(instance != null) {
      instance.onUnload();
      instances.remove(key);
    }
  }

  public Point3i getStructureLocalPosition() {
    return structureLocalPosition;
  }

  public void setStructureLocalPosition(Point3i structureLocalPosition) {
    this.structureLocalPosition = structureLocalPosition;
  }

  public String getEntityTypeName() {
    return entityTypeName;
  }

  public void setEntityTypeName(String entityTypeName) {
    this.entityTypeName = entityTypeName;
  }

  public int getMinSpawnDelay() {
    return minSpawnDelay;
  }

  public void setMinSpawnDelay(int minSpawnDelay) {
    this.minSpawnDelay = minSpawnDelay;
  }

  public int getMaxSpawnDelay() {
    return maxSpawnDelay;
  }

  public void setMaxSpawnDelay(int maxSpawnDelay) {
    this.maxSpawnDelay = maxSpawnDelay;
  }

  public int getMinPlayerDistance() {
    return minPlayerDistance;
  }

  public void setMinPlayerDistance(int minPlayerDistance) {
    this.minPlayerDistance = minPlayerDistance;
  }

  public int getSpawnCount() {
    return spawnCount;
  }

  public void setSpawnCount(int spawnCount) {
    this.spawnCount = spawnCount;
  }

  public int getMaxSpawnRetries() {
    return maxSpawnRetries;
  }

  public void setMaxSpawnRetries(int maxSpawnRetries) {
    this.maxSpawnRetries = maxSpawnRetries;
  }

  public int getSpawnRange() {
    return spawnRange;
  }

  public void setSpawnRange(int spawnRange) {
    this.spawnRange = spawnRange;
  }

  public int getMaxNearbyEntities() {
    return maxNearbyEntities;
  }

  public void setMaxNearbyEntities(int maxNearbyEntities) {
    this.maxNearbyEntities = maxNearbyEntities;
  }

  public boolean isUseVanillaSpawnChecks() {
    return useVanillaSpawnChecks;
  }

  public void setUseVanillaSpawnChecks(boolean useVanillaSpawnChecks) {
    this.useVanillaSpawnChecks = useVanillaSpawnChecks;
  }

  public int getDespawnTimeSeconds() {
    return despawnTimeSeconds;
  }

  public void setDespawnTimeSeconds(int despawnTimeSeconds) {
    this.despawnTimeSeconds = despawnTimeSeconds;
  }

  public boolean isRenderParticles() {
    return renderParticles;
  }

  public void setRenderParticles(boolean renderParticles) {
    this.renderParticles = renderParticles;
  }

  private static class InstanceKey {

    private final String structureUid;
    private final Point3i worldPos;

    public InstanceKey(IStructure s, VirtualSpawnerBehaviour b) {
      structureUid = s.getUid();
      worldPos = s.transformLocalToWorld(b.structureLocalPosition);
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
