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

public interface IStructure {
  
  //TODO:
  boolean isValidSite(IWorldStructures existingStructures, World world, Random random, ChunkBounds bounds);

  String getUid();
  
  IStructureTemplate getTemplate();
  
  Point3i getOrigin();

  void setOrigin(Point3i origin);
  
  Rotation getRotation();
  
  Collection<Point3i> getTaggedLocations(String target);
  
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

  void build(World world, Random random, ChunkBounds bounds);

  void writeToNBT(NBTTagCompound root);

}