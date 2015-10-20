package crazypants.structures.api.runtime;

import crazypants.structures.api.gen.IStructure;
import net.minecraft.world.World;

public interface IBehaviour {

  void onStructureGenerated(World world, IStructure structure);
  
  void onStructureLoaded(World world, IStructure structure);
  
  void onStructureUnloaded(World world, IStructure structure);
  
}
