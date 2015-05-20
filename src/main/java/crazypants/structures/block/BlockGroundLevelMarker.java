package crazypants.structures.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.structures.EnderStructures;
import crazypants.structures.EnderStructuresTab;

public class BlockGroundLevelMarker extends Block {

  public static final String NAME = "blockGroundLevelMarker";

  public static BlockGroundLevelMarker create() {
    BlockGroundLevelMarker res = new BlockGroundLevelMarker();
    res.init();
    return res;
  }

  protected BlockGroundLevelMarker() {
    super(Material.rock);
    setHardness(0.5F);
    setBlockName(NAME);
    setStepSound(Block.soundTypeStone);
    setHarvestLevel("pickaxe", 0);
    setCreativeTab(EnderStructuresTab.tabEnderStructures);
    setLightOpacity(0);
    setBlockTextureName(EnderStructures.MODID.toLowerCase() + ":" + NAME);
  }

  protected void init() {
    GameRegistry.registerBlock(this, NAME);
  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public void registerBlockIcons(IIconRegister iIconRegister) {
//    blockIcon = iIconRegister.registerIcon(EnderStructures.MODID.toLowerCase() + ":" + NAME);
//  }

}
