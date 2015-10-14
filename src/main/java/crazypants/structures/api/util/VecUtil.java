package crazypants.structures.api.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import crazypants.structures.api.gen.IStructureTemplate;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class VecUtil {

  public static Vec3 scale(Vec3 vec, double scale) {
    Vec3 result = copy(vec);
    result.xCoord = vec.xCoord * scale;
    result.yCoord = vec.yCoord * scale;
    result.zCoord = vec.zCoord * scale;
    return result;
  }

  public static Vec3 copy(Vec3 vec) {
    return Vec3.createVectorHelper(vec.xCoord, vec.yCoord, vec.zCoord);
  }

  public static Vec3 subtract(Vec3 a, Vec3 b) {
    return Vec3.createVectorHelper(a.xCoord - b.xCoord, a.yCoord - b.yCoord, a.zCoord - b.zCoord);
  }

  public static Vec3 add(Vec3 a, Vec3 b) {
    return Vec3.createVectorHelper(a.xCoord + b.xCoord, a.yCoord + b.yCoord, a.zCoord + b.zCoord);
  }

  public static void set(Vec3 pos, double posX, double posY, double posZ) {
    pos.xCoord = posX;
    pos.yCoord = posY;
    pos.zCoord = posZ;
  }

  public static boolean isInBounds(AxisAlignedBB bb, Point3i bc) {
    return bb.isVecInside(Vec3.createVectorHelper(bc.x, bc.y, bc.z));
  }

  public static Point3i transformStructureCoodToWorld(int worldOriginX, int worldOriginY, int worldOriginZ, Rotation componentRotation, Point3i componentSize,
      Point3i localCoord) {
    Point3i bc = new Point3i(localCoord);
    componentRotation.rotate(bc, componentSize.x - 1, componentSize.z - 1);
    bc.add(worldOriginX, worldOriginY, worldOriginZ);
    return bc;
  }

  public static Collection<Point3i> getTaggedLocationsInWorldCoords(IStructureTemplate template, String target, int originX, int originY, int originZ,
      Rotation rotation) {
    List<Point3i> res = new ArrayList<Point3i>();

    Collection<Point3i> locs = template.getTaggedLocations(target);
    for (Point3i p : locs) {
      Point3i xFormed = new Point3i(p);
      xFormed = VecUtil.transformStructureCoodToWorld(originX, originY, originZ, rotation, template.getSize(), xFormed);
      res.add(xFormed);
    }
    return res;
  }

  public static Point3i size(AxisAlignedBB bb) {
    return new Point3i((int) Math.abs(bb.maxX - bb.minX), (int) Math.abs(bb.maxY - bb.minY), (int) Math.abs(bb.maxZ - bb.minZ));
  }

  public static StructureBoundingBox createForChunk(int chunkX, int chunkZ) {
    int minX = chunkX << 4;
    int minZ = chunkZ << 4;
    return new StructureBoundingBox(minX, minZ, minX + 16, minZ + 16);
  }

  public static boolean isInBounds(StructureBoundingBox bb, int x, int z) {
    return x >= bb.minX & x <= bb.maxX && z >= bb.minZ && z <= bb.maxZ;
  }

}
