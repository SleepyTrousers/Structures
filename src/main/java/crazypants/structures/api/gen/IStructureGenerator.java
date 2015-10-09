package crazypants.structures.api.gen;

import java.util.Collection;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

public interface IStructureGenerator {

  String getUid();

  Collection<IStructure> generate(IWorldStructures structures, Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider);

}