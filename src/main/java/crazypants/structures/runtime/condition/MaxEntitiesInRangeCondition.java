package crazypants.structures.runtime.condition;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.util.Point3i;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class MaxEntitiesInRangeCondition extends PositionedCondition {

  @Expose
  private int maxEntities = 10;
  
  @Expose
  private int range = 32;
  
  @Expose
  private List<String> entities = new ArrayList<String>();
  
  private transient Selector selector = new Selector();

  @Override
  protected boolean doIsConditonMet(World world, IStructure structure, Point3i worldPos) {  
    if(range <= 0) {
      return true;
    }            
    int nearbyEntities = world.selectEntitiesWithinAABB(EntityLiving.class,
        AxisAlignedBB.getBoundingBox(
            worldPos.x - range, worldPos.y - range, worldPos.z - range,
            worldPos.x + range, worldPos.y + range, worldPos.z + range),
        selector)
        .size();

    return nearbyEntities < maxEntities;
  }
  
  public int getMaxEntities() {
    return maxEntities;
  }

  public void setMaxEntities(int maxEntities) {
    this.maxEntities = maxEntities;
  }

  public int getRange() {
    return range;
  }

  public void setRange(int range) {
    this.range = range;
  }

  public List<String> getEntities() {
    return entities;
  }

  public void setEntities(List<String> ents) {
    this.entities = ents;
  }

  private class Selector implements IEntitySelector {

    @Override
    public boolean isEntityApplicable(Entity ent) {
      if(entities.isEmpty()) {
        return true;
      }
      String entityId = EntityList.getEntityString(ent);
      return entities.contains(entityId);
    }

  }

}
