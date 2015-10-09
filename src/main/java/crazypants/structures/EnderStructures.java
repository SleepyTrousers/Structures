package crazypants.structures;

import static crazypants.structures.EnderStructures.MODID;
import static crazypants.structures.EnderStructures.MOD_NAME;
import static crazypants.structures.EnderStructures.VERSION;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import crazypants.structures.config.Config;
import crazypants.structures.gen.DefaultStructures;
import crazypants.structures.gen.ReloadConfigCommand;
import crazypants.structures.gen.WorldGenerator;

@Mod(modid = MODID, name = MOD_NAME, version = VERSION, dependencies = "required-after:Forge@10.13.0.1150,)", guiFactory = "crazypants.structures.config.ConfigFactoryEnderStructures")
public class EnderStructures {

  public static final String MODID = "EnderStructures";
  public static final String MOD_NAME = "Ender Structures";
  public static final String VERSION = "@VERSION@";

  @Instance(MODID)
  public static EnderStructures instance;

  @SidedProxy(clientSide = "crazypants.structures.ClientProxy", serverSide = "crazypants.structures.CommonProxy")
  public static CommonProxy proxy;

  public static WorldGenerator structureGenerator;
  
  public static StructureRuntime structureRuntime;

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    Config.load(event);
    structureRuntime = StructureRuntime.create();
    structureGenerator = WorldGenerator.create();    
  }


  @EventHandler
  public void serverStopped(FMLServerStoppedEvent event) {
    structureGenerator.serverStopped(event);
    structureRuntime.serverStopped(event);
  }

  @EventHandler
  public void serverLoad(FMLServerStartingEvent event) {
    event.registerServerCommand(new ReloadConfigCommand());
  }

  @EventHandler
  public void load(FMLInitializationEvent event) {
    instance = this;
    proxy.load();
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {   
    addRecipes();    
    DefaultStructures.registerStructures();
  }

  private void addRecipes() {  
  }

}
