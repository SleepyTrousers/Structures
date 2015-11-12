package crazypants.structures.api.gen;

import java.util.Random;

import crazypants.structures.api.ITyped;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public interface ISiteValidator extends ITyped {

  boolean isValidBuildSite(IStructure structure, IWorldStructures existingStructures, World world, Random random, StructureBoundingBox bounds);
  
}
