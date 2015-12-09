package crazypants.structures.runtime;

import com.google.gson.annotations.Expose;

import crazypants.structures.AbstractTyped;
import crazypants.structures.StructureUtils;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.util.Point3i;

public class PositionedType extends AbstractTyped {

  @Expose
  private Point3i position;

  @Expose
  private String taggedPosition;

  public PositionedType(String type) {
    super(type);
  }

  public PositionedType(PositionedType other) {
    super(other.getType());
    this.position = other.position == null ? null : new Point3i(other.position);
    this.taggedPosition = other.taggedPosition;
  }

  public Point3i getWorldPosition(IStructure structure) {
    if(structure == null) {
      return null;
    }
    return getWorldPosition(structure, structure.getOrigin());
  }

  public Point3i getWorldPosition(IStructure structure, Point3i def) {

    //tagged position and is applied without modification
    //a local position is added to the default value if possible
    //    System.out.println("PositionedType.getWorldPosition: " + def);
    Point3i result = def == null ? null : new Point3i(def);
    Point3i taggedPos = StructureUtils.getTaggedLocationInStructureCoords(taggedPosition, structure, null);
    if(taggedPos != null) {
      result = structure.transformStructureLocalToWorld(taggedPos);
    } else if(position != null) {
      if(result == null) {
        result = structure.transformStructureLocalToWorld(position);
      } else {
        result.add(position);
      }
    }
    return result;

  }

  public Point3i getLocalPosition(IStructure structure, Point3i def) {
    Point3i res = StructureUtils.getTaggedLocationInStructureCoords(taggedPosition, structure, position);
    if(res != null) {
      return res;
    }
    return def;
  }

  public Point3i getPosition() {
    return position;
  }

  public void setPosition(Point3i position) {
    this.position = position;
  }

  public String getTaggedPosition() {
    return taggedPosition;
  }

  public void setTaggedPosition(String taggedPosition) {
    this.taggedPosition = taggedPosition;
  }

}
