package crazypants.structures.gen.io.resource;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

import crazypants.structures.config.Config;
import crazypants.structures.gen.StructureGenRegister;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ResourceModContainer extends cpw.mods.fml.common.DummyModContainer {

  public static final String MODID = "esresource";
  
  public static ResourceModContainer create() {
    ModMetadata md = new ModMetadata();
    md.modId = MODID;
    md.name = "EnderStructures Resource Loader";
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
  
  
  @SideOnly(Side.CLIENT)
  public static class ConfigFolderResourcePack implements IResourcePack {

    public ConfigFolderResourcePack(ModContainer mc) {    
    }
    
    @Override
    public InputStream getInputStream(ResourceLocation rl) throws IOException {
      return StructureGenRegister.instance.getResourceManager().getStream(rl.getResourcePath());
    }

    @Override
    public boolean resourceExists(ResourceLocation rl) {
      if(!ResourceModContainer.MODID.equals(rl.getResourceDomain())) {
        return false;
      }    
      return StructureGenRegister.instance.getResourceManager().resourceExists(rl.getResourcePath());   
    }
    
    @Override
    public Set<?> getResourceDomains() {
      return Collections.singleton(ResourceModContainer.MODID);
    }

    @Override
    public IMetadataSection getPackMetadata(IMetadataSerializer p_135058_1_, String p_135058_2_) throws IOException {    
      return null;
    }

    @Override
    public BufferedImage getPackImage() throws IOException {
      return null;
    }

    @Override
    public String getPackName() {
      return "EnderStructure Config Resources";
    }

  }

  

}
