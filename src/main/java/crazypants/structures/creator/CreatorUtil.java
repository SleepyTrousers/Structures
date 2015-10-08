package crazypants.structures.creator;

import crazypants.structures.gen.structure.StructureBlock;
import crazypants.structures.gen.structure.StructureComponent;
import crazypants.vec.Point3i;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;

public class CreatorUtil {

  
  public static StructureComponent createComponent(String name, IBlockAccess world, AxisAlignedBB worldBnds, int surfaceOffset) {
    
    AxisAlignedBB bb = worldBnds.getOffsetBoundingBox(-worldBnds.minX, -worldBnds.minY, -worldBnds.minZ);

    Point3i size = new Point3i((int) Math.abs(worldBnds.maxX - worldBnds.minX), (int) Math.abs(worldBnds.maxY - worldBnds.minY), (int) Math.abs(worldBnds.maxZ
        - worldBnds.minZ));

    //this.surfaceOffset = surfaceOffset;

    boolean markBiomeFillerForMerge = true;
    StructureBlock fb = null;
    StructureBlock sufb = null;
    if(markBiomeFillerForMerge) {
      BiomeGenBase biome = world.getBiomeGenForCoords((int) worldBnds.minX, (int) worldBnds.minZ);
      if(biome != null) {
        if(biome.topBlock != null) {
          sufb = new StructureBlock(biome.fillerBlock);
        }
        if(biome.fillerBlock != null) {
          fb = new StructureBlock(biome.fillerBlock);
          if(sufb == null) {
            sufb = fb;
          }
        }
      }
    }
    StructureComponent res = new StructureComponent(bb, size, name,  surfaceOffset, fb, sufb);
    
    int invNo = 0;
    int x;
    int y;
    int z;
    for (short xIndex = 0; xIndex < size.x; xIndex++) {
      for (short yIndex = 0; yIndex < size.y; yIndex++) {
        for (short zIndex = 0; zIndex < size.z; zIndex++) {
          x = (int) worldBnds.minX + xIndex;
          y = (int) worldBnds.minY + yIndex;
          z = (int) worldBnds.minZ + zIndex;
          Block blk = world.getBlock(x, y, z);
          StructureBlock sb = new StructureBlock(blk, world.getBlockMetadata(x, y, z), world.getTileEntity(x, y, z));
          if(!sb.isAir()) {
            //Only store air blocks if they must be cleared
            if(blk == EnderStructuresCreator.blockClearMarker) {
              sb = new StructureBlock(Blocks.air);
            }            
            res.addBlock(sb, xIndex, yIndex, zIndex);
            if(sb.getTileEntity() != null) {
              TileEntity te = TileEntity.createAndLoadEntity(sb.getTileEntity());
              if(te instanceof IInventory) {
                res.addTagForLocation("inv" + invNo, new Point3i(xIndex, yIndex, zIndex));
                invNo++;
              }
            }
          }
        }
      }
    }    
    return res;
  }
  
  public static Point3i findBlockOnAxis(Block blk, IBlockAccess world, int x, int y, int z, short scanDistance, Point3i axis, boolean bothDirs) {
    int steps = 0;

    Point3i tst = new Point3i(x, y, z);
    do {
      tst.add(axis);
      if(world.getBlock(tst.x, tst.y, tst.z) == blk) {
        return tst;
      }
      steps++;
    } while (steps < scanDistance);

    if(bothDirs) {
      axis.scale(-1);
      return findBlockOnAxis(blk, world, x, y, z, scanDistance, axis, false);
    }
    return null;
  }
  
}
