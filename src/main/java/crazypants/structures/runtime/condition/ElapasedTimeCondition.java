package crazypants.structures.runtime.condition;

import com.google.gson.annotations.Expose;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.ICondition;
import crazypants.structures.api.util.Point3i;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ElapasedTimeCondition implements ICondition {

  @Expose
  private int initialTime = 40;
  
  @Expose
  private int minTime = 100;
  
  @Expose
  private int maxTime = 200;
  
  @Expose
  private boolean persisted = false;

  private transient long conditionMetAtTime = -1;

  public ElapasedTimeCondition() {    
  }
  
  public ElapasedTimeCondition(ElapasedTimeCondition template, World world, NBTTagCompound state) {
    initialTime = template.initialTime;
    minTime = template.minTime;
    maxTime = template.maxTime;

    if(state != null && state.hasKey("conditionMetAtTime")) {
      conditionMetAtTime = state.getLong("conditionMetAtTime");
    } else {
      conditionMetAtTime = world.getTotalWorldTime();
      if(initialTime >= 0) {
        conditionMetAtTime += initialTime;
      } else {
        updateConditionTime(world);
      }
    }
  }
    
  @Override
  public NBTTagCompound getState() {
    if(!persisted) {
      return null;
    }
    NBTTagCompound res = new NBTTagCompound();
    res.setLong("conditionMetAtTime", conditionMetAtTime);
    return res;
  }

  @Override
  public ICondition createInstance(World world, IStructure structure, NBTTagCompound state) {
    return new ElapasedTimeCondition(this, world, state);
  }

  @Override
  public boolean isConditionMet(World world, IStructure structure, Point3i refPoint) {
    long curTime = world.getTotalWorldTime();
    if(curTime >= conditionMetAtTime) {
      updateConditionTime(world);
      return true;
    }
    return false;
  }

  private void updateConditionTime(World world) {
    conditionMetAtTime = world.getTotalWorldTime() + minTime;
    if(maxTime > minTime) {
      conditionMetAtTime += world.rand.nextInt(maxTime - minTime);
    }

  }

  public int getInitialTime() {
    return initialTime;
  }

  public void setInitialTime(int initialTime) {
    this.initialTime = initialTime;
  }

  public int getMinTime() {
    return minTime;
  }

  public void setMinTime(int minTime) {
    this.minTime = minTime;
  }

  public int getMaxTime() {
    return maxTime;
  }

  public void setMaxTime(int maxTime) {
    this.maxTime = maxTime;
  }

  public boolean isPersisted() {
    return persisted;
  }

  public void setPersisted(boolean persisted) {
    this.persisted = persisted;
  }

}
