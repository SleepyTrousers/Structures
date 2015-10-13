package crazypants.structures.gen.io.resource;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.structures.config.Config;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class ConfigFolderResourcePack implements IResourcePack {

  public ConfigFolderResourcePack(ModContainer mc) {    
  }
  
  @Override
  public InputStream getInputStream(ResourceLocation rl) throws IOException {
    return new FileInputStream(new File(Config.configDirectory, rl.getResourcePath()));
  }

  @Override
  public boolean resourceExists(ResourceLocation rl) {
    if(!ResourceModContainer.MODID.equals(rl.getResourceDomain())) {
      return false;
    }
    File f = new File(Config.configDirectory, rl.getResourcePath());
    if(f.exists()) {
      return true;
    }
    return false;    
  }

  @Override
  public Set getResourceDomains() {
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
