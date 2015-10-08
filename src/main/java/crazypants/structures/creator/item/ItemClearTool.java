package crazypants.structures.creator.item;

import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.structures.creator.EnderStructuresCreator;
import crazypants.structures.creator.EnderStructuresCreatorTab;
import crazypants.vec.Point3i;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemClearTool extends Item {

  private static final String NAME = "itemClearTool";

  public static ItemClearTool create() {
    ItemClearTool res = new ItemClearTool();
    res.init();
    return res;
  }

  private ItemClearTool() {
    setUnlocalizedName(NAME);
    setCreativeTab(EnderStructuresCreatorTab.tabEnderStructures);
    setTextureName(EnderStructuresCreator.MODID.toLowerCase() + ":" + NAME);
    setHasSubtypes(false);
  }

  private void init() {
    GameRegistry.registerItem(this, NAME);
  }

  @Override
  public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

    if (world.isRemote) {
      return true;
    }

    ForgeDirection dir = ForgeDirection.getOrientation(side);
    Point3i bc = new Point3i(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
    int filled = floodFill(world, bc, 0);
    System.out.println("ItemClearTool.onItemUse: filled " + filled + " with blocks of air");
    return true;
  }

  private int floodFill(World world, Point3i bc, int filled) {
    if (!world.isAirBlock(bc.x, bc.y, bc.z)) {
      return filled;
    }
    if (filled >= 100) {
      return filled;
    }
    
    world.setBlock(bc.x, bc.y, bc.z, EnderStructuresCreator.blockClearMarker);
    filled++;
    if (filled >= 100) {
      return filled;
    }

    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      if (dir != ForgeDirection.UP) {
        Point3i next = new Point3i(bc.x + dir.offsetX, bc.y + dir.offsetY, bc.z + dir.offsetZ);
        filled = floodFill(world, next, filled);
      }
    }
    return filled;
  }

}
