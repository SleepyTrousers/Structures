package crazypants.structures;

import static crazypants.structures.EnderStructures.MODID;
import static crazypants.structures.EnderStructures.MOD_NAME;
import static crazypants.structures.EnderStructures.VERSION;

import crazypants.structures.block.BlockClearMarker;
import crazypants.structures.block.BlockGroundLevelMarker;
import crazypants.structures.block.BlockStructureMarker;
import crazypants.structures.config.Config;
import crazypants.structures.gen.DefaultStructures;
import crazypants.structures.gen.ReloadConfigCommand;
import crazypants.structures.gen.WorldGenerator;
import crazypants.structures.item.ExportManager;
import crazypants.structures.item.ItemClearTool;
import crazypants.structures.item.ItemComponentTool;
import crazypants.structures.item.ItemTemplateTool;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

@Mod(modid = MODID, name = MOD_NAME, version = VERSION, dependencies = "required-after:Forge@[11.14.1.1341,)", guiFactory = "crazypants.structures.config.ConfigFactoryEnderStructures")
public class EnderStructures {

  public static final String MODID = "EnderStructures";
  public static final String MOD_NAME = "Ender Structures";
  public static final String VERSION = "@VERSION@";

  @Instance(MODID)
  public static EnderStructures instance;

  @SidedProxy(clientSide = "crazypants.structures.ClientProxy", serverSide = "crazypants.structures.CommonProxy")
  public static CommonProxy proxy;

  public static BlockStructureMarker blockStructureMarker;
  public static BlockClearMarker blockClearMarker;
  public static BlockGroundLevelMarker blockGroundLevelMarker;
  
  public static ItemComponentTool itemComponentTool;
  public static ItemTemplateTool itemTemplateTool;
  public static ItemClearTool itemClearTool;
  public static WorldGenerator structureManager;  

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {

    Config.load(event);   

    blockStructureMarker = BlockStructureMarker.create();
    blockGroundLevelMarker = BlockGroundLevelMarker.create();
    blockClearMarker = BlockClearMarker.create();
    
    itemComponentTool = ItemComponentTool.create();
    itemTemplateTool = ItemTemplateTool.create();
    itemClearTool = ItemClearTool.create();
    structureManager = WorldGenerator.create();
  }


  @EventHandler
  public void serverStopped(FMLServerStoppedEvent event) {
    structureManager.serverStopped(event);
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
    ExportManager.instance.loadExportFolder();

  }

  private void addRecipes() {  
  }

}
