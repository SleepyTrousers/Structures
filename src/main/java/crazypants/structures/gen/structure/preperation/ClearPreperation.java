package crazypants.structures.gen.structure.preperation;

import java.util.Random;

import crazypants.structures.gen.ChunkBounds;
import crazypants.structures.gen.StructureUtil;
import crazypants.structures.gen.structure.Border;
import crazypants.structures.gen.structure.Structure;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ClearPreperation implements ISitePreperation {

  private Border border = new Border();

  private boolean clearPlants = true;

  private boolean clearBellowGround = false;  

  public ClearPreperation() {
    border.setBorder(1, 1, 1, 1, 3, 0);
  }

  @Override
  public boolean prepareLocation(Structure structure, World world, Random random, ChunkBounds clip) {

    AxisAlignedBB bb = structure.getBounds();
    int minX = (int) bb.minX - border.get(EnumFacing.WEST);
    int maxX = (int) bb.maxX + border.get(EnumFacing.EAST);
    int minY = (int) bb.minY - border.get(EnumFacing.DOWN);
    int maxY = (int) bb.maxY + border.get(EnumFacing.UP);
    int minZ = (int) bb.minZ - border.get(EnumFacing.NORTH);
    int maxZ = (int) bb.maxZ + border.get(EnumFacing.SOUTH);

    if(!clearBellowGround) {
      minY += structure.getSurfaceOffset() + 1;
    }
    
    for (int x = minX; x < maxX; x++) {
      for (int y = minY; y < maxY; y++) {
        for (int z = minZ; z < maxZ; z++) {
          if( (clip == null || clip.isBlockInBounds(x, z)) && (clearPlants || !StructureUtil.isPlant(world.getBlockState(new BlockPos(x, y, z)).getBlock(), world, x, y, z))) {
            if(!world.isAirBlock(new BlockPos(x, y, z))) {
              world.setBlockToAir(new BlockPos(x, y, z));
            }
          }
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
