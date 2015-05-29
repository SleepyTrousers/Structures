package crazypants.structures.gen.structure.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import crazypants.structures.gen.WorldStructures;
import crazypants.structures.gen.structure.StructureGenerator;

public class CompositeValidator implements IChunkValidator {

  private final List<IChunkValidator> validators = new ArrayList<IChunkValidator>();
  
  public void add(IChunkValidator rule) {
    validators.add(rule);
  }

  @Override
  public boolean isValidChunk(StructureGenerator generator, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {
    for (IChunkValidator rule : validators) {
      if(!rule.isValidChunk(generator, structures, world, random, chunkX, chunkZ)) {
//        System.out.println("CompositeValidator.isValidChunk: Failed rule: " + rule);
        return false;
      }
    }
    return true;
  }

  public Collection<IChunkValidator> getValidators() {
    return validators;
  }

}
