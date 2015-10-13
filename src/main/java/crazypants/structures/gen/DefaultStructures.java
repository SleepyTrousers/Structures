package crazypants.structures.gen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import crazypants.IoUtil;
import crazypants.structures.Log;
import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.config.Config;
import crazypants.structures.gen.io.resource.IResourcePath;
import crazypants.structures.gen.io.resource.StructureResourceManager;

public class DefaultStructures {

  public static final File ROOT_DIR = new File(Config.configDirectory + "/structures/");
  public static final String RESOURCE_PATH = "/assets/enderstructures/structures/";

  public static final File TEST_DIR = new File(Config.configDirectory + "/test/");
  public static final String TEST_RESOURCE_PATH = "/assets/enderstructures/test/";

  public static void init() {

    StructureRegister reg = StructureRegister.instance;
    List<IResourcePath> toScan = new ArrayList<IResourcePath>();
    
    if(Config.testStructuresEnabled) {
      String name = "test" + StructureResourceManager.GENERATOR_EXT;
      copyTestFile(name, name + ".defaultValues");

      name = "test" + StructureResourceManager.TEMPLATE_EXT;
      copyTestFile(name, name + ".defaultValues");

      name = "test2" + StructureResourceManager.TEMPLATE_EXT;
      copyTestFile(name, name + ".defaultValues");
      
      toScan.add(reg.getResourceManager().addResourcePath(TEST_DIR));
      toScan.add(reg.getResourceManager().addClassLoaderResourcePath(TEST_RESOURCE_PATH));
    }    

    toScan.add(reg.getResourceManager().addResourcePath(ROOT_DIR));
    toScan.add(reg.getResourceManager().addClassLoaderResourcePath(RESOURCE_PATH));

    loadAndRegisterGenerators(toScan);

  }

  private static void loadAndRegisterGenerators(List<IResourcePath> resourcePaths) {

    for (IResourcePath path : resourcePaths) {
      List<String> gens = getGenerators(path.getChildren());
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
  
  private static List<String> getGenerators(List<String> kids) {
    if(kids == null || kids.isEmpty()) {
      return Collections.emptyList();
    }
    List<String> res = new ArrayList<String>();
    for (String kid : kids) {
      if(kid != null && kid.endsWith(".gen")) {
        String uid = kid.substring(0, kid.length() - 4);
        res.add(uid);
      }
    }
    return res;
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
