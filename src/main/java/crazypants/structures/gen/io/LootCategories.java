package crazypants.structures.gen.io;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import crazypants.structures.api.ListElementType;
import crazypants.structures.api.gen.IResource;

public class LootCategories implements IResource {

  @ListElementType(elementType = LootCategory.class)
  @Expose
  private List<LootCategory> categories;

  private String uid;

  public LootCategories() {
    categories = new ArrayList<LootCategory>();
  }

  public List<LootCategory> getCategories() {
    return categories;
  }

  public void setCategories(List<LootCategory> curCategories) {
    this.categories = curCategories;
  }

  @Override
  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }
  
  public void register() {
    System.out.println("LootCategories.register: UID = " + uid);
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

}
