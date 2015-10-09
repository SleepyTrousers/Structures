package crazypants.structures.gen.structure;

import java.util.Collection;
import java.util.Random;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.gen.IWorldStructures;
import crazypants.structures.api.util.BoundingCircle;
import crazypants.structures.api.util.ChunkBounds;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.api.util.VecUtil;
import crazypants.structures.gen.StructureRegister;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

public class Structure implements IStructure {

  private final Point3i origin;
  private final Rotation rotation;
  private final IStructureTemplate template;

  private BoundingCircle bc; 
  private AxisAlignedBB bb;
  private Point3i size;
  

  public Structure(Point3i origin, Rotation rotation, IStructureTemplate template) {    
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

  @Override
  public AxisAlignedBB getBounds() {
    return bb;
  }

  @Override
  public Point3i getSize() {
    return size;
  }

  @Override
  public Point3i getOrigin() {
    return origin;
  }

  @Override
  public void setOrigin(Point3i origin) {
    this.origin.set(origin.x, origin.y, origin.z);
    updateBounds();
  }

  private void updateBounds() {
    if(isValid()) {
      bb = rotation.rotate(template.getBounds());
      bb = bb.getOffsetBoundingBox(origin.x, origin.y, origin.z);
      size = VecUtil.size(bb);
      bc = new BoundingCircle(bb);
    }
  }
  
  @Override
  public int getSurfaceOffset() {    
    return template.getSurfaceOffset();
  }  

  @Override
  public ChunkCoordIntPair getChunkCoord() {
    return new ChunkCoordIntPair(origin.x >> 4, origin.z >> 4);
  }

  @Override
  public ChunkBounds getChunkBounds() {
    Point3i size = template.getSize();
    return new ChunkBounds(origin.x >> 4, origin.z >> 4, (origin.x + size.x) >> 4, (origin.z + size.z) >> 4);
  }

  @Override
  public void writeToNBT(NBTTagCompound root) {
    root.setInteger("x", origin.x);
    root.setInteger("y", origin.y);
    root.setInteger("z", origin.z);
    root.setString("template", template.getUid());
    root.setShort("rotation", (short) rotation.ordinal());
  }

  @Override
  public boolean isChunkBoundaryCrossed() {
    return getChunkBounds().getNumChunks() > 1;
  }

  @Override
  public boolean canSpanChunks() {    
    return template.getCanSpanChunks();
  }

  @Override
  public void build(World world, Random random, ChunkBounds bounds) {
    template.build(this, world, random, bounds);
  }
  
  @Override
  public boolean getGenerationRequiresLoadedChunks() {
    return template.getGenerationRequiresLoadedChunks();
  }

  @Override
  public double getBoundingRadius() {
    return bc.getRadius();
  }

  @Override
  public BoundingCircle getBoundingCircle() {
    return bc;
  }
  
  @Override
  public boolean isValid() {
    return origin != null && template != null;
  }

  @Override
  public IStructureTemplate getTemplate() {
    return template;
  }

  @Override
  public String toString() {    
    return "Structure [template=" + (template == null ? "null" : template.getUid()) + ", origin=" + origin + "]";    
  }

  @Override
  public String getUid() {
    return template.getUid();
  }

  @Override
  public Rotation getRotation() {
    return rotation;
  }

  @Override
  public boolean isValidSite(IWorldStructures existingStructures, World world, Random random, ChunkBounds bounds) {
    return template.getSiteValiditor().isValidBuildSite(this, existingStructures, world, random, bounds);
  }

  @Override
  public Collection<Point3i> getTaggedLocations(String target) {
    return VecUtil.getTaggedLocationsInWorldCoords(template, target, origin.x, origin.y, origin.z, rotation);
  }

}
