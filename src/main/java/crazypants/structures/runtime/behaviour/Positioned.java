package crazypants.structures.runtime.behaviour;

import crazypants.structures.StructureUtils;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.util.Point3i;

public class Positioned {

  private Point3i position;
  private String taggedPosition;
  
  public Positioned() {    
  }
  
  public Positioned(Positioned other) {
    if(other == null) {
      return;
    }
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
    Point3i worldPos = def;
    Point3i sLocalPos = StructureUtils.getTaggedLocationInStructureCoords(taggedPosition, structure, position);
    if(sLocalPos != null) {
      worldPos = structure.transformStructureLocalToWorld(sLocalPos);
    }
    return worldPos;    
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
