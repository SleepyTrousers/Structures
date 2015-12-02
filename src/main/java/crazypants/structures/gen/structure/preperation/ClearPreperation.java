package crazypants.structures.gen.structure.preperation;

import java.util.List;
import java.util.Random;

import com.google.gson.annotations.Expose;

import crazypants.structures.AbstractTyped;
import crazypants.structures.api.gen.ISitePreperation;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.util.StructureUtil;
import crazypants.structures.api.util.VecUtil;
import crazypants.structures.gen.structure.Border;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.util.ForgeDirection;

public class ClearPreperation extends AbstractTyped implements ISitePreperation {

  @Expose
  private Border border = new Border();

  @Expose
  private boolean clearPlants = true;

  @Expose
  private boolean clearBellowGround = false;

  @Expose
  private boolean clearItems = true;

  public ClearPreperation() {
    super("ClearPreperation");
    border.setBorder(1, 1, 1, 1, 3, 0);
  }

  @Override
  public boolean prepareLocation(IStructure structure, World world, Random random, StructureBoundingBox clip) {

    AxisAlignedBB bb = structure.getBounds();
    int minX = (int) bb.minX - border.get(ForgeDirection.WEST);
    int maxX = (int) bb.maxX + border.get(ForgeDirection.EAST);
    int minY = (int) bb.minY - border.get(ForgeDirection.DOWN);
    int maxY = (int) bb.maxY + border.get(ForgeDirection.UP);
    int minZ = (int) bb.minZ - border.get(ForgeDirection.NORTH);
    int maxZ = (int) bb.maxZ + border.get(ForgeDirection.SOUTH);

    if(!clearBellowGround) {
      minY += structure.getSurfaceOffset() + 1;
    }

    for (int x = minX; x < maxX; x++) {
      for (int y = minY; y < maxY; y++) {
        for (int z = minZ; z < maxZ; z++) {
          if((clip == null || VecUtil.isInBounds(clip, x, z)) && (clearPlants || !StructureUtil.isPlant(world.getBlock(x, y, z), world, x, y, z))) {
            if(!world.isAirBlock(x, y, z)) {
              world.setBlockToAir(x, y, z);
            }
          }
        }
      }
    }

    if(clearItems) {
      @SuppressWarnings("unchecked")
      List<EntityItem> ents = world.getEntitiesWithinAABB(EntityItem.class, bb);
      if(ents != null) {
        for (EntityItem item : ents) {
          item.setDead();
        }
      }
    }
    return true;
  }

  public Border getBorder() {
    return border;
  }

  public void setBorder(Border border) {
    this.border = border;
  }

  public boolean isClearPlants() {
    return clearPlants;
  }

  public void setClearPlants(boolean clearPlants) {
    this.clearPlants = clearPlants;
  }

  public boolean getClearBellowGround() {
    return clearBellowGround;
  }

  public void setClearBellowGround(boolean clearBellowGround) {
    this.clearBellowGround = clearBellowGround;
  }

}
