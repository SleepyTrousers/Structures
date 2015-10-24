package crazypants.structures.api.runtime;

import crazypants.structures.api.gen.IStructure;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public interface ICondition extends IStateful {

  /**
   * Creating a new instance of the condition for each structure allows it to 
   * store state specific to that instance
   * @param state 
   * @return
   */
  ICondition createInstance(World world, IStructure structure, NBTTagCompound state);
  
  
  
  boolean isConditionMet(World world, IStructure structure);
  
}
