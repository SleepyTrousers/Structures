package crazypants.structures.gen.structure.sampler;

import java.util.Random;

import crazypants.structures.api.gen.ILocationSampler;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.gen.IWorldStructures;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.StructureUtil;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class SurfaceLocationSampler implements ILocationSampler {

  private static final Random rnd = new Random();

  private int distanceFromSurface = 0;
  private boolean canPlaceInFluid = false;

  public SurfaceLocationSampler() {
  }

  public int getDistanceFromSurface() {
    return distanceFromSurface;
  }

  public void setDistanceFromSurface(int distanceFromSurface) {
    this.distanceFromSurface = distanceFromSurface;
  }

  public boolean isCanPlaceInFluid() {
    return canPlaceInFluid;
  }

  public void setCanGenerateOnFluid(boolean canPlaceInFluid) {
    this.canPlaceInFluid = canPlaceInFluid;
  }

  @Override
  public Point3i generateCandidateLocation(IStructure structure, IWorldStructures structures, Random random,
      int chunkX, int chunkZ) {

    return findStartPos(structure, chunkX, chunkZ, structures.getWorld());
  }

  protected Point3i findStartPos(IStructure structure, int chunkX, int chunkZ, World world) {     
    Point3i candidate;
    if(structure.canSpanChunks()) {
      candidate = getRandomBlock(world, chunkX, chunkZ, 16, 16, distanceFromSurface - structure.getSurfaceOffset(), structure.getSize().y);
    } else {
      candidate = getRandomBlock(world, chunkX, chunkZ, 16 - structure.getSize().x, 16 - structure.getSize().z, distanceFromSurface - structure.getSurfaceOffset(), structure.getSize().y);
    }
    return candidate;
  }

  protected Point3i getRandomBlock(World world, int chunkX, int chunkZ, int maxOffsetX, int maxOffsetZ, int distanceFromSurface, int requiredVerticalSpace) {
    int x = chunkX * 16 + (maxOffsetX > 0 ? rnd.nextInt(maxOffsetX) : 0);
    int z = chunkZ * 16 + (maxOffsetZ > 0 ? rnd.nextInt(maxOffsetZ) : 0);

    //Find the surface y
    Block blk;
    Point3i loc = new Point3i();

    blk = StructureUtil.getSurfaceBlock(world, x, z, loc, true, !canPlaceInFluid);
    if(blk != world.getBiomeGenForCoords(x, z).topBlock && blk != world.getBiomeGenForCoords(x, z).fillerBlock) {
      return null;
    }

    //Correct for distance from surface
    loc.y += distanceFromSurface;    
    if(loc.y > 0 && loc.y < 256 + requiredVerticalSpace) {
      return loc;
    }
    return null;
  }

}
