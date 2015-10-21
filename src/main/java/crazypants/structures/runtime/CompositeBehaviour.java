package crazypants.structures.runtime;

import java.util.ArrayList;
import java.util.List;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.IBehaviour;
import net.minecraft.world.World;

public class CompositeBehaviour implements IBehaviour {

  private final List<IBehaviour> behaviours = new ArrayList<IBehaviour>();

  public void add(IBehaviour be) {
    if(be != null) {
      behaviours.add(be);
    }
  }

  public void remove(IBehaviour be) {
    if(be != null) {
      behaviours.remove(be);
    }
  }

  @Override
  public void onStructureGenerated(World world, IStructure structure) {
    for (IBehaviour b : behaviours) {
      b.onStructureGenerated(world, structure);
    }
  }

  @Override
  public void onStructureLoaded(World world, IStructure structure) {
    for (IBehaviour b : behaviours) {
      b.onStructureLoaded(world, structure);
    }
  }

  @Override
  public void onStructureUnloaded(World world, IStructure structure) {
    for (IBehaviour b : behaviours) {
      b.onStructureUnloaded(world, structure);
    }
  }

}
