package crazypants.structures.api.gen;

import java.util.List;

import crazypants.structures.api.util.ChunkBounds;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public interface IStructureComponent {

  String getUid();

  int getSurfaceOffset();

  AxisAlignedBB getBounds();

  Point3i getSize();

  List<Point3i> getTaggedLocations(String tag);
  
  void build(World world, int x, int y, int z, Rotation rot, ChunkBounds genBounds);

}