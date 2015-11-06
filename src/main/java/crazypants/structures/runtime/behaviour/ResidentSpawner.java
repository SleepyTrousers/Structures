package crazypants.structures.runtime.behaviour;

import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import crazypants.structures.StructureUtils;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.IAction;
import crazypants.structures.api.runtime.IBehaviour;
import crazypants.structures.api.runtime.ICondition;
import crazypants.structures.api.util.Point3i;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class ResidentSpawner extends AbstractEventBehaviour {

  //template variables
  private String entityName = "";
  private String entityNbtText = "";
  private int numToSpawn = 1;
  private int spawnRange = 4;
  private int respawnRate = 200;
  private int homeRadius = 64;
  private Point3i localPos = new Point3i();
  private String taggedPos;

  //instance variables
  private Selector selector;
  private int checkPeriod = respawnRate / 10;
  private World world;
  private IStructure structure;
  private long lastTimePresent = -1;
  private int residentId = -1;
  private NBTTagCompound entityNBT;

  private ICondition preCondition;
  private IAction onSpawnAction;

  public ResidentSpawner() {
  }

  public ResidentSpawner(ResidentSpawner template, World world, IStructure structure, NBTTagCompound state) {

    this.world = world;
    this.structure = structure;

    this.entityName = template.entityName;
    this.entityNbtText = template.entityNbtText;
    updateEntityNBT();
    
    respawnRate = template.respawnRate;
    checkPeriod = respawnRate / 10;
    homeRadius = template.homeRadius;
                    
    localPos = template.localPos;
    taggedPos = template.taggedPos;    
    localPos = StructureUtils.getTaggedLocation(taggedPos, structure, localPos != null ? localPos : new Point3i());
    
    
    numToSpawn = template.numToSpawn;
    spawnRange = template.spawnRange;

    if(template.getPreCondition() != null) {
      preCondition = template.getPreCondition().createInstance(world, structure, getSubState(state, "preCondition"));
    }
    if(template.getOnSpawnAction() != null) {
      onSpawnAction = template.getOnSpawnAction().createInstance(world, structure, getSubState(state, "onSpawnAction"));
    }

    if(state != null) {
      if(state.hasKey("lastTimePresent")) {
        lastTimePresent = state.getLong("lastTimePresent");
      }
      if(state.hasKey("residentId")) {
        residentId = state.getInteger("residentId");
      }
    }
    selector = new Selector();

  }

  @Override
  public IBehaviour createInstance(World world, IStructure structure, NBTTagCompound state) {
    return new ResidentSpawner(this, world, structure, state);
  }

  @Override
  public NBTTagCompound getState() {
    NBTTagCompound res = new NBTTagCompound();
    if(lastTimePresent > 0) {
      res.setLong("lastTimePresent", lastTimePresent);
    }
    if(residentId >= 0) {
      res.setLong("residentId", residentId);
    }
    addSubState(res, "preCondition", preCondition);
    addSubState(res, "onSpawnAction", onSpawnAction);
    return res.hasNoTags() ? null : res;

  }

  @Override
  public void onStructureGenerated(World world, IStructure structure) {
    super.onStructureGenerated(world, structure);
    residentId = world.rand.nextInt(Integer.MAX_VALUE);
    spawnResidents(numToSpawn);
  }

  @SubscribeEvent
  public void update(ServerTickEvent evt) {
    long curTime = world.getTotalWorldTime();
    if(curTime % checkPeriod != 0) {
      return;
    }

    if(preCondition != null && !preCondition.isConditionMet(world, structure)) {
      return;
    }

    int curNum = getNumResidentsInHomeBounds();
    if(curNum >= numToSpawn) { //TODO: Optional to wait for all to be dead?
      lastTimePresent = world.getTotalWorldTime();
    } else if(curTime - lastTimePresent >= respawnRate) {
      spawnResidents(numToSpawn - curNum);
    }
  }

  private int getNumResidentsInHomeBounds() {    
    Point3i worldPos = structure.transformLocalToWorld(localPos);
    List<?> ents = world.selectEntitiesWithinAABB(EntityLiving.class,
        AxisAlignedBB.getBoundingBox(
            worldPos.x - homeRadius, worldPos.y - homeRadius, worldPos.z - homeRadius,
            worldPos.x + homeRadius, worldPos.y + homeRadius, worldPos.z + homeRadius),
        selector);
    return ents.size();

  }

  private void spawnResidents(int numToSpawn) {
    if(numToSpawn <= 0) {
      return;
    }
    lastTimePresent = world.getTotalWorldTime();
    Point3i worldPos = structure.transformLocalToWorld(localPos);
    for (int i = 0; i < numToSpawn; i++) {
      EntityLiving ent = createEntity();
      if(ent == null) {
        return;
      }
      if(StructureUtils.spawnEntity(world, ent, worldPos, spawnRange, 6, false)) {
        if(onSpawnAction != null) {
          onSpawnAction.doAction(world, structure, new Point3i((int) ent.posX, (int) ent.posY, (int) ent.posZ));
        }
      }
    }

  }

  EntityLiving createEntity() {
  
    Entity ent = EntityList.createEntityFromNBT(entityNBT, world);
    if(!(ent instanceof EntityLiving)) {
      return null;
    }
    EntityLiving res = (EntityLiving) ent;

    res.func_110163_bv(); //persist  
    res.getEntityData().setInteger("residentId", residentId);

    Point3i worldPos = structure.transformLocalToWorld(localPos);
    if(res instanceof EntityCreature) {
      EntityCreature creature = (EntityCreature) res;
      creature.setHomeArea(worldPos.x, worldPos.y, worldPos.z, homeRadius - 1);
    }
    return res;
  }

  public String getEntity() {
    return entityName;
  }

  public final void setEntity(String entity) {
    this.entityName = entity;
    updateEntityNBT();
  }

  public String getEntityNbtText() {
    return entityNbtText;
  }

  public void setEntityNbtText(String entityNbtText) {
    this.entityNbtText = entityNbtText;
    updateEntityNBT();
  }

  private void updateEntityNBT() {
    entityNBT = StructureUtils.createEntityNBT(entityName, entityNbtText);    
  }

  public int getNumSpawned() {
    return numToSpawn;
  }

  public void setNumSpawned(int numSpawned) {
    this.numToSpawn = numSpawned;
  }

  public int getSpawnRange() {
    return spawnRange;
  }

  public void setSpawnRange(int spawnRange) {
    this.spawnRange = spawnRange;
  }

  public int getRespawnRate() {
    return respawnRate;
  }

  public void setRespawnRate(int respawnRate) {
    this.respawnRate = respawnRate;
    checkPeriod = respawnRate / 10;
  }

  public int getHomeRadius() {
    return homeRadius;
  }

  public void setHomeRadius(int homeRadius) {
    this.homeRadius = homeRadius;
  }

  public Point3i getLocalPos() {
    return localPos;
  }

  public void setLocalPos(Point3i localPos) {
    this.localPos = localPos;
  }

  public String getTaggedPos() {
    return taggedPos;
  }

  public void setTaggedPos(String taggedPos) {
    this.taggedPos = taggedPos;
  }

  public ICondition getPreCondition() {
    return preCondition;
  }

  public void setPreCondition(ICondition preCondition) {
    this.preCondition = preCondition;
  }

  public IAction getOnSpawnAction() {
    return onSpawnAction;
  }

  public void setOnSpawnAction(IAction onSpawnAction) {
    this.onSpawnAction = onSpawnAction;
  }

  private class Selector implements IEntitySelector {

    @Override
    public boolean isEntityApplicable(Entity ent) {
      String entityId = EntityList.getEntityString(ent);
      if(!entityName.equals(entityId)) {
        return false;
      }
      NBTTagCompound data = ent.getEntityData();
      int resId = data.getInteger("residentId");
      return resId == residentId;
    }

  }

}
