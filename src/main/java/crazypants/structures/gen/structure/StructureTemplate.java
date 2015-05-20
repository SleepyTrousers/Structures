package crazypants.structures.gen.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import crazypants.structures.gen.structure.Structure.Rotation;
import crazypants.structures.gen.structure.preperation.CompositePreperation;
import crazypants.structures.gen.structure.preperation.ISitePreperation;
import crazypants.vec.Point3i;

public class StructureTemplate {
  
  private static final Random RND = new Random();
  
  private List<StructureComponent> components = new ArrayList<StructureComponent>();
  private List<Rotation> rots = new ArrayList<Structure.Rotation>();
  private final CompositePreperation sitePreps = new CompositePreperation();
  
  private boolean canSpanChunks = true;
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

  public boolean isCanSpanChunks() {
    return canSpanChunks;
  }

  public void setCanSpanChunks(boolean canSpanChunks) {
    this.canSpanChunks = canSpanChunks;
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

  public Structure createInstance(StructureGenerator gen) {
    return new Structure(new Point3i(), getRndRotation(), canSpanChunks, components.get(0));
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

}