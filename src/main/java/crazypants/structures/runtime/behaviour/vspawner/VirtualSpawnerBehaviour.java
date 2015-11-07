package crazypants.structures.runtime.behaviour.vspawner;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.IBehaviour;
import crazypants.structures.api.runtime.ICondition;
import crazypants.structures.runtime.behaviour.Positioned;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class VirtualSpawnerBehaviour extends Positioned implements IBehaviour {
 
//TODO: Allow for a list of entities
  private String entityTypeName = "Pig";  
  
  private String entityNbtText = "";

  private ICondition activeCondition;
  
  private ICondition spawnCondition;

  private int numSpawned = 4;
  private int maxSpawnRetries = 3;
  private int spawnRange = 4;

  private boolean useVanillaSpawnChecks = true;

  private boolean renderParticles = true;

  private boolean persistEntities = false;
  
  
  //-------------- runtime / per instance state
  private final VirtualSpawnerInstance instance;
 
  public VirtualSpawnerBehaviour() {   
    this(null, null);
  }
  
  public VirtualSpawnerBehaviour(VirtualSpawnerBehaviour template, VirtualSpawnerInstance instance) {
    super(template);
    this.instance = instance;
  }


  @Override
  public NBTTagCompound getState() {  
    if(instance != null) {
      return instance.getState();
    }
    return null;
  }

  @Override
  public IBehaviour createInstance(World world, IStructure structure, NBTTagCompound state) {   
    return new VirtualSpawnerBehaviour(this, new VirtualSpawnerInstance(structure, this, world, getWorldPosition(structure), state));
  }


  @Override
  public void onStructureGenerated(World world, IStructure structure) {
    onStructureLoaded(world, structure, null);
  }

  @Override
  public void onStructureLoaded(World world, IStructure structure, NBTTagCompound state) {
    if(instance != null) {
      instance.onLoad();      
    }
  }

  @Override
  public void onStructureUnloaded(World world, IStructure structure) {
    if(instance != null) {
      instance.onUnload();
    }    
  }
  

  public ICondition getActiveCondition() {
    return activeCondition;
  }

  public void setActiveCondition(ICondition activeCondition) {
    this.activeCondition = activeCondition;
  }

  public ICondition getSpawnCondition() {
    return spawnCondition;
  }

  public void setSpawnCondition(ICondition spawnCondition) {
    this.spawnCondition = spawnCondition;
  }

  public String getEntityTypeName() {
    return entityTypeName;
  }

  public void setEntityTypeName(String entityTypeName) {
    this.entityTypeName = entityTypeName;
  }
  
  public String getEntityNbtText() {
    return entityNbtText;
  }

  public void setEntityNbtText(String entityNbtText) {
    this.entityNbtText = entityNbtText;
  }

  public int getNumberSpawned() {
    return numSpawned;
  }

  public void setNumberSpawned(int spawnCount) {
    this.numSpawned = spawnCount;
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

  public boolean isUseVanillaSpawnChecks() {
    return useVanillaSpawnChecks;
  }

  public void setUseVanillaSpawnChecks(boolean useVanillaSpawnChecks) {
    this.useVanillaSpawnChecks = useVanillaSpawnChecks;
  }

  public boolean isRenderParticles() {
    return renderParticles;
  }

  public void setRenderParticles(boolean renderParticles) {
    this.renderParticles = renderParticles;
  }

  public boolean isPersistEntities() {
    return persistEntities;
  }

  public void setPersistEntities(boolean persistEntities) {
    this.persistEntities = persistEntities;
  }
  
}
