package crazypants.structures.creator.block;

import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.structures.creator.EnderStructuresCreator;
import crazypants.structures.creator.EnderStructuresCreatorTab;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

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
    setCreativeTab(EnderStructuresCreatorTab.tabEnderStructures);
    setLightOpacity(0);
    setBlockTextureName(EnderStructuresCreator.MODID.toLowerCase() + ":" + NAME);
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
