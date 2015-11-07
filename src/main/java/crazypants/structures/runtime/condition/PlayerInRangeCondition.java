package crazypants.structures.runtime.condition;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.util.Point3i;
import net.minecraft.world.World;

public class PlayerInRangeCondition extends PositionedCondition {

  private int distance = 32;
  
    
  public int getRange() {
    return distance;
  }

  public void setRange(int distance) {
    this.distance = distance;
  }

  @Override
  protected boolean doIsConditonMet(World world, IStructure structure, Point3i worldPos) {  
    return world.getClosestPlayer(worldPos.x + 0.5D, worldPos.y + 0.5D, worldPos.z + 0.5D, distance) != null;
  }
  
}
