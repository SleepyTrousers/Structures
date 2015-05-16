package crazypants.structures.gen.structure.validator;

import java.util.Random;

import net.minecraft.world.World;
import crazypants.structures.gen.WorldStructures;
import crazypants.structures.gen.structure.Structure;
import crazypants.structures.gen.structure.StructureGenerator;

public interface ILocationValidator {

  boolean isValidChunk(StructureGenerator template, WorldStructures structures, World world, Random random, int chunkX, int chunkZ);

  boolean isValidLocation(Structure structure, WorldStructures existingStructures, World world, Random random, int chunkX, int chunkZ);

}
