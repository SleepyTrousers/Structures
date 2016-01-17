package crazypants.structures.runtime.behaviour;

import com.google.gson.annotations.Expose;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.IAction;
import crazypants.structures.api.runtime.IBehaviour;
import crazypants.structures.api.runtime.ICondition;
import crazypants.structures.api.util.Point3i;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class ServerTickBehaviour extends AbstractEventBehaviour  {

  //Template variables  
  @Expose
  private int executionInterval;
  
  @Expose
  private ICondition condition;
  
  @Expose
  private IAction action;  
  
  //Runtime state
  private World world;
  private IStructure structure;
  private Point3i worldPosition;
  
  public ServerTickBehaviour() {    
    super("ServerTickBehaviour");
  }
  
  public ServerTickBehaviour(ServerTickBehaviour template, World world, IStructure strcuture, NBTTagCompound state) {
    super(template);
    this.world = world;
    this.structure = strcuture;
    
    worldPosition = getWorldPosition(strcuture);
             
    executionInterval = template.executionInterval;
    if(template.getCondition() != null) {      
      condition = template.getCondition().createInstance(world, strcuture, getSubState(state, "condition"));
    }
    if(template.getAction() != null) {      
      action = template.getAction().createInstance(world, strcuture, getSubState(state, "action"));
    }        
  }

  @Override
  public IBehaviour createInstance(World world, IStructure structure, NBTTagCompound state) {
    return new ServerTickBehaviour(this, world, structure, state);
  }
  
  @SubscribeEvent
  public void update(ServerTickEvent evt) {
    long curTime = world.getTotalWorldTime();
    if(executionInterval > 0 && curTime % executionInterval != 0) {
      return;
    }
    if(condition != null && !condition.isConditionMet(world, structure, worldPosition)) {
      return;
    }
    if(action != null) {      
      action.doAction(world, structure, worldPosition);
    }
  }

  @Override
  public NBTTagCompound getState() {
    NBTTagCompound state = new NBTTagCompound();
    boolean hasState = false;
    hasState |= addSubState(state, "condition", condition);
    hasState |= addSubState(state, "action", action);
    return hasState ? state : null;
  }

  public int getExecutionInterval() {
    return executionInterval;
  }

  public void setExecutionInterval(int executionInterval) {
    this.executionInterval = executionInterval;
  }

  public ICondition getCondition() {
    return condition;
  }

  public void setCondition(ICondition condition) {
    this.condition = condition;
  }

  public IAction getAction() {
    return action;
  }

  public void setAction(IAction action) {
    this.action = action;
  } 

}
