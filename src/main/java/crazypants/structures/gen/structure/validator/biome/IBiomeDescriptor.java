package crazypants.structures.gen.structure.validator.biome;

import net.minecraftforge.common.BiomeDictionary;

public interface IBiomeDescriptor {

  BiomeDictionary.Type getType();

  String getName();

  boolean isExclude();

}
