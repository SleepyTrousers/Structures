package crazypants.structures.api.gen;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public interface ISitePreperation {

  boolean prepareLocation(IStructure structure, World world, Random random, StructureBoundingBox bounds);

}
