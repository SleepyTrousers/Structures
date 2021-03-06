package crazypants.structures;

import static crazypants.structures.EnderStructures.MODID;
import static crazypants.structures.EnderStructures.MOD_NAME;
import static crazypants.structures.EnderStructures.VERSION;

import java.io.File;

import com.google.common.collect.ImmutableList;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import crazypants.structures.api.API;
import crazypants.structures.api.ITyped;
import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.api.gen.IVillagerGenerator;
import crazypants.structures.config.Config;
import crazypants.structures.gen.DefaultStructures;
import crazypants.structures.gen.ReloadConfigCommand;
import crazypants.structures.gen.StructureGenRegister;
import crazypants.structures.gen.WorldGenerator;
import crazypants.structures.gen.io.DefaultTypes;
import crazypants.structures.gen.io.GsonParserAdapter;
import crazypants.structures.gen.io.ParserRegister;
import crazypants.structures.gen.structure.TypeRegister;
import crazypants.structures.runtime.StructureRegister;

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

  public static StructureRegister structureRegister;

  //SuperFlat Overworld village spawn seed: 3002013171567938552
  
  //TODO:
  /*
   * PotentialSpawns extends WorldEvent
   * BiomeColor extends BiomeEvent
   * EntityStruckByLightningEvent
   */
  
  
  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    Config.load(event);
    structureRegister = StructureRegister.create();
    structureGenerator = WorldGenerator.create();    
  }

  @EventHandler
  public void load(FMLInitializationEvent event) {
    instance = this;
    proxy.load();    
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {
    addRecipes();
    DefaultTypes.register();
    DefaultStructures.init();    
  }

  @EventHandler
  public void loadComplete(FMLLoadCompleteEvent event) {
    processImc(FMLInterModComms.fetchRuntimeMessages(this)); //Some mods send IMCs during PostInit, so we catch them here.
  }

  @EventHandler
  public void serverStopped(FMLServerStoppedEvent event) {
    structureGenerator.serverStopped(event);
    structureRegister.serverStopped(event);
  }

  @EventHandler
  public void serverLoad(FMLServerStartingEvent event) {
    event.registerServerCommand(new ReloadConfigCommand());
  }
  
  @EventHandler
  public void onImc(IMCEvent evt) {
    processImc(evt.getMessages());
  }

  private void processImc(ImmutableList<IMCMessage> messages) {
    //TODO: Villager gens, loot tables and custom types 
    StructureGenRegister reg = StructureGenRegister.instance;
    for (IMCMessage msg : messages) {
      String key = msg.key;
      try {
        if(msg.isStringMessage()) {

          if(API.ADD_RESOURCE_DIR.equalsIgnoreCase(key)) {

            reg.getResourceManager().addResourceDirectory(new File(msg.getStringValue()));

          } else if(API.ADD_RESOURCE_PATH.equalsIgnoreCase(key)) {

            reg.getResourceManager().addClassLoaderResourcePath(msg.getStringValue());

          } else if(API.REGISTER_GENERATOR.equalsIgnoreCase(key)) {
            IStructureGenerator gen = StructureGenRegister.instance.getResourceManager().loadGenerator(msg.getStringValue());
            if(gen != null) {
              reg.registerGenerator(gen);
            }
          } else if(API.REGISTER_VILLAGE_GENERATOR.equalsIgnoreCase(key)) {
            IVillagerGenerator vil = StructureGenRegister.instance.getResourceManager().loadVillager(msg.getStringValue());
            if(vil != null) {
              reg.registerVillagerGenerator(vil);
            }
          } else if(API.REGISTER_TYPE.equalsIgnoreCase(key)) {
            ITyped inst = (ITyped)Class.forName(msg.getStringValue()).newInstance();
            TypeRegister.INSTANCE.register(inst);    
            ParserRegister.instance.register(new GsonParserAdapter(inst));
          }             
        } else if(msg.isNBTMessage()) {

        }
      } catch (Exception e) {
      }
    }
  }

  private void addRecipes() {
  }

}
