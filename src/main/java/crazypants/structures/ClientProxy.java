package crazypants.structures;

import cpw.mods.fml.client.FMLClientHandler;
import crazypants.structures.gen.io.resource.ResourceModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ClientProxy extends CommonProxy {

  @Override
  public World getClientWorld() {
    return FMLClientHandler.instance().getClient().theWorld;
  }

  @Override
  public EntityPlayer getClientPlayer() {
    return Minecraft.getMinecraft().thePlayer;
  }

  @Override
  public void load() {
    FMLClientHandler.instance().addModAsResource(ResourceModContainer.create());
  }

}
