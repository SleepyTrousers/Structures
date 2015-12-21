package crazypants.structures.api.gen;

import java.util.List;
import java.util.Random;

import crazypants.structures.api.runtime.IBehaviour;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.gen.structure.loot.LootCategories;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public interface IStructureTemplate extends IResource {

  List<PositionedComponent> getComponents();

  List<Rotation> getRotations();

  boolean isValid();
  
  boolean getCanSpanChunks();

  boolean getGenerationRequiresLoadedChunks();
  
  LootCategories getLootCategories();
  
  AxisAlignedBB getBounds();

  Point3i getSize();

  int getSurfaceOffset();
  
  ISiteValidator getSiteValidator();
  
  void setSiteValidator(ISiteValidator val);
  
  IBehaviour getBehaviour();

  List<Point3i> getTaggedLocations(String target);
  
  List<String> getLocationTags();
  

  IStructure createInstance();
  
  IStructure createInstance(Rotation rotation);
  
  void build(IStructure structure, World world, Random random, StructureBoundingBox clipBounds);

  void setSitePreperation(ISitePreperation sitePreperation);

  ISitePreperation getSitePreperation();

  

}