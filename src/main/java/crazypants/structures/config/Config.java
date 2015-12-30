package crazypants.structures.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.structures.EnderStructures;
import crazypants.structures.Log;
import net.minecraftforge.common.config.Configuration;

public final class Config {

  public static class Section {
    public final String name;
    public final String lang;

    public Section(String name, String lang) {
      this.name = name;
      this.lang = lang;
      register();
    }

    private void register() {
      sections.add(this);
    }

    public String lc() {
      return name.toLowerCase();
    }
  }

  public static final List<Section> sections;

  static {
    sections = new ArrayList<Section>();
  }

  public static Configuration config;

  public static File configDirectory;
  
  public static final String CONFIG_RESOURCE_PATH = "/assets/enderstructures/config/";
  
  public static final Section sectionTest = new Section("Test Settings", "test");
  public static boolean testStructuresEnabled = false;
  

  public static final Section sectionStructures = new Section("Structure Settings", "structures");
  public static double spacingMultiplier = 1.0;
  public static double chanceMultiplier = 1.0;  
  public static String[] disabledStructures = new String[0];
  public static String[] disabledVillages = new String[0];

  public static void load(FMLPreInitializationEvent event) {

    FMLCommonHandler.instance().bus().register(new Config());
    configDirectory = new File(event.getModConfigurationDirectory(), EnderStructures.MODID.toLowerCase());
    if(!configDirectory.exists()) {
      configDirectory.mkdir();
    }

    File configFile = new File(configDirectory, "EnderStructures.cfg");
    config = new Configuration(configFile);
    syncConfig();
  }

  public static void syncConfig() {
    try {
      Config.processConfig(config);
    } catch (Exception e) {
      Log.error("EnderStructures has a problem loading it's configuration");
      e.printStackTrace();
    } finally {
      if(config.hasChanged()) {
        config.save();
      }
    }
  }

  @SubscribeEvent
  public void onConfigChanged(OnConfigChangedEvent event) {
    if(event.modID.equals(EnderStructures.MODID)) {
      Log.info("Updating config...");
      syncConfig();
    }
  }

  public static void processConfig(Configuration config) {   
    testStructuresEnabled = config.getBoolean("testStructuresEnabled", sectionTest.name, testStructuresEnabled, "When enabled structures used for testing will be generated.");
        
    spacingMultiplier = config.get(sectionStructures.name, "spacingMultiplier", spacingMultiplier, "Global multiplier for the minimum spacing between structures").getDouble(spacingMultiplier);
    chanceMultiplier = config.get(sectionStructures.name, "chanceMultiplier", chanceMultiplier, "Global multiplier for the chance a structure will be generated in a chunk").getDouble(chanceMultiplier);
    disabledStructures = config.get(sectionStructures.name, "disabledStructures", disabledStructures, "List of disabled structures. Entries correspond to the .gen file to be disabled. Extension is optional (ie adding foo or foo.gen has the same effect").getStringList();
    disabledVillages = config.get(sectionStructures.name, "disabledVillages", disabledVillages, "List of disabled villages. Entries correspond to the .vgen file to be disabled.").getStringList();
  }

  private Config() {
  }

}
