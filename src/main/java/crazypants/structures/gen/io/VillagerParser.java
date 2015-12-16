package crazypants.structures.gen.io;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import crazypants.structures.Log;
import crazypants.structures.api.gen.IVillagerGenerator;
import crazypants.structures.gen.villager.VillagerTemplate;

public class VillagerParser {

  public IVillagerGenerator parseVillagerGenerator(String uid, String json) throws Exception {
    VillagerTemplate tmpl = parseVillagerTemplate(uid, json);
    if(tmpl != null && tmpl.isValid()) {
      return tmpl.createGenerator();
    }
    return null;
  }
  
  
  
  public VillagerTemplate parseVillagerTemplate(String uid, String json) throws Exception {
    VillagerTemplate res = null;
    try {
      JsonObject root = new JsonParser().parse(json).getAsJsonObject();
      
      ResourceWrapper rw = GsonIO.INSTANCE.getGson().fromJson(root, ResourceWrapper.class);
      if(rw != null && rw.getVillagerTemplate() != null) {
        VillagerTemplate tmpl = rw.getVillagerTemplate();
        tmpl.setUid(uid);
        if(tmpl.isValid()) {
          res = tmpl;
        } else {
          Log.warn("Villager Generator " + uid + " was not valid");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return res;
  }
 
}
