package crazypants.structures.gen.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.google.gson.annotations.Expose;

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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class StructureTemplate implements IStructureTemplate {

  private static final Random RND = new Random(987345098532932115L);

  @Expose
  private List<Rotation> rotations = new ArrayList<Rotation>();

  @Expose
  private List<PositionedComponent> components = new ArrayList<PositionedComponent>();

  @Expose
  private ISitePreperation sitePreperation;

  @Expose
  private ISiteValidator siteValidator;

  @Expose
  private IDecorator decorator;

  @Expose
  private IBehaviour behaviour;

  @Expose
  private boolean canSpanChunks = true;

  @Expose
  private boolean generationRequiresLoadedChunks = canSpanChunks;

  private String uid;

  public StructureTemplate() {
    this(null);
  }

  public StructureTemplate(String uid) {
    this(uid, null);
  }

  public StructureTemplate(String uid, Collection<PositionedComponent> components) {
    this.uid = uid;
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
    return rotations;
  }

  public List<Rotation> getRots() {
    return rotations;
  }

  public void setRotations(List<Rotation> rots) {
    this.rotations = rots;
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

  public void setUid(String uid) {
    this.uid = uid;
  }

  private Rotation getRndRotation() {
    if(rotations == null || rotations.isEmpty()) {
      return Rotation.DEG_0;
    }
    return rotations.get(RND.nextInt(rotations.size()));
  }

  @Override
  public boolean isValid() {
    return components != null && !components.isEmpty();
  }

  @Override
  public AxisAlignedBB getBounds() {
    AxisAlignedBB res = null;
    for (PositionedComponent comp : components) {
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
    if(sitePreperation != null) {
      sitePreperation.prepareLocation(structure, world, random, bounds);
    }
    Point3i orig = structure.getOrigin();
    
    for (PositionedComponent pc : components) {
      Point3i offset = pc.getOffset();
      pc.getComponent().build(world, orig.x + offset.x, orig.y + offset.y, orig.z + offset.z, structure.getRotation(), bounds);
    }

    if(random == null) {
      random = RND;
    }
    if(decorator != null) {
      decorator.decorate(structure, world, random, bounds);
    }
  }

  @Override
  public ISiteValidator getSiteValiditor() {
    return siteValidator;
  }

  public void setSiteValidator(ISiteValidator sv) {
    siteValidator = sv;
  }

  public ISiteValidator getSiteValidator() {
    return siteValidator;
  }
  
  @Override
  public ISitePreperation getSitePreperation() {
    return sitePreperation;
  }

  @Override
  public void setSitePreperation(ISitePreperation sitePreperation) {
    this.sitePreperation = sitePreperation;
  }
  
  public IDecorator getDecorator() {
    return decorator;
  }

  public void setDecorator(IDecorator decorator) {
    this.decorator = decorator;
  }

  public void setBehaviour(IBehaviour behaviour) {
    this.behaviour = behaviour;
  }

  @Override
  public IBehaviour getBehaviour() {
    return behaviour;
  }

  @Override
  public Collection<Point3i> getTaggedLocations(String target) {
    List<Point3i> res = new ArrayList<Point3i>();
    for (PositionedComponent pc : components) {
      Collection<Point3i> r = pc.getComponent().getTaggedLocations(target);
      if(r != null) {
        for (Point3i p : r) {
          Point3i newP = new Point3i(p);
          newP.add(pc.getOffset());
          res.add(newP);
        }
      }
    }
    return res;
  }

  @Override
  public String toString() {
    return "StructureTemplate [uid=" + uid + "]";
  }

}