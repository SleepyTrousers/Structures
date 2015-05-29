package crazypants.structures.gen.structure.decorator;

import java.util.Random;

import net.minecraft.world.World;
import crazypants.structures.gen.ChunkBounds;
import crazypants.structures.gen.structure.Structure;

public interface IDecorator {

  void decorate(Structure structure, World world, Random random, ChunkBounds bounds);
  
}
