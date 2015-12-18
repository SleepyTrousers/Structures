package crazypants.structures.gen.io;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import crazypants.structures.api.ListElementType;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

public class LootCategory {

  @Expose
  private String category;
  
  @Expose
  private int minItems;
  
  @Expose
  private int maxItems;
  
  @ListElementType(elementType=LootEntry.class)
  @Expose
  private List<LootEntry> entries;
  
  public LootCategory() {
    entries = new ArrayList<LootEntry>();
    category = null;
    minItems = 1;
    maxItems = 1;
  }

  public void register() {
    ChestGenHooks cat = ChestGenHooks.getInfo(category);
    cat.setMin(minItems);
    cat.setMax(maxItems);
    if(entries != null) {
      for(LootEntry entry : entries) {
        if(entry != null) {
          WeightedRandomChestContent content = entry.createContent();
          if(content != null) {
            cat.addItem(content);
          }
        }
      }
    }    
  }  
  
  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public int getMinItems() {
    return minItems;
  }

  public void setMinItems(int minItems) {
    this.minItems = minItems;
  }

  public int getMaxItems() {
    return maxItems;
  }

  public void setMaxItems(int maxItems) {
    this.maxItems = maxItems;
  }

  public List<LootEntry> getEntries() {
    return entries;
  }

  public void setEntries(List<LootEntry> entries) {
    this.entries = entries;
  }
  
}
