package crazypants.structures.runtime.action;

import java.util.Random;

import com.google.gson.annotations.Expose;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.IAction;
import crazypants.structures.api.util.Point3i;
import net.minecraft.world.World;

public class RandomizerAction extends CompositeAction {

  private static final Random RND = new Random();

  @Expose
  private final Point3i minOffset = new Point3i();
  @Expose
  private final Point3i maxOffset = new Point3i();
  
  @Expose
  private int minDelay = 0;
  @Expose
  private int maxDelay = 0;

  @Expose
  private int minRepeats = 1;
  @Expose
  private int maxRepeats = 1;

  public RandomizerAction() {
  }

  public RandomizerAction(RandomizerAction template) {
    minOffset.set(template.minOffset);
    maxOffset.set(template.maxOffset);
    minRepeats = template.minRepeats;
    maxRepeats = template.maxRepeats;

    minDelay = template.minDelay;
    maxDelay = template.maxDelay;
  }

  @Override
  public void doAction(World world, IStructure structure, Point3i worldPos) {
    int repeats = randomInt(minRepeats, maxRepeats);
    for (int i = 0; i < repeats; i++) {
      for (IAction action : getActions()) {
        worldPos = randomizePos(worldPos);
        int delay = randomInt(minDelay, maxDelay);
        if(delay > 0) {
          DeferedActionHandler.INSTANCE.addDeferedAction(action, world, structure, worldPos, delay);
        } else {
          action.doAction(world, structure, worldPos);
        }
      }
    }
  }

  @Override
  protected CompositeAction doCreateInstance() {
    return new RandomizerAction(this);
  }

  public int getMinDelay() {
    return minDelay;
  }

  public void setMinDelay(int minDelay) {
    this.minDelay = minDelay;
  }

  public int getMaxDelay() {
    return maxDelay;
  }

  public void setMaxDelay(int maxDelay) {
    this.maxDelay = maxDelay;
  }

  public int getMinRepeats() {
    return minRepeats;
  }

  public void setMinRepeats(int minRepeats) {
    this.minRepeats = minRepeats;
  }

  public int getMaxRepeats() {
    return maxRepeats;
  }

  public void setMaxRepeats(int maxRepeats) {
    this.maxRepeats = maxRepeats;
  }

  public Point3i getMinOffset() {
    return minOffset;
  }

  public Point3i getMaxOffset() {
    return maxOffset;
  }

  public void setMinOffset(Point3i offset) {
    if(offset == null) {
      minOffset.set(0, 0, 0);
    } else {
      minOffset.set(offset);
    }
  }

  public void setMaxOffset(Point3i offset) {
    if(offset == null) {
      maxOffset.set(0, 0, 0);
    } else {
      maxOffset.set(offset);
    }
  }

  private Point3i randomizePos(Point3i worldPos) {
    Point3i res = new Point3i(worldPos);
    res.x = res.x + randomInt(minOffset.x, maxOffset.x);
    res.y = res.y + randomInt(minOffset.y, maxOffset.y);
    res.z = res.z + randomInt(minOffset.z, maxOffset.z);
    return res;
  }

  private int randomInt(int min, int max) {
    if(max <= min) {
      return min;
    }
    return min + RND.nextInt(max - min + 1);
  }

}
