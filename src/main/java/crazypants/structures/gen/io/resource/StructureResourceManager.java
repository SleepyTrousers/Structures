package crazypants.structures.gen.io.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import crazypants.IoUtil;
import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.gen.IVillagerGenerator;
import crazypants.structures.gen.StructureGenRegister;
import crazypants.structures.gen.io.GeneratorParser;
import crazypants.structures.gen.io.LootCategeoriesParser;
import crazypants.structures.gen.io.LootCategories;
import crazypants.structures.gen.io.TemplateParser;
import crazypants.structures.gen.io.VillagerParser;
import crazypants.structures.gen.structure.StructureComponentNBT;

public class StructureResourceManager {

  public static final String GENERATOR_EXT = ".gen";
  public static final String COMPONENT_EXT = ".nbt";
  public static final String TEMPLATE_EXT = ".stp";
  public static final String VILLAGER_EXT = ".vgen";
  public static final String LOOT_EXT = ".loot";

  private final List<IResourcePath> resourcePaths = new ArrayList<IResourcePath>();
  private final GeneratorParser generatorParsor = new GeneratorParser();
  private final TemplateParser templateParser = new TemplateParser();
  private final VillagerParser villagerParser = new VillagerParser();  
  private final StructureGenRegister register;

  public StructureResourceManager(StructureGenRegister register) {
    this.register = register;
  }

  public IResourcePath addResourceDirectory(File dir) {
    if(dir == null) {
      return null;
    }
    IResourcePath res = new DirectoryResourcePath(dir);
    if(!resourcePaths.contains(res)) {    
      resourcePaths.add(res);
    }
    return res;
  }

  public IResourcePath addResourceZip(File zipDile) {
    if(zipDile == null) {
      return null;
    }
    IResourcePath res = new ZipResourcePath(zipDile);
    resourcePaths.add(res);
    return res;
  }

  public IResourcePath addClassLoaderResourcePath(String resourcePath) {
    if(resourcePath == null) {
      return null;
    }
    IResourcePath res = new ClassLoaderResourcePath(resourcePath);
    resourcePaths.add(res);
    return res;
  }

  public boolean resourceExists(String resource) {
    for (IResourcePath rp : resourcePaths) {
      if(rp.exists(resource)) {
        return true;
      }
    }
    return false;
  }

  public List<File> getFilesWithExt(String ext) {
    List<File> res = new ArrayList<File>();
    for (IResourcePath rp : resourcePaths) {
      if(rp instanceof DirectoryResourcePath) {
        File dir = ((DirectoryResourcePath)rp).getDirectory(); 
        List<String> uids = rp.getChildUids(ext);
        if(uids != null) {
          for(String uid : uids) {
            res.add(new File(dir, uid + ext));
          }
        }
      }
    }
    return res;
  }

  public InputStream getStream(String resourceName) {
    for (IResourcePath rp : resourcePaths) {
      InputStream is = rp.getStream(resourceName);
      if(is != null) {
        return is;
      }
    }
    return null;
  }

  public IStructureGenerator loadGenerator(String uid) throws Exception {
    return loadGenerator(uid, loadText(uid, GENERATOR_EXT));
  }
  
  public IStructureGenerator loadGenerator(String uid, String text) throws Exception {
    return generatorParsor.parseGeneratorConfig(register, uid, text);
  }
  
  public IStructureGenerator loadGenerator(String uid, InputStream stream) throws Exception {   
    return loadGenerator(uid, loadText(uid, stream));
  }

  public IVillagerGenerator loadVillager(String uid) throws Exception {
    return villagerParser.parseVillagerGenerator(uid, loadText(uid, VILLAGER_EXT));
  }

  public IStructureTemplate loadTemplate(String uid) throws Exception {
    return loadTemplate(uid, loadText(uid, TEMPLATE_EXT));
  }

  public IStructureTemplate loadTemplate(String uid, InputStream fromStream) throws Exception {
    return loadTemplate(uid, loadText(uid, fromStream));
  }

  public IStructureTemplate loadTemplate(String uid, String text) throws Exception {
    return templateParser.parseTemplateConfig(register, uid, text);
  }

  public LootCategories loadLootCategories(String uid) throws Exception {    
    return LootCategeoriesParser.parseLootCategories(uid, loadText(uid, LOOT_EXT));
  }

  public StructureComponentNBT loadStructureComponent(String uid) throws IOException {
    InputStream stream = null;
    try {
      stream = getStream(uid, COMPONENT_EXT);
      if(stream == null) {
        throw new IOException("StructureResourceManager: Could find resources for template: " + uid);
      }
      return new StructureComponentNBT(uid, stream);
    } finally {
      IOUtils.closeQuietly(stream);
    }
  }

  private String loadText(String uid, String ext) throws IOException {
    return loadText(uid, getStream(uid, ext));
  }

  public String loadText(String uid, InputStream str) throws IOException {
    if(str == null) {
      throw new IOException("Could not find the resource for generator: " + uid);
    }
    String res = IoUtil.readStream(str);
    boolean stripComments = true;
    if(!stripComments) {
      return res;
    }
    StringBuffer buf = new StringBuffer();
    BufferedReader br = new BufferedReader(new StringReader(res));
    String line = br.readLine();
    while (line != null) {
      line = line.trim();
      if(!line.startsWith("#")) {
        buf.append(line + "\n");
      }
      line = br.readLine();
    }
    return buf.toString();
  }

  private InputStream getStream(String uid, String extension) {
    if(uid == null || extension == null) {
      return null;
    }
    if(uid.endsWith(extension)) {
      return getStream(uid);
    }
    return getStream(uid + extension);
  }

}
