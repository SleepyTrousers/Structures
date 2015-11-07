package crazypants.structures;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.HashMultimap;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.gen.io.JsonUtil;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class StructureUtils {

  public static boolean canSpawnEntity(World world, EntityLiving entityliving, boolean doEntityChecks) {
    boolean spaceClear = world.checkNoEntityCollision(entityliving.boundingBox)
        && world.getCollidingBoundingBoxes(entityliving, entityliving.boundingBox).isEmpty()
        && (!world.isAnyLiquid(entityliving.boundingBox) || entityliving.isCreatureType(EnumCreatureType.waterCreature, false));
    if(spaceClear && doEntityChecks) {
      //Full checks for lighting, dimension etc 
      spaceClear = entityliving.getCanSpawnHere();
    }
    return spaceClear;
  }

  public static boolean spawnEntity(World world, EntityLiving entityliving, Point3i worldPos, int spawnRange, int numAttempts, boolean doEntityChecks) {

    while (numAttempts-- > 0) {
      double x = worldPos.x + (world.rand.nextDouble() - world.rand.nextDouble()) * spawnRange;
      double y = worldPos.y + world.rand.nextInt(3) - 1;
      double z = worldPos.z + (world.rand.nextDouble() - world.rand.nextDouble()) * spawnRange;
      entityliving.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360.0F, 0.0F);

      if(StructureUtils.canSpawnEntity(world, entityliving, doEntityChecks)) {
        entityliving.onSpawnWithEgg(null);
        world.spawnEntityInWorld(entityliving);
        return true;
      }
    }

    return false;
  }

  public static NBTTagCompound createEntityNBT(String entityName, String entityNbtText) {
    if(entityName == null) {
      return null;
    }
    NBTTagCompound entityNBT = JsonUtil.parseNBT(entityNbtText);
    if(entityNBT == null) {
      entityNBT = new NBTTagCompound();
    }
    entityNBT.setString("id", entityName);
    return entityNBT;
  }

  public static Point3i getTaggedLocationInStructureCoords(String tag, IStructure structure, Point3i fallback) {
    if(structure == null) {
      return fallback;
    }

    if(tag != null) {
      Collection<Point3i> locs = structure.getTaggedLocationsInLocalCoords(tag);
      if(locs != null && !locs.isEmpty()) {
        if(locs.size() > 1) {
//          Log.warn("StructureUtils: Found mroe than one position for tag [" + tag + "] Using first only");
        }
        return locs.iterator().next();
      }
      Log.warn("StructureUtils: Could not find position for tag  [" + tag + "] Using structure fallback position");
    }

    if(fallback == null) {
      return null;
    }    
    return structure.getRotatedLocation(fallback);

  }

  public static Collection<String> getTagsAtLocation(HashMultimap<String, Point3i> taggedLocations, Point3i loc) {
    if(loc == null) {
      return Collections.emptyList();
    }
    Set<String> res = new HashSet<String>();
    Set<Entry<String, Point3i>> entries = taggedLocations.entries();
    for (Entry<String, Point3i> entry : entries) {
      if(loc.equals(entry.getValue())) {
        res.add(entry.getKey());
      }
    }
    return res;
  }

  public static void writeTaggedLocationToNBT(HashMultimap<String, Point3i> taggedLocations, NBTTagCompound root) {
    if(taggedLocations != null && !taggedLocations.isEmpty()) {
      NBTTagList locationsList = new NBTTagList();
      for (Entry<String, Collection<Point3i>> e : taggedLocations.asMap().entrySet()) {
        if(e.getValue() != null && !e.getValue().isEmpty()) {
          NBTTagCompound loc = new NBTTagCompound();
          loc.setString("tag", e.getKey());
          loc.setInteger("numCoords", e.getValue().size());
          loc.setByteArray("coords", StructureUtils.writeCoordsToByteArray(e.getValue()));
          locationsList.appendTag(loc);
        }
      }
      root.setTag("taggedLocations", locationsList);
    }
  }

  public static void readTaggedLocations(HashMultimap<String, Point3i> taggedLocations, NBTTagCompound root) {
    if(root.hasKey("taggedLocations")) {
      NBTTagList locs = (NBTTagList) root.getTag("taggedLocations");
      if(locs != null) {
        for (int i = 0; i < locs.tagCount(); i++) {
          NBTTagCompound tag = locs.getCompoundTagAt(i);
          String tagStr = tag.getString("tag");
          if(tagStr != null && !tagStr.trim().isEmpty()) {
            byte[] coordData = tag.getByteArray("coords");
            int numCoords = tag.getInteger("numCoords");
            if(coordData != null && numCoords > 0) {
              List<Point3i> points = new ArrayList<Point3i>();
              StructureUtils.readPoints(points, coordData, numCoords);
              taggedLocations.putAll(tagStr, points);
            }
          }
        }
      }
    }
  }

  public static byte[] writeCoordsToByteArray(Collection<Point3i> coords) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      for (Point3i coord : coords) {
        StructureUtils.writePoint(dos, coord);
      }
    } catch (IOException e) {
      Log.error("StructureTemplate: Ccould not write coords: " + e);
    }
    byte[] bytes = bos.toByteArray();
    return bytes;
  }

  public static List<Point3i> readCoords(NBTTagCompound entryTag) {
    byte[] bytes = entryTag.getByteArray("coords");
    int numCoords = entryTag.getInteger("numCoords");
    List<Point3i> coords = new ArrayList<Point3i>(numCoords);
    StructureUtils.readPoints(coords, bytes, numCoords);
    return coords;
  }

  public static void readPoints(List<Point3i> readInto, byte[] readFrom, int numCoords) {
    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(readFrom));
    try {
      for (int i = 0; i < numCoords; i++) {
        readInto.add(StructureUtils.readPoint(dis));
      }
    } catch (IOException e) {
      Log.error("StructureTemplate: Ccould not read coords: " + e);
    }
  }

  public static Point3i readPoint(DataInputStream dis) throws IOException {
    return new Point3i(dis.readShort(), dis.readShort(), dis.readShort());
  }

  public static void writePoint(DataOutputStream dos, Point3i coord) throws IOException {
    dos.writeShort((short) coord.x);
    dos.writeShort((short) coord.y);
    dos.writeShort((short) coord.z);
  }

}
