package crazypants.structures.gen.structure.validator;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.gson.annotations.Expose;

import crazypants.structures.AbstractTyped;
import crazypants.structures.api.gen.IChunkValidator;
import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.api.gen.IWorldStructures;
import net.minecraft.world.World;

public class DimensionValidator extends AbstractTyped implements IChunkValidator {

  @Expose
  private Set<String> includes = new HashSet<String>();
  
  @Expose
  private Set<String> excludes = new HashSet<String>();
  
  public DimensionValidator() {
    super("DimensionValidator");
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
  public boolean isValidChunk(IStructureGenerator template, IWorldStructures structures, World world, Random random, int chunkX, int chunkZ) {
    String dName = world.provider.getDimensionName();
    if(!includes.isEmpty() && !includes.contains(dName)) {
      return false;
    }    
    return !excludes.contains(dName);
  }

}
