package crazypants.structures.runtime.condition;

import com.google.gson.annotations.Expose;

import crazypants.structures.AbstractTyped;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.ICondition;
import crazypants.structures.api.util.Point3i;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TickCountCondition extends AbstractTyped implements ICondition {

  @Expose
  private int initialCount = 40;
  
  @Expose
  private int minCount = 100;
  
  @Expose
  private int maxCount = 200;
  
  @Expose
  private boolean persisted = false;
  
  private int ticksUntilConditionMet = 0;

  public TickCountCondition() {
    super("TickCountCondition");
  }

  public TickCountCondition(TickCountCondition template, World world, NBTTagCompound state) {
    this();
    initialCount = template.initialCount;
    minCount = template.minCount;
    maxCount = template.maxCount;
    persisted = template.persisted;

    if(state != null && state.hasKey("ticksUntilConditionMet")) {
      ticksUntilConditionMet = state.getInteger("ticksUntilConditionMet");
    } else {
      if(initialCount >= 0) {
        ticksUntilConditionMet = initialCount;
      } else {
        updateRemainingTicks(world);
      }
    }
  }

  @Override
  public NBTTagCompound getState() {    
    if(!persisted) {
      return null;
    }
    NBTTagCompound res = new NBTTagCompound();
    res.setInteger("ticksUntilConditionMet", ticksUntilConditionMet);
    return res;
  }

  private void updateRemainingTicks(World world) {
    ticksUntilConditionMet = minCount;
    if(maxCount > minCount) {
      ticksUntilConditionMet += world.rand.nextInt(maxCount - minCount);
    }
  }

  @Override
  public ICondition createInstance(World world, IStructure structure, NBTTagCompound state) {
    return new TickCountCondition(this, world, state);
  }

  @Override
  public boolean isConditionMet(World world, IStructure structure, Point3i refPoint) {
    --ticksUntilConditionMet;
    if(ticksUntilConditionMet <= 0) {
      updateRemainingTicks(world);
      return true;
    }
    return false;
  }

  public int getInitialCount() {
    return initialCount;
  }

  public void setInitialCount(int initialCount) {
    this.initialCount = initialCount;
  }

  public int getMinCount() {
    return minCount;
  }

  public void setMinCount(int minCount) {
    this.minCount = minCount;
  }

  public int getMaxCount() {
    return maxCount;
  }

  public void setMaxCount(int maxCount) {
    this.maxCount = maxCount;
  }

  public boolean isPersisted() {
    return persisted;
  }

  public void setPersisted(boolean persisted) {
    this.persisted = persisted;
  }

}
