package crazypants.structures.gen.structure.validator;

import java.util.Random;

import crazypants.structures.gen.WorldStructures;
import crazypants.structures.gen.structure.StructureGenerator;
import crazypants.structures.gen.structure.validator.biome.IBiomeFilter;
import crazypants.vec.VecUtil;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class BiomeValidator implements IChunkValidator {

  private final IBiomeFilter filter;
  
  public BiomeValidator(IBiomeFilter filter) {
    this.filter = filter;
  }

  @Override
  public boolean isValidChunk(StructureGenerator template, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {                
    BiomeGenBase bgb = world.getBiomeGenForCoords(VecUtil.getCenterOfChunk(chunkX, chunkZ));            
    return filter.isMatchingBiome(bgb);
  }

}
