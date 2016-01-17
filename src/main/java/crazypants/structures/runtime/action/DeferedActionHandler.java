package crazypants.structures.runtime.action;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.IAction;
import crazypants.structures.api.util.Point3i;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class DeferedActionHandler {

  public static final DeferedActionHandler INSTANCE = new DeferedActionHandler();

  static {
    INSTANCE.register();
  }

  private boolean registered = false;

  private final List<TimedAction> deferedActions = new ArrayList<TimedAction>();

  public void addDeferedAction(IAction action, World world, IStructure structure, Point3i worldPosition, int delay) {
    deferedActions.add(new TimedAction(action, world, structure, worldPosition, delay));
  }

  @SubscribeEvent
  public void update(ServerTickEvent evt) {
    ListIterator<TimedAction> iter = deferedActions.listIterator();
    while(iter.hasNext()) {
      TimedAction action = iter.next();
      if(action.performThisTick()) {
        iter.remove();
      }
    }
  }

  protected void register() {
    if(!registered) {
      registered = true;      
      MinecraftForge.EVENT_BUS.register(this);
    }
  }

  protected void deregister() {
    if(registered) {
      MinecraftForge.EVENT_BUS.unregister(this);
      registered = false;
    }
  }

  private static class TimedAction {

    final IAction action;

    final World world;
    final IStructure structure;
    final Point3i worldPosition;

    int delay;

    public TimedAction(IAction action, World world, IStructure structure, Point3i worldPosition, int delay) {
      this.action = action;
      this.world = world;
      this.structure = structure;
      this.worldPosition = worldPosition;
      this.delay = delay;
    }

    public boolean performThisTick() {
      --delay;
      if(delay > 0) {
        return false;  
      }
      action.doAction(world, structure, worldPosition);
      return true;
    }

  }
}