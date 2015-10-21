package crazypants.structures.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.ICondition;
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
  public ICondition createPerStructureInstance(World world, IStructure structure) {
    AndCondition res = new AndCondition();
    for(ICondition con : conditions) {
      res.addCondition(con.createPerStructureInstance(world, structure));
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
