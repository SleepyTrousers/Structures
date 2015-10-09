package crazypants.structures.api.gen;

import java.util.Random;

import net.minecraft.world.World;

public interface IChunkValidator {

  boolean isValidChunk(IStructureGenerator template, IWorldStructures structures, World world, Random random, int chunkX, int chunkZ);

}
