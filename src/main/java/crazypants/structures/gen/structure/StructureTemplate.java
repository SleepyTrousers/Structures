package crazypants.structures.gen.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import crazypants.structures.Log;
import crazypants.structures.api.gen.IDecorator;
import crazypants.structures.api.gen.ISitePreperation;
import crazypants.structures.api.gen.ISiteValidator;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.gen.IStructureComponent;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.util.ChunkBounds;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.gen.structure.decorator.CompositeDecorator;
import crazypants.structures.gen.structure.preperation.CompositePreperation;
import crazypants.structures.gen.structure.validator.CompositeSiteValidator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class StructureTemplate implements IStructureTemplate {
  
  private static final Random RND = new Random(987345098532932115L);
  
  private List<Rotation> rots = new ArrayList<Rotation>();
  
  //TODO: Need offsets for components
  private List<IStructureComponent> components = new ArrayList<IStructureComponent>();  
  private final CompositePreperation sitePreps = new CompositePreperation();
  private final CompositeSiteValidator siteVals = new CompositeSiteValidator();
  private final CompositeDecorator decorators = new CompositeDecorator();
  
  private boolean canSpanChunks = true;
  private boolean generationRequiresLoadedChunks = canSpanChunks;
  
  private final String uid;

  public StructureTemplate(String uid) {
    this.uid = uid;
  }
  
  public StructureTemplate(String uid, Collection<IStructureComponent> components) {
    this(uid);
    if(components != null) {
      this.components.addAll(components);
    }        
  }
  
  @Override
  public IStructure createInstance() {
    return new Structure(new Point3i(), getRndRotation(), this);
  }

  @Override
  public IStructure createInstance(Rotation rotation) {  
    if(!rots.contains(rotation)) {
      Log.warn("StructureTemplate.createInstance: Rotation " + rotation + " not supported by template " + uid);
      return null;
    }
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
  public List<IStructureComponent> getComponents() {
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
  
  public void addComponent(IStructureComponent st) {
    if(components == null) {
      components = new ArrayList<IStructureComponent>();
    }
    components.add(st);    
  }

  public void setComponents(List<IStructureComponent> components) {
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
    return components.get(0).getBounds();
  }

  @Override
  public Point3i getSize() {
    return components.get(0).getSize();
  }

  @Override
  public int getSurfaceOffset() {
    return components.get(0).getSurfaceOffset();
  }

  @Override
  public void build(IStructure structure, World world, Random random, ChunkBounds bounds) {
    sitePreps.prepareLocation(structure, world, random, bounds);
    Point3i orig = structure.getOrigin();
    components.get(0).build(world, orig.x, orig.y, orig.z, structure.getRotation(), bounds);   
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
  public Collection<Point3i> getTaggedLocations(String target) {
    //TODO: Need to handle offset for components
    List<Point3i> res = new ArrayList<Point3i>();
    for(IStructureComponent comp : components) {
      List<Point3i> r = comp.getTaggedLocations(target);
      if(r != null) {
        res.addAll(r);
      }
    }
    return res;
  }

}