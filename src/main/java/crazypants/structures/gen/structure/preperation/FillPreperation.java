package crazypants.structures.gen.structure.preperation;

import java.util.Random;

import crazypants.structures.gen.ChunkBounds;
import crazypants.structures.gen.StructureUtil;
import crazypants.structures.gen.structure.Border;
import crazypants.structures.gen.structure.Structure;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class FillPreperation implements ISitePreperation {

  private boolean useBiomeFillerBlock = true;
  private boolean clearPlants = true;

  private Border border = new Border();

  public FillPreperation() {
    border.setBorderXZ(1);
  }

  @Override
  public boolean prepareLocation(Structure structure, World world, Random random, ChunkBounds clip) {
    IBlockState fill = null;
    IBlockState surf = null;
    if(useBiomeFillerBlock) {
      BlockPos bp = new BlockPos(structure.getOrigin().x, 64, structure.getOrigin().z);
      fill = world.getBiomeGenForCoords(bp).fillerBlock;      
      surf = world.getBiomeGenForCoords(bp).topBlock;      
    }
    if(fill == null) {
      fill = Blocks.cobblestone.getDefaultState();
    }
    if(surf == null) {
      surf = fill;
    }

//        fill = Blocks.glass;
//        surf = Blocks.glass;
    //    surfaceMeta = 4;

    AxisAlignedBB bb = structure.getBounds();

    int minX = (int) bb.minX;
    int maxX = (int) bb.maxX;
    int minZ = (int) bb.minZ;
    int maxZ = (int) bb.maxZ;

    
    int minY = 0;
    int maxY = (int) bb.minY + structure.getSurfaceOffset();

    IBlockState curBlk;
//    int curMeta;
    for (int x = minX - border.get(EnumFacing.WEST); x < maxX + border.get(EnumFacing.EAST); x++) {
      for (int z = minZ - border.get(EnumFacing.NORTH); z < maxZ + border.get(EnumFacing.SOUTH); z++) {

        int startY = maxY;
        if(x < minX || x >= maxX || z < minZ || z >= maxZ) {
          //border, so need to make it back to ground level 
//          startY = maxY + structure.getTemplate().getSurfaceOffset();
        }
        for (int y = startY; y > minY; y--) {
          if(clip == null || clip.isBlockInBounds(x, z)) {
            if(StructureUtil.isIgnoredAsSurface(world, x, z, y, world.getBlockState(new BlockPos(x, y, z)), true, true)) {
              if(y >= maxY && world.isAirBlock(new BlockPos(x, y + 1, z))) {
                curBlk = surf;               
              } else {
                curBlk = fill;
              }                            
              world.setBlockState(new BlockPos(x, y, z), curBlk, 2);
            } else {
              y = 0; //done for the x,z
            }
          }
        }
      }
    }
    return true;
  }

  public boolean isClearPlants() {
    return clearPlants;
  }

  public void setClearPlants(boolean clearPlants) {
    this.clearPlants = clearPlants;
  }

  public Border getBorder() {
    return border;
  }

  public void setBorder(Border border) {
    this.border = border;
  }

}
