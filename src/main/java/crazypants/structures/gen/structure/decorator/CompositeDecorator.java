package crazypants.structures.gen.structure.decorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import crazypants.structures.gen.ChunkBounds;
import crazypants.structures.gen.structure.Structure;

public class CompositeDecorator implements IDecorator {

  private final List<IDecorator> decorators = new ArrayList<IDecorator>(); 
  
  public void add(IDecorator dec) {
    decorators.add(dec);
  }
  
  @Override
  public void decorate(Structure structure, World world, Random random, ChunkBounds bounds) {
    for(IDecorator d : decorators) {
      d.decorate(structure, world, random, bounds);
    }
    
  }

}
