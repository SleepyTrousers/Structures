package crazypants.structures.block;

import crazypants.structures.EnderStructuresTab;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
    setUnlocalizedName(NAME);
    setStepSound(Block.soundTypeStone);
    setHarvestLevel("pickaxe", 0);
    setCreativeTab(EnderStructuresTab.tabEnderStructures);
    setLightOpacity(0);    
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
