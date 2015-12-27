package crazypants.structures.api.gen;

import java.util.Collection;
import java.util.Random;

import com.google.gson.annotations.Expose;

public class WeightedTemplate implements Comparable<WeightedTemplate> {

  private static final Random RND = new Random();
  
  @Expose
  private int weight;

  @Expose
  private IStructureTemplate template;

  public WeightedTemplate() {
    weight = 1;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public IStructureTemplate getTemplate() {
    return template;
  }

  public void setTemplate(IStructureTemplate template) {
    this.template = template;
  }

  public static IStructureTemplate getTemplate(Collection<WeightedTemplate> candidates) {
    if(candidates == null || candidates.isEmpty()) {
      return null;
    }

    int totalWeight = 0;
    for (WeightedTemplate wt : candidates) {
      totalWeight += wt.getWeight();
    }

    int curWeight = 0;
    int target = RND.nextInt(totalWeight);
    IStructureTemplate res = null;
    for (WeightedTemplate wt : candidates) {
      res = wt.getTemplate();
      if(curWeight >= target) {        
        break;
      }
      curWeight += wt.getWeight();
    }    
    return res;

  }

  @Override
  public int compareTo(WeightedTemplate o) {
    if(o == null) {
      return -1;
    }
    return Integer.compare(weight, o.weight);
  }

}
