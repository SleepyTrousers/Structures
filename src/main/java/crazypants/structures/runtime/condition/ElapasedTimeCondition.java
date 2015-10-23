package crazypants.structures.runtime.condition;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.ICondition;
import net.minecraft.world.World;

public class ElapasedTimeCondition implements ICondition {

  private int initialTime = 40;
  private int minTime = 100;
  private int maxTime = 200;
  private boolean persisted = false;

  private long conditionMetAtTime = -1;

  public ElapasedTimeCondition() {
    
  }
  
  public ElapasedTimeCondition(ElapasedTimeCondition template, World world) {
    initialTime = template.initialTime;
    minTime = template.minTime;
    maxTime = template.maxTime;

    conditionMetAtTime = world.getTotalWorldTime();
    if(initialTime >= 0) {
      conditionMetAtTime += initialTime;
    } else {
      updateConditionTime(world);
    }
  }

  @Override
  public ICondition createPerStructureInstance(World world, IStructure structure) {
    return new ElapasedTimeCondition(this, world);
  }

  @Override
  public boolean isConditionMet(World world, IStructure structure) {
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
