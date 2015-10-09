package crazypants.structures.gen.structure.preperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import crazypants.structures.api.gen.ISitePreperation;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.util.ChunkBounds;

public class CompositePreperation implements ISitePreperation {

  private final List<ISitePreperation> preps = new ArrayList<ISitePreperation>();

  public CompositePreperation() {
  }

  public void add(ISitePreperation prep) {
    preps.add(prep);
  }

  @Override
  public boolean prepareLocation(IStructure structure, World world, Random random, ChunkBounds bounds) {
    for (ISitePreperation rule : preps) {
      if (!rule.prepareLocation(structure, world, random, bounds)) {
        return false;
      }
    }
    return true;
  }

}
