package crazypants.structures.api.gen;

import java.util.Random;

import crazypants.structures.api.ITyped;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public interface ISitePreperation extends ITyped {

  boolean prepareLocation(IStructure structure, World world, Random random, StructureBoundingBox bounds);

}
