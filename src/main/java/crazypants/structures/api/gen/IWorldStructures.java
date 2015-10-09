package crazypants.structures.api.gen;

import java.util.Collection;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

public interface IWorldStructures {

  World getWorld();

  void add(IStructure s);

  void addAll(Collection<IStructure> structures);

  Collection<IStructure> getStructuresWithOriginInChunk(ChunkCoordIntPair chunkPos);

  Collection<IStructure> getStructuresWithOriginInChunk(ChunkCoordIntPair chunkPos, String templateUid);

  void getStructuresWithOriginInChunk(ChunkCoordIntPair chunkPos, String templateUid, Collection<IStructure> result);

  void getStructuresIntersectingChunk(ChunkCoordIntPair chunk, String structureUid, Collection<IStructure> res);

  int getStructureCount();

}