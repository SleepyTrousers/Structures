package crazypants.structures.api.runtime;

import crazypants.structures.api.gen.IStructure;
import net.minecraft.world.World;

public interface ICondition {

  /**
   * Creating a new instance of the condition for each structure allows it to 
   * store state specific to that instance
   * @return
   */
  ICondition createPerStructureInstance(World world, IStructure structure);
  
  boolean isConditionMet(World world, IStructure structure);
  
}
