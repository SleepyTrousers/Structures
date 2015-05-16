package crazypants.structures.gen.structure.validator.biome;

import net.minecraft.world.biome.BiomeGenBase;

public interface IBiomeFilter {

  void addBiomeDescriptor(IBiomeDescriptor biome);

  BiomeGenBase[] getMatchedBiomes();

  boolean isMatchingBiome(BiomeGenBase bgb);

}
