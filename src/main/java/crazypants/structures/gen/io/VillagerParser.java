package crazypants.structures.gen.io;

import crazypants.structures.Log;
import crazypants.structures.api.gen.IVillagerGenerator;
import crazypants.structures.gen.structure.loot.LootCategories;
import crazypants.structures.gen.villager.VillagerTemplate;

public class VillagerParser {

  private VillagerParser() {    
  }
  
  public static IVillagerGenerator parseVillagerGenerator(String uid, String json) throws Exception {
    VillagerTemplate tmpl = parseVillagerTemplate(uid, json);
    if(tmpl != null && tmpl.isValid()) {
      return tmpl.createGenerator();
    }
    return null;
  }

  public static VillagerTemplate parseVillagerTemplate(String uid, String json) throws Exception {
    ResourceWrapper rw = GsonIO.INSTANCE.getGson().fromJson(json, ResourceWrapper.class);
    if(rw != null && rw.getVillagerTemplate() != null) {
      VillagerTemplate tmpl = rw.getVillagerTemplate();
      tmpl.setUid(uid);
      LootCategories lc = tmpl.getLootCategories();
      if(lc != null) {
        lc.setUid(uid);
      }      
      if(tmpl.isValid()) {
        return tmpl;
      } else {
        Log.warn("Villager Generator " + uid + " was not valid");
      }
    }
    throw new Exception("VillagerParser: No valid template found in " + uid);
  }

}
