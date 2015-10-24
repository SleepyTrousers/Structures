package crazypants.structures.api.runtime;

import crazypants.structures.api.gen.IStructure;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public interface IBehaviour extends IStateful {

  IBehaviour createInstance(World world, IStructure structure, NBTTagCompound state);
  
  @Override
  NBTTagCompound getState();

  
  
  void onStructureGenerated(World world, IStructure structure);
  
  void onStructureLoaded(World world, IStructure structure, NBTTagCompound state);
  
  void onStructureUnloaded(World world, IStructure structure);
  
}
