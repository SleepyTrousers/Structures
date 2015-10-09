package crazypants.structures.api.gen;

import java.util.Random;

import crazypants.structures.api.util.ChunkBounds;
import net.minecraft.world.World;

public interface ISiteValidator {

  boolean isValidBuildSite(IStructure structure, IWorldStructures existingStructures, World world, Random random, ChunkBounds bounds);
  
}
