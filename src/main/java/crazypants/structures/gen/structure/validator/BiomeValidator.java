package crazypants.structures.gen.structure.validator;

import java.util.Random;

import crazypants.structures.api.gen.IChunkValidator;
import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.api.gen.IWorldStructures;
import crazypants.structures.gen.structure.validator.biome.IBiomeFilter;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class BiomeValidator implements IChunkValidator {

  private final IBiomeFilter filter;
  
  public BiomeValidator(IBiomeFilter filter) {
    this.filter = filter;
  }

  @Override
  public boolean isValidChunk(IStructureGenerator template, IWorldStructures structures, World world, Random random, int chunkX, int chunkZ) {                
    BiomeGenBase bgb = world.getBiomeGenForCoords((chunkX << 4) + 1, (chunkZ << 4) + 1);            
    return filter.isMatchingBiome(bgb);
  }

}
