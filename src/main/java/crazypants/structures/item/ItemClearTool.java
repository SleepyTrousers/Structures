package crazypants.structures.item;

import crazypants.structures.EnderStructures;
import crazypants.structures.EnderStructuresTab;
import crazypants.vec.Point3i;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemClearTool extends Item {

  private static final String NAME = "clearTool";

  public static ItemClearTool create() {
    ItemClearTool res = new ItemClearTool();
    res.init();
    return res;
  }

  private ItemClearTool() {
    setUnlocalizedName(NAME);
    setCreativeTab(EnderStructuresTab.tabEnderStructures);
    setHasSubtypes(false);
  }

  private void init() {
    GameRegistry.registerItem(this, NAME);
  }

  @Override
  public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World world, BlockPos pos, EnumFacing dir, float hitX, float hitY, float hitZ) {
    if(world.isRemote) {
      return true;
    }
    
    Point3i bc = new Point3i(pos, dir);
    int filled = floodFill(world, bc, 0);
    System.out.println("ItemClearTool.onItemUse: filled " + filled + " with blocks of air");
    return true;
  }

  private int floodFill(World world, Point3i bc, int filled) {
    if(!world.isAirBlock(bc.pos())) {
      return filled;
    }
    if(filled >= 100) {
      return filled;
    }

    world.setBlockState(bc.pos(), EnderStructures.blockClearMarker.getDefaultState());
    filled++;
    if(filled >= 100) {
      return filled;
    }

    for (EnumFacing dir : EnumFacing.values()) {
      if(dir != EnumFacing.UP) {
        Point3i next = new Point3i(bc.x + dir.getFrontOffsetX(), bc.y + dir.getFrontOffsetY(), bc.z + dir.getFrontOffsetZ());
        filled = floodFill(world, next, filled);
      }
    }
    return filled;

  }

}
