package crazypants.structures.gen.structure.preperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.annotations.Expose;

import crazypants.structures.AbstractTyped;
import crazypants.structures.api.gen.ISitePreperation;
import crazypants.structures.api.gen.IStructure;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class CompositePreperation extends AbstractTyped implements ISitePreperation {

  @Expose
  private final List<ISitePreperation> preperations = new ArrayList<ISitePreperation>();

  public CompositePreperation() {
    super("CompositePreperation");
  }

  public void add(ISitePreperation prep) {
    preperations.add(prep);
  }

  @Override
  public boolean prepareLocation(IStructure structure, World world, Random random, StructureBoundingBox bounds) {
    for (ISitePreperation rule : preperations) {
      if (!rule.prepareLocation(structure, world, random, bounds)) {
        return false;
      }
    }
    return true;
  }

}
