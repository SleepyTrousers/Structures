package crazypants.structures.runtime.condition;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.ICondition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class StatelessCondition implements ICondition {

  @Override
  public ICondition createInstance(World world, IStructure structure, NBTTagCompound state) {
    return this;
  }

  @Override
  public NBTTagCompound getState() {
    return null;
  }

}
