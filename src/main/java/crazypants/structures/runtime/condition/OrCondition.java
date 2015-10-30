package crazypants.structures.runtime.condition;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.ICondition;
import net.minecraft.world.World;

public class OrCondition extends AndCondition {

  @Override
  public boolean isConditionMet(World world, IStructure structure) {
    for(ICondition con : getConditions()) {
      if(con.isConditionMet(world, structure)) {
        return true;
      }
    }
    return false;
  }
  
}
