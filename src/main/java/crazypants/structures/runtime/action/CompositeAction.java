package crazypants.structures.runtime.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gson.annotations.Expose;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.IAction;
import crazypants.structures.api.util.Point3i;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class CompositeAction implements IAction {

  @Expose
  private List<IAction> actions = new ArrayList<IAction>();

  public void addAction(IAction condition) {
    if(condition != null) {
      actions.add(condition);
    }
  }

  public void removeAction(IAction condition) {
    if(condition != null) {
      actions.remove(condition);
    }
  }

  public Collection<IAction> getActions() {
    return actions;
  }

  @Override
  public NBTTagCompound getState() {
    NBTTagList childStates = new NBTTagList();
    for (IAction action : actions) {
      if(action != null) {
        NBTTagCompound conState = action.getState();
        if(conState != null) {
          childStates.appendTag(conState);
        }
      }
    }

    if(childStates.tagCount() <= 0) {
      return null;
    }
    NBTTagCompound res = new NBTTagCompound();
    res.setTag("childActions", childStates);
    return res;

  }

  @Override
  public IAction createInstance(World world, IStructure structure, NBTTagCompound state) {

    NBTTagList childStates;
    if(state != null && state.hasKey("childActions")) {
      childStates = (NBTTagList) state.getTag("childActions");
    } else {
      childStates = new NBTTagList();
    }

    CompositeAction res = doCreateInstance();
    int index = 0;
    for (IAction con : actions) {
      if(con != null) {
        NBTTagCompound childState = null;
        if(childStates.tagCount() > index) {
          childState = childStates.getCompoundTagAt(index);
        }
        res.addAction(con.createInstance(world, structure, childState));
        ++index;
      }
    }
    return res;
  }

  protected CompositeAction doCreateInstance() {
    return new CompositeAction();
  }

  @Override
  public void doAction(World world, IStructure structure, Point3i worldPos) {
    for (IAction action : actions) {
      action.doAction(world, structure, worldPos);
    }
  }

}
