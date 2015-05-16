package crazypants.structures.gen;

import java.io.File;
import java.io.IOException;

import crazypants.IoUtil;
import crazypants.structures.Log;
import crazypants.structures.config.Config;
import crazypants.structures.gen.io.StructureResourceManager;

public class DefaultStructures {

  public static final File ROOT_DIR = new File(Config.configDirectory + "/structures/");
  public static final String RESOURCE_PATH = "/assets/enderstructures/config/structures/";

  public static void registerStructures() {
    StructureRegister reg = StructureRegister.instance;
    reg.getResourceManager().addResourcePath(ROOT_DIR);
    reg.getResourceManager().addResourcePath(RESOURCE_PATH);

    String fileName = "README.txt";
    copyText(fileName, fileName);

    register("test");
  }

  private static void copyText(String resourceName, String fileName) {
    try {
      IoUtil.copyTextTo(new File(ROOT_DIR, fileName), DefaultStructures.class.getResourceAsStream(RESOURCE_PATH + resourceName));
    } catch (IOException e) {
      Log.warn("EnderZooStructures: Could not copy " + RESOURCE_PATH + resourceName + " from jar to " + ROOT_DIR.getAbsolutePath() + fileName + " Ex:" + e);
    }
  }

  private static void register(String uid) {
    
      String name = uid + StructureResourceManager.GENERATOR_EXT;
      copyText(name, name + ".defaultValues");

      StructureRegister reg = StructureRegister.instance;
      try {
        reg.registerGenerator(reg.getResourceManager().loadGenerator(uid));
      } catch (Exception e) {
        Log.error("EnderZooStructures: Could not load structure template " + uid + StructureResourceManager.GENERATOR_EXT);
        e.printStackTrace();
      }
    

  }

}
