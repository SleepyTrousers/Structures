package crazypants.structures.gen.structure.preperation;

import java.util.Random;

import net.minecraft.world.World;
import crazypants.structures.gen.WorldStructures;
import crazypants.structures.gen.structure.Structure;

public interface ISitePreperation {

  boolean prepareLocation(Structure structure, WorldStructures structures, World world, Random random, int chunkX, int chunkZ);

}
