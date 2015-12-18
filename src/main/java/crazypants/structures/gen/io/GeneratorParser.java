package crazypants.structures.gen.io;

import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.gen.StructureGenRegister;
import crazypants.structures.gen.structure.StructureGenerator;

public class GeneratorParser {

  private GeneratorParser() {
  }

  public static IStructureGenerator parseGeneratorConfig(StructureGenRegister reg, String uid, String json) throws Exception {
    ResourceWrapper rw = GsonIO.INSTANCE.getGson().fromJson(json, ResourceWrapper.class);
    if(rw != null) {
      StructureGenerator res = rw.getStructureGenerator();
      if(res != null) {
        res.setUid(uid);
        if(res.isValid()) {
          return res;
        }
      }
    }
    throw new Exception("GeneratorParser: No valid generator found in " + uid);
  }

}
