package crazypants.structures.gen.structure.validator;

import java.util.Random;

import net.minecraft.world.World;
import crazypants.structures.gen.ChunkBounds;
import crazypants.structures.gen.WorldStructures;
import crazypants.structures.gen.structure.Structure;

public interface ISiteValidator {

  boolean isValidBuildSite(Structure structure, WorldStructures existingStructures, World world, Random random, ChunkBounds bounds);
  
}
