package crazypants.structures.gen.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import crazypants.structures.gen.ChunkBounds;
import crazypants.structures.gen.structure.decorator.CompositeDecorator;
import crazypants.structures.gen.structure.decorator.IDecorator;
import crazypants.structures.gen.structure.preperation.CompositePreperation;
import crazypants.structures.gen.structure.preperation.ISitePreperation;
import crazypants.structures.gen.structure.validator.CompositeSiteValidator;
import crazypants.structures.gen.structure.validator.ISiteValidator;
import crazypants.vec.Point3i;

public class StructureTemplate {
  
  private static final Random RND = new Random(987345098532932115L);
  
  private List<Rotation> rots = new ArrayList<Rotation>();
  
  //TODO: Need offsets for components
  private List<StructureComponent> components = new ArrayList<StructureComponent>();  
  private final CompositePreperation sitePreps = new CompositePreperation();
  private final CompositeSiteValidator siteVals = new CompositeSiteValidator();
  private final CompositeDecorator decorators = new CompositeDecorator();
  
  private boolean canSpanChunks = true;
  private boolean generationRequiresLoadedChunks = canSpanChunks;
  
  private final String uid;

  public StructureTemplate(String uid) {
    this.uid = uid;
  }
  
  public StructureTemplate(String uid, Collection<StructureComponent> components) {
    this(uid);
    if(components != null) {
      this.components.addAll(components);
    }        
  }
  
  public Structure createInstance() {
    return new Structure(new Point3i(), getRndRotation(), this);
  }

  public boolean getCanSpanChunks() {
    return canSpanChunks;
  }

  public void setCanSpanChunks(boolean canSpanChunks) {
    this.canSpanChunks = canSpanChunks;
  }

  public boolean getGenerationRequiresLoadedChunks() {
    return generationRequiresLoadedChunks;
  }

  public void setGenerationRequiresLoadedChunks(boolean generationRequiresLoadedChunks) {
    this.generationRequiresLoadedChunks = generationRequiresLoadedChunks;
  }

  public List<StructureComponent> getComponents() {
    return components;
  }

  public List<Rotation> getRotations() {
    return rots;
  }

  public List<Rotation> getRots() {
    return rots;
  }

  public void setRotations(List<Rotation> rots) {
    this.rots = rots;
  }
  
  public void addComponent(StructureComponent st) {
    if(components == null) {
      components = new ArrayList<StructureComponent>();
    }
    components.add(st);    
  }

  public void setComponents(List<StructureComponent> components) {
    this.components = components;
  }

  public String getUid() {
    return uid;
  }
  
  public void addSitePreperation(ISitePreperation val) {
    if(val != null) {
      sitePreps.add(val);
    }
  }
  
  public void addSiteValidator(ISiteValidator val) {
    if(val != null) {
      siteVals.add(val);
    }    
  }
  
  public void addDecorator(IDecorator val) {
    if(val != null) {
      decorators.add(val);
    }    
  }

  private Rotation getRndRotation() {
    if(rots == null || rots.isEmpty()) {
      return Rotation.DEG_0;
    }
    return rots.get(RND.nextInt(rots.size()));    
  }
  
  public boolean isValid() {
    return components != null && !components.isEmpty();
  }

  public AxisAlignedBB getBounds() {
    return components.get(0).getBounds();
  }

  public Point3i getSize() {
    return components.get(0).getSize();
  }

  public int getSurfaceOffset() {
    return components.get(0).getSurfaceOffset();
  }

  public void build(Structure structure, World world, Random random, ChunkBounds bounds) {
    sitePreps.prepareLocation(structure, world, random, bounds);
    Point3i orig = structure.getOrigin();
    components.get(0).build(world, orig.x, orig.y, orig.z, structure.getRotation(), bounds);   
    if(random == null) {
      random = RND;
    }
    decorators.decorate(structure, world, random, bounds);
  }

  public ISiteValidator getSiteValiditor() {
    return siteVals;
  }

  public Collection<Point3i> getTaggedLocations(String target) {
    //TODO: Need to handle offset for components
    List<Point3i> res = new ArrayList<Point3i>();
    for(StructureComponent comp : components) {
      List<Point3i> r = comp.getTaggedLocations(target);
      if(r != null) {
        res.addAll(r);
      }
    }
    return res;
  }
  
  public Collection<Point3i> getTaggedLocations(String target, int originX, int originY, int originZ, Rotation rotation) {
    List<Point3i> res = new ArrayList<Point3i>();
    for(StructureComponent comp : components) {
      List<Point3i> locs = comp.getTaggedLocations(target);
      for(Point3i p : locs) {
        Point3i xFormed = new Point3i(p);
        xFormed = comp.transformToWorld(originX, originY, originZ, rotation, xFormed);
        res.add(xFormed);
      }
    }
    return res;
  }

}