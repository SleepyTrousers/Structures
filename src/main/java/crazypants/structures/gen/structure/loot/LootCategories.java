package crazypants.structures.gen.structure.loot;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import crazypants.structures.Log;
import crazypants.structures.api.ListElementType;

public class LootCategories {

  @ListElementType(elementType = LootCategory.class)
  @Expose
  private List<LootCategory> categories;

  private String uid;

  public LootCategories() {
    categories = new ArrayList<LootCategory>();
  }
  
  public LootCategories(LootCategories other) {
    this();
    if(other.categories != null) {
      for(LootCategory cat : other.categories) {
        if(cat != null) {
          categories.add(new LootCategory(cat));
        }
      }
    }
  }

  public List<LootCategory> getCategories() {
    return categories;
  }

  public void setCategories(List<LootCategory> curCategories) {
    this.categories = curCategories;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }
  
  public void register() {
    Log.info("LootCategories.register: Registered loot categories in: " + uid);
    if(categories == null) {
      return;
    }
    for(LootCategory cat : categories) {
      if(cat != null) {
//        System.out.println("LootCategories.register:    - Category " + cat.getCategory());
        cat.register();
      }
    }   
  }
  
  public void deregister() {    
    if(categories == null) {
      return;
    }
    for(LootCategory cat : categories) {
      if(cat != null) {
        cat.deregister();
      }
    }   
  }

}
