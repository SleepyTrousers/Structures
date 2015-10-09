package crazypants.structures.api.gen;

import java.util.Random;

import crazypants.structures.api.util.ChunkBounds;
import net.minecraft.world.World;

public interface ISitePreperation {

  boolean prepareLocation(IStructure structure, World world, Random random, ChunkBounds bounds);

}
