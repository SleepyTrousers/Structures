package crazypants.structures.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.ICondition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class AndCondition implements ICondition {

  private List<ICondition> conditions = new ArrayList<ICondition>();
  
  public void addCondition(ICondition condition) {
    if(condition != null) {
      conditions.add(condition);
    }
  }
  
  public void removeCondition(ICondition condition) {
    if(condition != null) {
      conditions.remove(condition);
    }
  }
  
  public Collection<ICondition> getConditions() {
    return conditions;
  }
  
  @Override
  public NBTTagCompound getState() {    
    NBTTagList childStates = new NBTTagList();
    for(ICondition con : conditions) {
      NBTTagCompound conState = con.getState();
      if(conState != null) {
        childStates.appendTag(conState);
      }
    }
    
    if(childStates.tagCount() <= 0) {
      return null;
    }    
    NBTTagCompound res = new NBTTagCompound();
    res.setTag("childStates", childStates);
    return res;
    
  }
  
  @Override
  public ICondition createInstance(World world, IStructure structure, NBTTagCompound state) {
    
    NBTTagList childStates; 
    if(state != null && state.hasKey("childStates")) {
      childStates = (NBTTagList)state.getTag("childStates");
    } else {
      childStates = new NBTTagList();
    }
    
    AndCondition res = new AndCondition();
    int index = 0;
    for(ICondition con : conditions) {
      NBTTagCompound childState = null;
      if(childStates.tagCount() > index) {
        childState = childStates.getCompoundTagAt(index);
      }
      res.addCondition(con.createInstance(world, structure, childState));
      
      ++index;
    }
    return res;
  }

  @Override
  public boolean isConditionMet(World world, IStructure structure) {
    for(ICondition con : conditions) {
      if(!con.isConditionMet(world, structure)) {
        return false;
      }
    }
    return true;
  }

  

}
