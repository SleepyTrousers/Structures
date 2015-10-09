package crazypants.structures.api.gen;

import java.util.Random;

import crazypants.structures.api.util.Point3i;

public interface ILocationSampler {

  Point3i generateCandidateLocation(IStructure newStructure, IWorldStructures existingStructures, Random random,
      int chunkX, int chunkZ);
}
