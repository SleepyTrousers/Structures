package crazypants.structures.runtime.condition;

import com.google.gson.annotations.Expose;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.util.Point3i;
import net.minecraft.world.World;

public class PlayerInRangeCondition extends PositionedCondition {

  public PlayerInRangeCondition() {
    super("PlayerInRange");
  }

  @Expose
  private int range = 32;  
    
  public int getRange() {
    return range;
  }

  public void setRange(int distance) {
    this.range = distance;
  }

  @Override
  protected boolean doIsConditonMet(World world, IStructure structure, Point3i worldPos) {  
    return world.getClosestPlayer(worldPos.x + 0.5D, worldPos.y + 0.5D, worldPos.z + 0.5D, range) != null;
  }
  
}
