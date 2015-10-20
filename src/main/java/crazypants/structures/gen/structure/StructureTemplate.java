package crazypants.structures.gen.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import crazypants.structures.api.gen.IDecorator;
import crazypants.structures.api.gen.ISitePreperation;
import crazypants.structures.api.gen.ISiteValidator;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.gen.IStructureComponent;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.gen.PositionedComponent;
import crazypants.structures.api.runtime.IBehaviour;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.gen.structure.decorator.CompositeDecorator;
import crazypants.structures.gen.structure.preperation.CompositePreperation;
import crazypants.structures.gen.structure.validator.CompositeSiteValidator;
import crazypants.structures.runtime.CompositeBehaviour;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class StructureTemplate implements IStructureTemplate {
  
  private static final Random RND = new Random(987345098532932115L);
  
  private List<Rotation> rots = new ArrayList<Rotation>();
  
  private List<PositionedComponent> components = new ArrayList<PositionedComponent>();  
  private final CompositePreperation sitePreps = new CompositePreperation();
  private final CompositeSiteValidator siteVals = new CompositeSiteValidator();
  private final CompositeDecorator decorators = new CompositeDecorator();
  private final CompositeBehaviour behaviour = new CompositeBehaviour();
  
  private boolean canSpanChunks = true;
  private boolean generationRequiresLoadedChunks = canSpanChunks;
  
  private final String uid;

  public StructureTemplate(String uid) {
    this(uid, null);
  }
  
  public StructureTemplate(String uid, Collection<PositionedComponent> components) {
    this.uid = uid;
    if(components != null) {
      this.components.addAll(components);
    }        
//    behaviour.add(new VirtualSpawnerBehaviour());
  }
  
  @Override
  public IStructure createInstance() {
    return new Structure(new Point3i(), getRndRotation(), this);
  }

  @Override
  public IStructure createInstance(Rotation rotation) {      
    return new Structure(new Point3i(), rotation, this);
  }

  @Override
  public boolean getCanSpanChunks() {
    return canSpanChunks;
  }

  public void setCanSpanChunks(boolean canSpanChunks) {
    this.canSpanChunks = canSpanChunks;
  }

  @Override
  public boolean getGenerationRequiresLoadedChunks() {
    return generationRequiresLoadedChunks;
  }

  public void setGenerationRequiresLoadedChunks(boolean generationRequiresLoadedChunks) {
    this.generationRequiresLoadedChunks = generationRequiresLoadedChunks;
  }

  @Override
  public List<PositionedComponent> getComponents() {
    return components;
  }

  @Override
  public List<Rotation> getRotations() {
    return rots;
  }

  public List<Rotation> getRots() {
    return rots;
  }

  public void setRotations(List<Rotation> rots) {
    this.rots = rots;
  }
  
  public void addComponent(IStructureComponent st, Point3i offset) {
    if(components == null) {
      components = new ArrayList<PositionedComponent>();
    }
    components.add(new PositionedComponent(st, offset));    
  }

  public void setComponents(List<PositionedComponent> components) {
    this.components = components;
  }

  @Override
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
  
  public void addBehaviour(IBehaviour be) {    
    behaviour.add(be);    
  }

  private Rotation getRndRotation() {        
    if(rots == null || rots.isEmpty()) {
      return Rotation.DEG_0;
    }
    return rots.get(RND.nextInt(rots.size()));    
  }
  
  @Override
  public boolean isValid() {
    return components != null && !components.isEmpty();
  }

  @Override
  public AxisAlignedBB getBounds() {    
    AxisAlignedBB res = null;
    for(PositionedComponent comp : components) {
      AxisAlignedBB bb = comp.getComponent().getBounds();
      Point3i offset = comp.getOffset();      
      bb = bb.getOffsetBoundingBox(offset.x, offset.y, offset.z);
      if(res == null) {
        res = bb;
      } else {
        res = res.func_111270_a(bb);
      }
    }
    return res;
  }

  @Override
  public Point3i getSize() {
    AxisAlignedBB bb = getBounds();    
    return new Point3i((int) Math.abs(bb.maxX - bb.minX), (int) Math.abs(bb.maxY - bb.minY), (int) Math.abs(bb.maxZ - bb.minZ));
  }

  @Override
  public int getSurfaceOffset() {
    //TODO: How to handle this? Just using the first value is a bit dodgy
    return components.get(0).getComponent().getSurfaceOffset();
  }

  @Override
  public void build(IStructure structure, World world, Random random, StructureBoundingBox bounds) {
    sitePreps.prepareLocation(structure, world, random, bounds);
    Point3i orig = structure.getOrigin();
    
    for(PositionedComponent pc : components) {
      Point3i offset = pc.getOffset();
      pc.getComponent().build(world, orig.x + offset.x, orig.y + offset.y, orig.z + offset.z, structure.getRotation(), bounds);
    }
    
    if(random == null) {
      random = RND;
    }
    decorators.decorate(structure, world, random, bounds);
  }

  @Override
  public ISiteValidator getSiteValiditor() {
    return siteVals;
  }

  @Override
  public IBehaviour getBehaviour() {  
    return behaviour;
  }

  @Override
  public Collection<Point3i> getTaggedLocations(String target) {
    List<Point3i> res = new ArrayList<Point3i>();
    for(PositionedComponent pc : components) {
      List<Point3i> r = pc.getComponent().getTaggedLocations(target);
      if(r != null) {
        for(Point3i p : r) {
          Point3i newP = new Point3i(p);
          newP.add(pc.getOffset());
          res.add(newP);
        }
      }
    }
    return res;
  }

}