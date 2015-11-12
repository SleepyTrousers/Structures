package crazypants.structures.gen.structure.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.google.gson.annotations.Expose;

import crazypants.structures.AbstractTyped;
import crazypants.structures.api.gen.ISiteValidator;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.gen.IWorldStructures;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class CompositeSiteValidator extends AbstractTyped implements ISiteValidator {

  public CompositeSiteValidator() {
    super("CompositeSiteValidator");
  }

  @Expose
  private final List<ISiteValidator> validators = new ArrayList<ISiteValidator>();

  public void add(ISiteValidator rule) {
    validators.add(rule);
  }

  @Override
  public boolean isValidBuildSite(IStructure structure, IWorldStructures existingStructures, World world, Random random, StructureBoundingBox bounds) {
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
