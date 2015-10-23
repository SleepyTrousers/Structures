package crazypants.structures.runtime.condition;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.ICondition;
import crazypants.structures.api.util.Point3i;
import net.minecraft.world.World;

public class PlayerInRangeCondition implements ICondition {

  private int distance = 32;
  private Point3i localPos = new Point3i();
  
  public int getRange() {
    return distance;
  }

  public void setRange(int distance) {
    this.distance = distance;
  }

  public Point3i getLocalPos() {
    return localPos;
  }

  public void setLocalPos(Point3i localPos) {
    this.localPos = localPos;
  }

  @Override
  public ICondition createPerStructureInstance(World world, IStructure structure) {
    return this;
  }

  @Override
  public boolean isConditionMet(World world, IStructure structure) {
    Point3i worldPos = structure.transformLocalToWorld(localPos);    
    return world.getClosestPlayer(worldPos.x + 0.5D, worldPos.y + 0.5D, worldPos.z + 0.5D, distance) != null;    
  }

}
