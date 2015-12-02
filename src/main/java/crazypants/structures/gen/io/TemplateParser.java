package crazypants.structures.gen.io;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import crazypants.structures.gen.StructureGenRegister;
import crazypants.structures.gen.structure.StructureTemplate;

public class TemplateParser {

  public StructureTemplate parseTemplateConfig(StructureGenRegister reg, String uid, String json) throws Exception {

    StructureTemplate res = null;
    try {
      JsonObject to = new JsonParser().parse(json).getAsJsonObject();
      to = to.getAsJsonObject("StructureTemplate");

      reg.getResourceManager().getLootTableParser().parseLootTableCategories(to);

      res = GsonIO.INSTANCE.getGson().fromJson(to, StructureTemplate.class);
      res.setUid(uid);
    } catch (Exception e) {
      e.printStackTrace();
    }

//    if(res != null) {
//      json = GsonIO.INSTANCE.getGson().toJson(res);
//      System.out.println("TemplateParser.parseTemplateConfig: -----------------------------------------------------------------------------");
//      System.out.println(json);
//    }
    
    return res;
  }

}
