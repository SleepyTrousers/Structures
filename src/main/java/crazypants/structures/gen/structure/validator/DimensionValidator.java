package crazypants.structures.gen.structure.validator;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import crazypants.structures.gen.WorldStructures;
import crazypants.structures.gen.structure.StructureGenerator;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class DimensionValidator implements IChunkValidator {

  private Set<String> includes = new HashSet<String>();
  private Set<String> excludes = new HashSet<String>();
  
  public DimensionValidator() {    
  }
  
  public void addDimension(String name, boolean isExclude) {
    if(name == null) {
      return;
    }
    if(isExclude) {
      excludes.add(name);
    } else {
      includes.add(name);
    }
  }
  
  public void addAll(List<String> dimensions, boolean isExclude) {
    if(dimensions == null) {
      return;      
    }
    for(String dim : dimensions) {
      addDimension(dim, isExclude);
    }    
  }
  
  @Override
  public boolean isValidChunk(StructureGenerator template, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {    
    String bName = world.getBiomeGenForCoords(new BlockPos(chunkX << 4 + 8, 64, chunkZ << 4 + 8)).biomeName;
    if(!includes.isEmpty() && !includes.contains(bName)) {
      return false;
    }    
    return !excludes.contains(bName);
  }

}
