package crazypants.structures.gen.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import crazypants.structures.gen.ChunkBounds;
import crazypants.structures.gen.structure.Structure.Rotation;
import crazypants.structures.gen.structure.preperation.CompositePreperation;
import crazypants.structures.gen.structure.preperation.ISitePreperation;
import crazypants.structures.gen.structure.validator.CompositeSiteValidator;
import crazypants.structures.gen.structure.validator.ISiteValidator;
import crazypants.vec.Point3i;

public class StructureTemplate {
  
  private static final Random RND = new Random();
  
  private List<StructureComponent> components = new ArrayList<StructureComponent>();
  private List<Rotation> rots = new ArrayList<Structure.Rotation>();
  private final CompositePreperation sitePreps = new CompositePreperation();
  private final CompositeSiteValidator siteVals = new CompositeSiteValidator();
  
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

  public Structure createInstance(StructureGenerator gen) {
    return new Structure(new Point3i(), getRndRotation(), this);
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

  public void build(Structure structure, World world, ChunkBounds bounds) {
    sitePreps.prepareLocation(structure, world, RND, bounds);
    Point3i orig = structure.getOrigin();
    components.get(0).build(world, orig.x, orig.y, orig.z, structure.getRotation(), bounds);   
  }

  public ISiteValidator getSiteValiditor() {
    return siteVals;
  }

}