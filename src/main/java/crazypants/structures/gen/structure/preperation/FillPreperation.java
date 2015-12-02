package crazypants.structures.gen.structure.preperation;

import java.util.Random;

import com.google.gson.annotations.Expose;

import crazypants.structures.AbstractTyped;
import crazypants.structures.api.gen.ISitePreperation;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.util.StructureUtil;
import crazypants.structures.api.util.VecUtil;
import crazypants.structures.gen.structure.Border;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.util.ForgeDirection;

public class FillPreperation extends AbstractTyped implements ISitePreperation {

  @Expose
  private Block fillBlock;
  
  @Expose
  private int fillMeta = 0;

  @Expose
  private Block surfaceBlock;
  
  @Expose
  private int surfaceMeta = 0;

  @Expose
  private boolean useBiomeFillerBlock = true;
  
  @Expose
  private boolean clearPlants = true;

  @Expose
  private Border border = new Border();

  public FillPreperation() {
    super("FillPreperation");
    border.setBorderXZ(1);
  }
  
  @Override
  public StructureBoundingBox getEffectedBounds(IStructure structure) {
    return border.getBounds(structure);
  }

  @Override
  public boolean prepareLocation(IStructure structure, World world, Random random, StructureBoundingBox clip) {
    Block fill = fillBlock;
    Block surf = surfaceBlock;
    if(useBiomeFillerBlock) {
      fill = world.getBiomeGenForCoords(structure.getOrigin().x, structure.getOrigin().z).fillerBlock;
      surf = world.getBiomeGenForCoords(structure.getOrigin().x, structure.getOrigin().z).topBlock;
    }
    if(fill == null) {
      fill = Blocks.cobblestone;
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

    Block curBlk;
    int curMeta;
    for (int x = minX - border.get(ForgeDirection.WEST); x < maxX + border.get(ForgeDirection.EAST); x++) {
      for (int z = minZ - border.get(ForgeDirection.NORTH); z < maxZ + border.get(ForgeDirection.SOUTH); z++) {

        int startY = maxY;
        if(x < minX || x >= maxX || z < minZ || z >= maxZ) {
          //border, so need to make it back to ground level 
//          startY = maxY + structure.getTemplate().getSurfaceOffset();
        }
        for (int y = startY; y > minY; y--) {
          if(clip == null || VecUtil.isInBounds(clip, x, z)) {
            if(StructureUtil.isIgnoredAsSurface(world, x, z, y, world.getBlock(x, y, z), true, true)) {
              if(y >= maxY && world.isAirBlock(x, y + 1, z)) {
                curBlk = surf;
                curMeta = surfaceMeta;
              } else {
                curBlk = fill;
                curMeta = fillMeta;
              }
              world.setBlock(x, y, z, curBlk, curMeta, 2);
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
