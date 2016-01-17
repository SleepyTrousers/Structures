package crazypants.structures.gen.structure.validator;

import java.util.Random;

import crazypants.structures.api.gen.IChunkValidator;
import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.api.gen.IWorldStructures;
import crazypants.structures.gen.structure.validator.biome.BiomeFilterAny;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class BiomeValidatorAll extends BiomeFilterAny implements IChunkValidator {

  public BiomeValidatorAll() {
    super("BiomeValidatorAll");
  }

  @Override
  public boolean isValidChunk(IStructureGenerator template, IWorldStructures structures, World world, Random random, int chunkX, int chunkZ) {                
    BiomeGenBase bgb = world.getBiomeGenForCoords(new BlockPos((chunkX << 4) + 1, 64, (chunkZ << 4) + 1));            
    return isMatchingBiome(bgb);
  }
}
