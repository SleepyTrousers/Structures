package crazypants.structures.runtime.behaviour;

import cpw.mods.fml.common.FMLCommonHandler;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.IBehaviour;
import crazypants.structures.api.runtime.IStateful;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class AbstractEventBehaviour implements IBehaviour {

  private boolean registered = false;

  @Override
  public void onStructureGenerated(World world, IStructure structure) {   
    register();
  }

  @Override
  public void onStructureLoaded(World world, IStructure structure, NBTTagCompound state) {
    register();
  }

  
  @Override
  public void onStructureUnloaded(World world, IStructure structure) {
    deregister();
  }

  protected void register() {
    if(!registered) {
      FMLCommonHandler.instance().bus().register(this);
      registered = true;
    }
  }

  protected void deregister() {
    if(registered) {
      FMLCommonHandler.instance().bus().unregister(this);
      registered = false;
    }
  }
  
  protected boolean addSubState(NBTTagCompound root, String name, IStateful stateful) {
    if(stateful == null || name == null) {
      return false;
    }
    NBTTagCompound st = stateful.getState();
    if(st != null) {
      root.setTag(name, st);
      return true;
    }
    return false;
  }
  
  protected NBTTagCompound getSubState(NBTTagCompound root, String name) {
    if(root == null || name == null) {
      return null;
    }
    return root.getCompoundTag(name);
  }
  
}
