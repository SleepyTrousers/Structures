package crazypants.structures.gen.io.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import crazypants.IoUtil;
import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.gen.IVillagerGenerator;
import crazypants.structures.gen.StructureRegister;
import crazypants.structures.gen.io.GeneratorParser;
import crazypants.structures.gen.io.LootTableParser;
import crazypants.structures.gen.io.TemplateParser;
import crazypants.structures.gen.structure.StructureComponentNBT;
import crazypants.structures.gen.villager.VillagerParser;

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
  private final LootTableParser chestGenParser = new LootTableParser();
  private final StructureRegister register;

  public StructureResourceManager(StructureRegister register) {
    this.register = register;
  }

  public IResourcePath addResourceDirectory(File dir) {
    if(dir == null) {
      return null;
    }
    IResourcePath res = new DirectoryResourcePath(dir);
    resourcePaths.add(res);
    return res;
  }

  public IResourcePath addClassLoaderResourcePath(String resourcePath) {
    IResourcePath res = new ClassLoaderReourcePath(resourcePath);
    resourcePaths.add(res);
    return res;
  }

  public LootTableParser getLootTableParser() {
    return chestGenParser;
  }

  public boolean resourceExists(String resource) {
    for (IResourcePath rp : resourcePaths) {
      if(rp.exists(resource)) {
        return true;
      }
    }
    return false;
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
    return generatorParsor.parseGeneratorConfig(register, uid, loadText(uid, GENERATOR_EXT));
  }

  public IVillagerGenerator loadVillager(String uid) throws Exception {
    return villagerParser.parseVillagerConfig(register, uid, loadText(uid, VILLAGER_EXT));
  }

  public IStructureTemplate loadTemplate(String uid) throws Exception {
    return templateParser.parseTemplateConfig(register, uid, loadText(uid, TEMPLATE_EXT));
  }

  public void loadLootTableDefination(String uid) throws Exception {
    chestGenParser.parseLootTableCategories(uid, loadText(uid, LOOT_EXT));
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

  private String loadText(String uid, InputStream str) throws IOException {
    if(str == null) {
      throw new IOException("Could not find the resource for generator: " + uid);
    }
    return IoUtil.readStream(str);
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
