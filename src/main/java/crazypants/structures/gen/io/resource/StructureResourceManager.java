package crazypants.structures.gen.io.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import crazypants.IoUtil;
import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.gen.StructureRegister;
import crazypants.structures.gen.io.GeneratorParser;
import crazypants.structures.gen.structure.StructureComponentNBT;
import crazypants.structures.gen.structure.StructureTemplate;

public class StructureResourceManager {

  public static final String GENERATOR_EXT = ".gen";
  public static final String COMPONENT_EXT = ".nbt";
  public static final String TEMPLATE_EXT = ".stp";

  private final List<IResourcePath> resourcePaths = new ArrayList<IResourcePath>();
  private final GeneratorParser parser = new GeneratorParser();
  private final StructureRegister register;

  public StructureResourceManager(StructureRegister register) {
    this.register = register;
  }

  public IResourcePath addResourcePath(File dir) {
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

  public IStructureGenerator loadGenerator(String uid) throws Exception {
    return parseJsonGenerator(loadGeneratorText(uid));
  }

  public IStructureGenerator loadGenerator(File fromFile) throws Exception {
    return parseJsonGenerator(loadText(fromFile));
  }

  public IStructureGenerator parseJsonGenerator(String json) throws Exception {
    return parser.parseGeneratorConfig(register, json);
  }

  public String loadText(File fromFile) throws IOException {
    return IoUtil.readStream(new FileInputStream(fromFile));
  }

  public String loadGeneratorText(String uid) throws IOException {
    InputStream str = getStreamForGenerator(uid);
    if(str == null) {
      throw new IOException("Could not find the resource for generator: " + uid);
    }
    return IoUtil.readStream(str);
  }

  public StructureComponentNBT loadStructureComponent(String uid) throws IOException {
    InputStream stream = null;
    try {
      stream = getStreamForComponent(uid);
      if(stream == null) {
        throw new IOException("StructureResourceManager: Could find resources for template: " + uid);
      }
      return new StructureComponentNBT(stream);
    } finally {
      IOUtils.closeQuietly(stream);
    }
  }

  private String loadTemplateText(String uid) throws IOException {
    InputStream str = getStreamForTemplate(uid);
    if(str == null) {
      throw new IOException("Could not find the resource for template: " + uid);
    }
    return IoUtil.readStream(str);
  }

  public StructureTemplate loadTemplate(String uid) throws Exception {
    return parseJsonTemplate(loadTemplateText(uid));
  }

  public IStructureTemplate loadTemplate(File fromFile) throws Exception {
    return parseJsonTemplate(loadText(fromFile));
  }

  public StructureTemplate parseJsonTemplate(String json) throws Exception {
    return parser.parseTemplateConfig(register, json);
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

  private InputStream getStreamForGenerator(String uid) {
    if(uid.endsWith(GENERATOR_EXT)) {
      return getStream(uid);
    }
    return getStream(uid + GENERATOR_EXT);
  }

  private InputStream getStreamForComponent(String uid) {
    if(uid.endsWith(COMPONENT_EXT)) {
      return getStream(uid.substring(0, uid.length() - COMPONENT_EXT.length()));
    }
    return getStream(uid + COMPONENT_EXT);
  }

  private InputStream getStreamForTemplate(String uid) {
    return getStream(uid + TEMPLATE_EXT);
  }

}
