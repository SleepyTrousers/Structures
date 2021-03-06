package crazypants.structures.gen.structure.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.google.gson.annotations.Expose;

import crazypants.structures.AbstractTyped;
import crazypants.structures.api.ListElementType;
import crazypants.structures.api.gen.IChunkValidator;
import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.api.gen.IWorldStructures;
import net.minecraft.world.World;

public class CompositeValidator extends AbstractTyped implements IChunkValidator {

  @ListElementType(elementType=IChunkValidator.class)
  @Expose
  private final List<IChunkValidator> validators = new ArrayList<IChunkValidator>();
  
  public CompositeValidator() {
    super("CompositeValidator");
  }
  
  public void add(IChunkValidator rule) {
    validators.add(rule);
  }

  @Override
  public boolean isValidChunk(IStructureGenerator generator, IWorldStructures structures, World world, Random random, int chunkX, int chunkZ) {
    for (IChunkValidator rule : validators) {
      if(rule != null && !rule.isValidChunk(generator, structures, world, random, chunkX, chunkZ)) {
        return false;
      }
    }
    return true;
  }

  public Collection<IChunkValidator> getValidators() {
    return validators;
  }

}
