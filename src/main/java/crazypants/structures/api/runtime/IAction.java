package crazypants.structures.api.runtime;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.util.Point3i;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public interface IAction extends IStateful {

  /**
   * Creating a new instance of the condition for each structure allows it to 
   * store state specific to that instance
   * @param state 
   * @return
   */
  IAction createInstance(World world, IStructure structure, NBTTagCompound state);
  
  void doAction(World world, IStructure structure, Point3i worldPos);
  
}
