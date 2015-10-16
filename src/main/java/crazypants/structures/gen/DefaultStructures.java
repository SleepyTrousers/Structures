package crazypants.structures.gen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import crazypants.IoUtil;
import crazypants.structures.Log;
import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.api.gen.IVillagerGenerator;
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

      name = "esRuinTest" + StructureResourceManager.TEMPLATE_EXT;
      copyTestFile(name, name + ".defaultValues");

      name = "esSmallHouseTest" + StructureResourceManager.TEMPLATE_EXT;
      copyTestFile(name, name + ".defaultValues");
      
      name = "testVillager" + StructureResourceManager.VILLAGER_EXT;
      copyTestFile(name, name + ".defaultValues");

      toScan.add(reg.getResourceManager().addResourceDirectory(TEST_DIR));
      toScan.add(reg.getResourceManager().addClassLoaderResourcePath(TEST_RESOURCE_PATH));
    }

    toScan.add(reg.getResourceManager().addResourceDirectory(ROOT_DIR));
    toScan.add(reg.getResourceManager().addClassLoaderResourcePath(RESOURCE_PATH));

    loadAndRegister(toScan);

  }

  private static void loadAndRegister(List<IResourcePath> resourcePaths) {

    for (IResourcePath path : resourcePaths) {
      List<String> gens = path.getChildren(StructureResourceManager.GENERATOR_EXT);
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

    for (IResourcePath path : resourcePaths) {
      List<String> uids = path.getChildren(StructureResourceManager.VILLAGER_EXT);
      for (String uid : uids) {
        try {
          IVillagerGenerator gen = StructureRegister.instance.getResourceManager().loadVillager(uid);
          if(gen != null) {
            StructureRegister.instance.registerVillagerGenerator(gen);            
          }
        } catch (Exception e) {
          Log.warn("StructureResourceManager.loadGenerators: Could not load generator: " + uid + " error: " + e);
        }
      }
    }
    
    for (IResourcePath path : resourcePaths) {
      List<String> uids = path.getChildren(StructureResourceManager.LOOT_EXT);
      for (String uid : uids) {
        try {
          StructureRegister.instance.getResourceManager().loadLootTableDefination(uid);          
        } catch (Exception e) {
          Log.warn("StructureResourceManager.loadGenerators: Could not load loot table categories from: " + uid + " error: " + e);
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
