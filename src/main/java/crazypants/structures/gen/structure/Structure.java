package crazypants.structures.gen.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import crazypants.structures.gen.BoundingCircle;
import crazypants.structures.gen.ChunkBounds;
import crazypants.structures.gen.StructureRegister;
import crazypants.structures.gen.WorldStructures;
import crazypants.vec.Point3i;

public class Structure {

  private final Point3i origin;
  private final Rotation rotation;
  private final StructureTemplate template;

  private BoundingCircle bc; 
  private AxisAlignedBB bb;
  private Point3i size;
  

  public Structure(Point3i origin, Rotation rotation, StructureTemplate template) {    
    if(origin == null) {
      origin = new Point3i();
    }
    this.origin = origin;
    if(rotation == null) {
      this.rotation = Rotation.DEG_0;
    } else {
      this.rotation = rotation;
    }
    this.template = template;
    updateBounds();
  }

  public Structure(NBTTagCompound root) {        
    template = StructureRegister.instance.getStructureTemplate(root.getString("template"), true);      
    origin = new Point3i(root.getInteger("x"), root.getInteger("y"), root.getInteger("z"));
    rotation = Rotation.values()[MathHelper.clamp_int(root.getShort("rotation"), 0, Rotation.values().length - 1)];    
    updateBounds();
  }

  public AxisAlignedBB getBounds() {
    return bb;
  }

  public Point3i getSize() {
    return size;
  }

  public Point3i getOrigin() {
    return origin;
  }

  public void setOrigin(Point3i origin) {
    this.origin.set(origin.x, origin.y, origin.z);
    updateBounds();
  }

  private void updateBounds() {
    if(isValid()) {
      bb = rotation.rotate(template.getBounds());
      bb = bb.getOffsetBoundingBox(origin.x, origin.y, origin.z);
      size = size(bb);
      bc = new BoundingCircle(bb);
    }
  }
  
  public int getSurfaceOffset() {    
    return template.getSurfaceOffset();
  }  

  public static Point3i size(AxisAlignedBB bb) {
    return new Point3i((int) Math.abs(bb.maxX - bb.minX), (int) Math.abs(bb.maxY - bb.minY), (int) Math.abs(bb.maxZ - bb.minZ));
  }

  public ChunkCoordIntPair getChunkCoord() {
    return new ChunkCoordIntPair(origin.x >> 4, origin.z >> 4);
  }

  public ChunkBounds getChunkBounds() {
    Point3i size = template.getSize();
    return new ChunkBounds(origin.x >> 4, origin.z >> 4, (origin.x + size.x) >> 4, (origin.z + size.z) >> 4);
  }

  public void writeToNBT(NBTTagCompound root) {
    root.setInteger("x", origin.x);
    root.setInteger("y", origin.y);
    root.setInteger("z", origin.z);
    root.setString("template", template.getUid());
    root.setShort("rotation", (short) rotation.ordinal());
  }

  public boolean isChunkBoundaryCrossed() {
    return getChunkBounds().getNumChunks() > 1;
  }

  public boolean canSpanChunks() {    
    return template.getCanSpanChunks();
  }

  public void build(World world, Random random, ChunkBounds bounds) {
    template.build(this, world, random, bounds);
  }
  
  public boolean getGenerationRequiresLoadedChunks() {
    return template.getGenerationRequiresLoadedChunks();
  }

  public double getBoundingRadius() {
    return bc.getRadius();
  }

  public BoundingCircle getBoundingCircle() {
    return bc;
  }
  
  public boolean isValid() {
    return origin != null && template != null;
  }

  public StructureTemplate getTemplate() {
    return template;
  }

  @Override
  public String toString() {    
    return "Structure [template=" + (template == null ? "null" : template.getUid()) + ", origin=" + origin + "]";    
  }

  public String getUid() {
    return template.getUid();
  }

  public Rotation getRotation() {
    return rotation;
  }

  public boolean isValidSite(WorldStructures existingStructures, World world, Random random, ChunkBounds bounds) {
    return template.getSiteValiditor().isValidBuildSite(this, existingStructures, world, random, bounds);
  }

  public Collection<Point3i> getTaggedLocations(String target) {
    Collection<Point3i> locs = template.getTaggedLocations(target);
    if(locs == null) {
      return Collections.emptyList();
    }
    if(rotation == null || rotation == Rotation.DEG_0) {      
      return locs;
    }
    //Need to rotate the points
    List<Point3i> res = new ArrayList<Point3i>(locs.size());
    for(Point3i l : locs) {
      Point3i loc = new Point3i(l);
      rotation.rotate(loc, size.x, size.z);
      loc.add(origin);
      res.add(loc);
    }    
    return res;
  }

}
