package crazypants.structures.gen.io;

import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.gen.StructureGenRegister;
import crazypants.structures.gen.structure.StructureTemplate;
import crazypants.structures.gen.structure.loot.LootCategories;

public class TemplateParser {

  private TemplateParser() {    
  }
  
  public static IStructureTemplate parseTemplateConfig(StructureGenRegister reg, String uid, String json) throws Exception {
    ResourceWrapper rw = GsonIO.INSTANCE.getGson().fromJson(json, ResourceWrapper.class);
    if(rw != null) {
      StructureTemplate res = rw.getStructureTemplate();
      if(res != null) {
        res.setUid(uid);
        LootCategories lc = res.getLootCategories();
        if(lc != null) {
          lc.setUid(uid);
        }
        if(res.isValid()) {
          return res;
        }
      }
    }
    throw new Exception("TemplateParser: No valid template found in " + uid);
  }

}
