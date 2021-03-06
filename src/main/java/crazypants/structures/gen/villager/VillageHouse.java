package crazypants.structures.gen.villager;

import java.util.Collection;
import java.util.Random;

import crazypants.structures.EnderStructures;
import crazypants.structures.Log;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.api.util.Vector2d;
import crazypants.structures.gen.structure.Structure;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;

public class VillageHouse extends StructureVillagePieces.House1 {

  private int villagerId;

  private int averageGroundLevel = -1;

  private IStructure structure;
  private IStructureTemplate template;

  //Used to adjust the yoffset when adding villagers so they don't spawn in basements
  private boolean addingVilagers = false;
  
  private String spawnTag;

  public VillageHouse() {
  }

  public VillageHouse(IStructureTemplate template, String spawnTag, int villagerId, int x, int y, int z, int coordBaseMode) {
    this.template = template;
    this.spawnTag = spawnTag;
    this.villagerId = villagerId;
    this.coordBaseMode = coordBaseMode;
    
    structure = template.createInstance(getRotation());    

    AxisAlignedBB bb = structure.getBounds();
    bb = bb.getOffsetBoundingBox(x, y, z);
    boundingBox = new StructureBoundingBox((int) bb.minX, (int) bb.minY, (int) bb.minZ, (int) bb.maxX, (int) bb.maxY, (int) bb.maxZ);
    if(coordBaseMode == 1) {
      boundingBox.offset((int) -(bb.maxX - bb.minX), 0, 0);
    } else if(coordBaseMode == 2) {
      boundingBox.offset(0, 0, (int) -(bb.maxZ - bb.minZ));
    }
  }

  @Override
  public boolean addComponentParts(World world, Random random, StructureBoundingBox bb) {

    if(structure == null || template == null) {
      return false;
    }

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
    if(!structure.isValid()) {
      return false;
    }    
    
    template.build(structure, world, random, bb);    
    EnderStructures.structureRegister.getStructuresForWorld(world).add(structure);    
    structure.onGenerated(world);
    
    if(!world.isRemote && villagerId > 0) {
      addingVilagers = true;
      try {        
        Point3i spawnPoint = getSpawnPoint();
        if(spawnPoint == null) {
          Vector2d or = structure.getBoundingCircle().getOrigin();          
          spawnPoint = new Point3i((int)or.x, 1 + structure.getSurfaceOffset(), (int)or.y); 
        }
        spawnVillagers(world, bb, spawnPoint.x, spawnPoint.y, spawnPoint.z, 1);
      } finally {
        addingVilagers = false;
      }
    }
    return true;
  }
  
  private Point3i getSpawnPoint() {
    if(spawnTag == null) {
      return null;
    }
    Collection<Point3i> locs = structure.getTemplate().getTaggedLocations(spawnTag);
    if(locs == null || locs.isEmpty()) {
      return null;
    }
    return locs.iterator().next();
  }

  @Override
  protected int getYWithOffset(int offset) {
    if(!addingVilagers || coordBaseMode == -1 || structure == null) {
      return super.getYWithOffset(offset);
    } else {
      //Adjusting Y offset so villages spawn at ground level
      return offset + this.boundingBox.minY + structure.getSurfaceOffset();
    }

  }

  private Rotation getRotation() {
    return Rotation.values()[coordBaseMode];
  }

  @Override
  protected int getVillagerType(int par1) {
    return villagerId;
  }

  @Override
  protected void func_143012_a(NBTTagCompound nbt) {
    super.func_143012_a(nbt);
    nbt.setInteger("villagerId", villagerId);
    nbt.setInteger("averageGroundLevel", averageGroundLevel);

    if(structure != null && structure.isValid()) {
      NBTTagCompound strRoot = new NBTTagCompound();
      structure.writeToNBT(strRoot);
      nbt.setTag("structure", strRoot);
    }

  }

  @Override
  protected void func_143011_b(NBTTagCompound nbt) {
    super.func_143011_b(nbt);
    villagerId = nbt.getInteger("villagerId");
    averageGroundLevel = nbt.getInteger("averageGroundLevel");

    if(nbt.hasKey("structure")) {
      NBTTagCompound strRoot = nbt.getCompoundTag("structure");
      structure = new Structure(strRoot);
      if(!structure.isValid()) {
        Log.warn("VillageHouse: Could not load template for previously generated house: " + structure.getUid());
        structure = null;
        template = null;        
      }
    }
  }

}