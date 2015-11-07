package crazypants.structures.runtime.condition;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.ICondition;
import crazypants.structures.api.util.Point3i;
import net.minecraft.world.World;

public class OrCondition extends AndCondition {

  @Override
  public boolean isConditionMet(World world, IStructure structure, Point3i refPoint) {
    for(ICondition con : getConditions()) {
      if(con.isConditionMet(world, structure, refPoint)) {
        return true;
      }
    }
    return false;
  }
  
}
