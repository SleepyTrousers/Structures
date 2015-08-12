package crazypants.structures.config;

import java.util.ArrayList;
import java.util.List;

import crazypants.structures.EnderStructures;
import crazypants.structures.config.Config.Section;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GuiConfigFactoryEnderStructures extends GuiConfig {

  public GuiConfigFactoryEnderStructures(GuiScreen parentScreen) {
    super(parentScreen, getConfigElements(parentScreen), EnderStructures.MODID, false, false, StatCollector.translateToLocal("enderstructures.config.title"));
  }

  
  private static List<IConfigElement> getConfigElements(GuiScreen parent) {
    List<IConfigElement> list = new ArrayList<IConfigElement>();
    String prefix = "enderstructures.config.";

    for (Section section : Config.sections) {
      list.add(new ConfigElement(Config.config.getCategory(section.lc()).setLanguageKey(prefix + section.lang)));
    }

    return list;
  }

}
