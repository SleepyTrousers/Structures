package crazypants.structures.gen.structure.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import crazypants.structures.gen.ChunkBounds;
import crazypants.structures.gen.WorldStructures;
import crazypants.structures.gen.structure.Structure;

public class CompositeSiteValidator implements ISiteValidator {

private final List<ISiteValidator> validators = new ArrayList<ISiteValidator>();
  
  public void add(ISiteValidator rule) {
    validators.add(rule);
  }

  
  @Override
  public boolean isValidBuildSite(Structure structure, WorldStructures existingStructures, World world, Random random, ChunkBounds bounds) {
    for (ISiteValidator rule : validators) {
      if(!rule.isValidBuildSite(structure, existingStructures, world, random, bounds)) {
//        System.out.println("CompositeSiteValidator.isValidChunk: Failed rule: " + rule);
        return false;
      }
    }
    return true;
  }

  public Collection<ISiteValidator> getValidators() {
    return validators;
  }
  
}
