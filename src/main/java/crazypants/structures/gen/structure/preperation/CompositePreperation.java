package crazypants.structures.gen.structure.preperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.annotations.Expose;

import crazypants.structures.AbstractTyped;
import crazypants.structures.api.ListElementType;
import crazypants.structures.api.gen.ISitePreperation;
import crazypants.structures.api.gen.IStructure;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class CompositePreperation extends AbstractTyped implements ISitePreperation {

  @ListElementType(elementType=ISitePreperation.class)
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

  @Override
  public StructureBoundingBox getEffectedBounds(IStructure structure) {
    AxisAlignedBB bb = structure.getBounds();
    StructureBoundingBox res = new StructureBoundingBox((int)bb.minX,(int)bb.minY,(int)bb.minZ,(int)bb.maxX,(int)bb.maxY,(int)bb.maxZ);
    for (ISitePreperation rule : preperations) {
      StructureBoundingBox bounds = rule.getEffectedBounds(structure);
      if(bounds != null) {
        res.expandTo(bounds);
      }
    }
    return res;
  }

}
