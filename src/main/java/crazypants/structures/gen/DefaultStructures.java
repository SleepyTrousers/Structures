package crazypants.structures.gen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import crazypants.IoUtil;
import crazypants.structures.Log;
import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.config.Config;
import crazypants.structures.gen.io.StructureResourceManager;
import crazypants.structures.gen.io.StructureResourceManager.ResourcePath;

public class DefaultStructures {

  public static final File ROOT_DIR = new File(Config.configDirectory + "/structures/");
  public static final String RESOURCE_PATH = "/assets/enderstructures/structures/";

  public static final File TEST_DIR = new File(Config.configDirectory + "/test/");
  public static final String TEST_RESOURCE_PATH = "/assets/enderstructures/test/";

  public static void init() {

    StructureRegister reg = StructureRegister.instance;
    List<ResourcePath> toScan = new ArrayList<StructureResourceManager.ResourcePath>();
    
    if(Config.testStructuresEnabled) {
      String name = "test" + StructureResourceManager.GENERATOR_EXT;
      copyTestFile(name, name + ".defaultValues");

      name = "test" + StructureResourceManager.TEMPLATE_EXT;
      copyTestFile(name, name + ".defaultValues");

      name = "test2" + StructureResourceManager.TEMPLATE_EXT;
      copyTestFile(name, name + ".defaultValues");
      
      toScan.add(reg.getResourceManager().addResourcePath(TEST_DIR));
      toScan.add(reg.getResourceManager().addResourcePath(TEST_RESOURCE_PATH));
    }    

    toScan.add(reg.getResourceManager().addResourcePath(ROOT_DIR));
    toScan.add(reg.getResourceManager().addResourcePath(RESOURCE_PATH));

    loadAndRegisterGenerators(toScan);

  }

  public static void loadAndRegisterGenerators(List<ResourcePath> resourcePaths) {

    for (ResourcePath path : resourcePaths) {
      List<String> gens = path.getGenerators();
      for (String uid : gens) {
        try {
          IStructureGenerator gen = StructureRegister.instance.getResourceManager().loadGenerator(uid);
          if(gen != null) {
            StructureRegister.instance.registerGenerator(gen);
          }
        } catch (Exception e) {
          Log.warn("StructureResourceManager.loadGenerators: Could not load generator: " + uid + " error: " + e);
        }
      }
    }

  }

  private static void copyTestFile(String resourceName, String fileName) {
    try {
      IoUtil.copyTextTo(new File(TEST_DIR, fileName), DefaultStructures.class.getResourceAsStream(TEST_RESOURCE_PATH + resourceName));
    } catch (IOException e) {
      Log.warn(
          "EnderZooStructures: Could not copy " + TEST_RESOURCE_PATH + resourceName + " from jar to " + TEST_DIR.getAbsolutePath() + fileName + " Ex:" + e);
    }
  }

}
