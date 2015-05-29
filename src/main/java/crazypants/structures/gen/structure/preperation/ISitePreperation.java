package crazypants.structures.gen.structure.preperation;

import java.util.Random;

import net.minecraft.world.World;
import crazypants.structures.gen.ChunkBounds;
import crazypants.structures.gen.structure.Structure;

public interface ISitePreperation {

  boolean prepareLocation(Structure structure, World world, Random random, ChunkBounds bounds);

}
