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
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

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
  public StructureBoundingBox getEffectedBounds(IStructure structure) {
    StructureBoundingBox res = border.getBounds(structure);    
    if(!clearBellowGround) {
      res.minY += structure.getSurfaceOffset() + 1;
    }
    return res;
  }

  @Override
  public boolean prepareLocation(IStructure structure, World world, Random random, StructureBoundingBox clip) {

    StructureBoundingBox bb = getEffectedBounds(structure);
    for (int x = bb.minX; x < bb.maxX; x++) {
      for (int y = bb.minY; y < bb.maxY; y++) {
        for (int z = bb.minZ; z < bb.maxZ; z++) {
          if((clip == null || VecUtil.isInBounds(clip, x, z)) && (clearPlants || !StructureUtil.isPlant(world.getBlockState(new BlockPos(x, y, z)).getBlock(), world, x, y, z))) {
            if(!world.isAirBlock(new BlockPos(x, y, z))) {
              world.setBlockToAir(new BlockPos(x, y, z));
            }
          }
        }
      }
    }

    if(clearItems) {            
      AxisAlignedBB aabb = new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
      List<EntityItem> ents = world.getEntitiesWithinAABB(EntityItem.class, aabb);
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
