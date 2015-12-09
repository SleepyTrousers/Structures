package crazypants.structures.gen;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.utils.IOUtils;

import crazypants.structures.config.Config;
import crazypants.structures.gen.io.resource.ClassLoaderResourcePath;
import crazypants.structures.gen.io.resource.IResourcePath;

public class DefaultStructures {

  public static final File ROOT_DIR = new File(Config.configDirectory + "/structures/");
  public static final String RESOURCE_PATH = "/assets/enderstructures/structures/";

  public static final File TEST_DIR = new File(Config.configDirectory + "/test/");
  public static final String TEST_RESOURCE_PATH = "/assets/enderstructures/test/";

  public static void init() {

    StructureGenRegister reg = StructureGenRegister.instance;
    List<IResourcePath> toScan = new ArrayList<IResourcePath>();
    toScan.add(reg.getResourceManager().addResourceDirectory(ROOT_DIR));
    toScan.add(reg.getResourceManager().addClassLoaderResourcePath(RESOURCE_PATH));    
    registerZipFiles(ROOT_DIR, toScan);
    
    if(Config.testStructuresEnabled) {
      loadTestResources(reg, toScan);
    }

    for (IResourcePath path : toScan) {
      StructureGenRegister.instance.loadAndRegisterAllResources(path, true);
    }

  }

  public static void registerZipFiles(File rootDir, List<IResourcePath> toScan) {
    if(rootDir == null || !rootDir.exists() || !rootDir.isDirectory()) {
      return;
    }
    File[] kids = rootDir.listFiles();
    if(kids == null) {
      return;
    }
    for (File kid : kids) {
      if(kid.isFile() && kid.getName().endsWith(".zip")) {
        toScan.add(StructureGenRegister.instance.getResourceManager().addResourceZip(kid));
      }
    }
  }

  private static void loadTestResources(StructureGenRegister reg, List<IResourcePath> toScan) {
    if(!TEST_DIR.exists()) {
      TEST_DIR.mkdirs();
    }    
    ClassLoaderResourcePath path = new ClassLoaderResourcePath(TEST_RESOURCE_PATH);
    List<String> kids = path.getChildren();
    for(String kid : kids) {
      File targetFile = new File(TEST_DIR, kid);
      if(!targetFile.exists()) {              
        try {
          IOUtils.copy(path.getStream(kid),new FileOutputStream(targetFile));
        } catch (Exception e) {        
          e.printStackTrace();
        }        
      }
    }
    toScan.add(reg.getResourceManager().addResourceDirectory(TEST_DIR)); 
    
    registerZipFiles(TEST_DIR, toScan);
  }

}
