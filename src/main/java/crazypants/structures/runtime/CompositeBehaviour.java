package crazypants.structures.runtime;

import java.util.ArrayList;
import java.util.List;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.IBehaviour;
import crazypants.structures.api.runtime.IStateful;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class CompositeBehaviour implements IBehaviour {

  private final List<IBehaviour> behaviours = new ArrayList<IBehaviour>();
  
  public CompositeBehaviour() {
    
  }
    

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
  public void onStructureLoaded(World world, IStructure structure, NBTTagCompound state) {
    for (IBehaviour b : behaviours) {
      b.onStructureLoaded(world, structure, state);
    }
  }

  @Override
  public void onStructureUnloaded(World world, IStructure structure) {
    for (IBehaviour b : behaviours) {
      b.onStructureUnloaded(world, structure);
    }
  }

  @Override
  public NBTTagCompound getState() {    
    return getChildStates(behaviours);
   
  }

  @Override
  public IBehaviour createInstance(World world, IStructure structure, NBTTagCompound state) {
    
    NBTTagList childStates; 
    if(state != null && state.hasKey("childStates")) {
      childStates = (NBTTagList)state.getTag("childStates");
    } else {
      childStates = new NBTTagList();
    }
    
    int index = 0;
    CompositeBehaviour res = new CompositeBehaviour();
    for(IBehaviour b : behaviours) {
      NBTTagCompound childState = null;
      if(childStates.tagCount() > index) {
        childState = childStates.getCompoundTagAt(index);
      }
      res.add(b.createInstance(world, structure, childState));
      ++index;
    }
    return res;
  }
  
  public static NBTTagCompound getChildStates(List<? extends IStateful> statefules) {
    NBTTagList childStates = new NBTTagList();
    for(IStateful con : statefules) {
      NBTTagCompound conState = con.getState();
      if(conState != null) {
        childStates.appendTag(conState);
      }
    }
    
    if(childStates.tagCount() <= 0) {
      return null;
    }    
    NBTTagCompound res = new NBTTagCompound();
    res.setTag("childStates", childStates);
    return res;
  }

}
