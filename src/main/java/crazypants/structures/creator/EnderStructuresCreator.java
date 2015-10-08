package crazypants.structures.creator;

import static crazypants.structures.creator.EnderStructuresCreator.MODID;
import static crazypants.structures.creator.EnderStructuresCreator.MOD_NAME;
import static crazypants.structures.creator.EnderStructuresCreator.VERSION;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import crazypants.structures.creator.block.BlockClearMarker;
import crazypants.structures.creator.block.BlockGroundLevelMarker;
import crazypants.structures.creator.block.BlockStructureMarker;
import crazypants.structures.creator.item.ExportManager;
import crazypants.structures.creator.item.ItemClearTool;
import crazypants.structures.creator.item.ItemComponentTool;
import crazypants.structures.creator.item.ItemTemplateTool;

@Mod(modid = MODID, name = MOD_NAME, version = VERSION, dependencies = "required-after:Forge@10.13.0.1150,)")
public class EnderStructuresCreator {

  public static final String MODID = "EnderStructuresCreator";
  public static final String MOD_NAME = "Ender Structures Creator";
  public static final String VERSION = "@VERSION@";
  
  @Instance(MODID)
  public static EnderStructuresCreator instance;

//  @SidedProxy(clientSide = "crazypants.structures.ClientProxy", serverSide = "crazypants.structures.CommonProxy")
//  public static CommonProxy proxy;
  
  public static BlockStructureMarker blockStructureMarker;
  public static BlockClearMarker blockClearMarker;
  public static BlockGroundLevelMarker blockGroundLevelMarker;
  
  public static ItemComponentTool itemComponentTool;
  public static ItemTemplateTool itemTemplateTool;
  public static ItemClearTool itemClearTool;

  
  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {

    //Config.load(event);   
    blockStructureMarker = BlockStructureMarker.create();
    blockGroundLevelMarker = BlockGroundLevelMarker.create();
    blockClearMarker = BlockClearMarker.create();
    
    itemComponentTool = ItemComponentTool.create();
    itemTemplateTool = ItemTemplateTool.create();
    itemClearTool = ItemClearTool.create();
    
  }
  
  @EventHandler
  public void load(FMLInitializationEvent event) {
    instance = this;    
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {
   
    addRecipes();
        
    ExportManager.instance.loadExportFolder();
  }
  
  private void addRecipes() {  
  }

  
}
