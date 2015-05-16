package crazypants.structures.gen.structure.sampler;

import java.util.Random;

import net.minecraft.world.World;
import crazypants.structures.gen.WorldStructures;
import crazypants.structures.gen.structure.Structure;
import crazypants.vec.Point3i;

public interface ILocationSampler {

  Point3i generateCandidateLocation(Structure structure, WorldStructures structures, World world,
      Random random, int chunkX, int chunkZ);
}
