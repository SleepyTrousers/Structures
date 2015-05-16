package crazypants.structures.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import crazypants.structures.EnderStructures;
import crazypants.structures.config.Config.Section;

public class GuiConfigFactoryEnderStructures extends GuiConfig {

  public GuiConfigFactoryEnderStructures(GuiScreen parentScreen) {
    super(parentScreen, getConfigElements(parentScreen), EnderStructures.MODID, false, false, StatCollector.translateToLocal("enderstructures.config.title"));
  }

  @SuppressWarnings("rawtypes")
  private static List<IConfigElement> getConfigElements(GuiScreen parent) {
    List<IConfigElement> list = new ArrayList<IConfigElement>();
    String prefix = "enderstructures.config.";

    for (Section section : Config.sections) {
      list.add(new ConfigElement<ConfigCategory>(Config.config.getCategory(section.lc()).setLanguageKey(prefix + section.lang)));
    }

    return list;
  }

}
