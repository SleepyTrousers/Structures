package crazypants.structures.api.gen;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import crazypants.structures.api.util.ChunkBounds;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public interface IStructureTemplate {

  String getUid();

  //TODO: These need offsets
  List<IStructureComponent> getComponents();

  List<Rotation> getRotations();

  boolean isValid();
  
  boolean getCanSpanChunks();

  boolean getGenerationRequiresLoadedChunks();

  
  AxisAlignedBB getBounds();

  Point3i getSize();

  int getSurfaceOffset();
  
  ISiteValidator getSiteValiditor();

  Collection<Point3i> getTaggedLocations(String target);
  

  IStructure createInstance();
  
  void build(IStructure structure, World world, Random random, ChunkBounds bounds);

  

}