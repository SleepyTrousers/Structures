package crazypants.structures.gen.villager;

import java.util.Random;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.gen.StructureRegister;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;

public class VillageHouse extends StructureVillagePieces.House1 {

  private int villagerId;

  private int averageGroundLevel = -1;

  private IStructure structure;
  private IStructureTemplate template;

  public VillageHouse() {

  }

  public VillageHouse(String templateUid, int villagerId, int x, int y, int z, int coordBaseMode) {
    this.villagerId = villagerId;
    this.coordBaseMode = coordBaseMode;

    template = StructureRegister.instance.getStructureTemplate(templateUid, true);
    structure = template.createInstance(getRotation());

    AxisAlignedBB bb = structure.getBounds();
    bb = bb.getOffsetBoundingBox(x, y, z);
    this.boundingBox = new StructureBoundingBox((int) bb.minX, (int) bb.minY, (int) bb.minZ, (int) bb.maxX, (int) bb.maxY, (int) bb.maxZ);

    if(coordBaseMode == 1) {
      boundingBox.offset((int) -(bb.maxX - bb.minX), 0, 0);
    } else if(coordBaseMode == 2) {
      boundingBox.offset(0, 0, (int) -(bb.maxZ - bb.minZ));
    }


  }

  @Override
  public boolean addComponentParts(World world, Random random, StructureBoundingBox bb) {
       
    if(averageGroundLevel < 0) {
      averageGroundLevel = getAverageGroundLevel(world, bb);

      if(averageGroundLevel < 0) {
        return true;
      }

      boundingBox.offset(0, averageGroundLevel - boundingBox.minY - 1 - structure.getSurfaceOffset(), 0);      
    }
    Point3i origin = new Point3i(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
    
    
    //WHats the deal with this? 
    //public abstract static class Village extends StructureComponent
    // /**
    // * Gets the next village component, with the bounding box shifted -1 in the X and Z direction.
    // */
    //protected StructureComponent getNextComponentNN(
    
    if(coordBaseMode == 1) {
      origin.x++;
    } else if(coordBaseMode == 2) {
      origin.z++;
    }
    structure.setOrigin(origin);
    template.build(structure, world, random, bb);

    spawnVillagers(world, bb, 3, 1, 3, 1);

    return true;
  }

  private Rotation getRotation() {
    return Rotation.values()[coordBaseMode];
  }

  @Override
  protected int getVillagerType(int par1) {
    return villagerId;
  }
}