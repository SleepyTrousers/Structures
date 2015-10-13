package crazypants.structures.gen.io.resource;

import java.io.File;

import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.structures.config.Config;

@SideOnly(Side.CLIENT)
public class ResourceModContainer extends cpw.mods.fml.common.DummyModContainer {

  public static final String MODID = "esdummy";
  
  public static ResourceModContainer create() {
    ModMetadata md = new ModMetadata();
    md.modId = MODID;
    md.name = "EnderStructures Dummy Mod";
    return new ResourceModContainer(md);
  }
  
  public ResourceModContainer(ModMetadata md) {
    super(md);
  }
  
  @Override
  public Class<?> getCustomResourcePackClass() {
    return ConfigFolderResourcePack.class;
  }

  @Override
  public File getSource() {
    return Config.configDirectory;
  }
  
  

}
