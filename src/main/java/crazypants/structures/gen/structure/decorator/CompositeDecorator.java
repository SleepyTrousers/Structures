package crazypants.structures.gen.structure.decorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.annotations.Expose;

import crazypants.structures.AbstractTyped;
import crazypants.structures.api.ListElementType;
import crazypants.structures.api.gen.IDecorator;
import crazypants.structures.api.gen.IStructure;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class CompositeDecorator extends AbstractTyped implements IDecorator {

  @ListElementType(elementType=IDecorator.class)
  @Expose
  private final List<IDecorator> decorators = new ArrayList<IDecorator>();

  public CompositeDecorator() {
    super("CompositeDecorator");
  }

  public void add(IDecorator dec) {
    decorators.add(dec);
  }
  
  @Override
  public void decorate(IStructure structure, World world, Random random, StructureBoundingBox bounds) {
    for(IDecorator d : decorators) {
      d.decorate(structure, world, random, bounds);
    }
    
  }

}
