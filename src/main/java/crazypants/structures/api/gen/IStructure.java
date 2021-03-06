package crazypants.structures.api.gen;

import java.util.Collection;
import java.util.Random;

import crazypants.structures.api.util.BoundingCircle;
import crazypants.structures.api.util.ChunkBounds;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public interface IStructure {
  
  //TODO: Dont think this should be here
  boolean isValidSite(IWorldStructures existingStructures, World world, Random random, StructureBoundingBox bounds);

  String getUid();
  
  IStructureTemplate getTemplate();
  
  Point3i getOrigin();

  void setOrigin(Point3i origin);
  
  Rotation getRotation();
  
  Point3i getRotatedLocation(Point3i localPos);
  
  Collection<Point3i> getTaggedLocationsInWorldCoords(String tag);
  
  Collection<Point3i> getTaggedLocationsInLocalCoords(String tag);
  

  //-- bounds
  
  Point3i getSize();
  
  AxisAlignedBB getBounds();
  
  BoundingCircle getBoundingCircle();
  
  double getBoundingRadius();

  ChunkBounds getChunkBounds();
  
  ChunkCoordIntPair getChunkCoord();
  
  int getSurfaceOffset();

  

  boolean isChunkBoundaryCrossed();
  
  boolean getGenerationRequiresLoadedChunks();

  boolean canSpanChunks();
  

  boolean isValid();

  void build(World world, Random random, StructureBoundingBox bounds);

  void writeToNBT(NBTTagCompound root);
  
  //the point is rotated then translated to world coords
  Point3i transformTemplateLocalToWorld(Point3i local);
  
  //the point is translated to world coords
  Point3i transformStructureLocalToWorld(Point3i local);

  //-- Runtime stuff
  NBTTagCompound getState();
  
  void onGenerated(World world);
  
  void onLoaded(World world, NBTTagCompound state);
  
  NBTTagCompound onUnloaded(World world);

  

}