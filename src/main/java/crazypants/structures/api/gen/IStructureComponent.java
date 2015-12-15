package crazypants.structures.api.gen;

import java.util.Collection;

import com.google.common.collect.Multimap;

import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public interface IStructureComponent extends IResource {  

  int getSurfaceOffset();

  AxisAlignedBB getBounds();

  Point3i getSize();

  Collection<Point3i> getTaggedLocations(String tag);
  
  Collection<String> getTagsAtLocation(Point3i loc);
  
  Multimap<String, Point3i> getTaggedLocations();
  
  void build(World world, int x, int y, int z, Rotation rot, StructureBoundingBox genBounds);

  

}