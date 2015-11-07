package crazypants.structures.runtime.condition;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.ICondition;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.runtime.behaviour.Positioned;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class PositionedCondition extends Positioned implements ICondition {


  public PositionedCondition() {
  }

  @Override
  public final boolean isConditionMet(World world, IStructure structure, Point3i worldPos) {    
    return doIsConditonMet(world, structure, getWorldPosition(structure, worldPos));
  }

  protected abstract boolean doIsConditonMet(World world, IStructure structure, Point3i worldPos);

  
  @Override
  public ICondition createInstance(World world, IStructure structure, NBTTagCompound state) {
    return this;
  }

  @Override
  public NBTTagCompound getState() {
    return null;
  }

}
