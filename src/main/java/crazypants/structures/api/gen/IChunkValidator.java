package crazypants.structures.api.gen;

import java.util.Random;

import crazypants.structures.api.ITyped;
import net.minecraft.world.World;

public interface IChunkValidator extends ITyped {

  boolean isValidChunk(IStructureGenerator template, IWorldStructures structures, World world, Random random, int chunkX, int chunkZ);

}
