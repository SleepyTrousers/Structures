package crazypants.structures.runtime.behaviour;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.IBehaviour;
import crazypants.structures.api.runtime.IStateful;
import crazypants.structures.runtime.PositionedType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public abstract class AbstractEventBehaviour extends PositionedType implements IBehaviour {

  private boolean registered = false;

  public AbstractEventBehaviour(String type) {
    super(type);
  }
  
  public AbstractEventBehaviour(AbstractEventBehaviour other) {
    super(other);
  }
  
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
      MinecraftForge.EVENT_BUS.register(this);
      registered = true;
    }
  }

  protected void deregister() {
    if(registered) {
      MinecraftForge.EVENT_BUS.unregister(this);
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
