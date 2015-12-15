package crazypants.structures.api.gen;

import java.util.List;
import java.util.Random;

import crazypants.structures.api.runtime.IBehaviour;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public interface IStructureTemplate extends IResource {

  //TODO: These need offsets
  List<PositionedComponent> getComponents();

  List<Rotation> getRotations();

  boolean isValid();
  
  boolean getCanSpanChunks();

  boolean getGenerationRequiresLoadedChunks();

  
  AxisAlignedBB getBounds();

  Point3i getSize();

  int getSurfaceOffset();
  
  ISiteValidator getSiteValiditor();
  
  IBehaviour getBehaviour();

  List<Point3i> getTaggedLocations(String target);
  

  IStructure createInstance();
  
  IStructure createInstance(Rotation rotation);
  
  void build(IStructure structure, World world, Random random, StructureBoundingBox clipBounds);

  void setSitePreperation(ISitePreperation sitePreperation);

  ISitePreperation getSitePreperation();

  

}