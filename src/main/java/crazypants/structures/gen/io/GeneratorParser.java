package crazypants.structures.gen.io;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.gen.StructureGenRegister;
import crazypants.structures.gen.structure.StructureGenerator;

public class GeneratorParser {

  public GeneratorParser() {
  }

  public IStructureGenerator parseGeneratorConfig(StructureGenRegister reg, String uid, String json) throws Exception {
    
    
    StructureGenerator res = null;
    try {
      JsonObject root = new JsonParser().parse(json).getAsJsonObject();
      JsonObject to = root.getAsJsonObject("structureGenerator");      
      if(to != null) {
        reg.getResourceManager().getLootTableParser().parseLootTableCategories(to);
        res = GsonIO.INSTANCE.getGson().fromJson(to, StructureGenerator.class);
        res.setUid(uid);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    if(res == null || !res.isValid()) {
      throw new Exception("GeneratorParser: Could not create a valid generator for " + res);
    }
    
    return res;
    


  }

}
