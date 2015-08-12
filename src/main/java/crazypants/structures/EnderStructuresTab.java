package crazypants.structures;

import static crazypants.structures.EnderStructures.MODID;
import static crazypants.structures.EnderStructures.MOD_NAME;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnderStructuresTab extends CreativeTabs {

  public static final CreativeTabs tabEnderStructures = new EnderStructuresTab();

  public EnderStructuresTab() {
    super(MODID);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public String getTabLabel() {
    return MODID;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public String getTranslatedTabLabel() {
    return MOD_NAME;
  }

  @Override
  public Item getTabIconItem() {
    return EnderStructures.itemComponentTool;
  }

}
