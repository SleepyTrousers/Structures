package crazypants.structures.api.gen;

import crazypants.structures.api.util.Point3i;

public class PositionedComponent {

  private final Point3i offset;
  private final IStructureComponent component;
  
  
  public PositionedComponent(IStructureComponent component) {
    this(component, null);
  }
  
  public PositionedComponent(IStructureComponent component, Point3i offset) {
    if(offset == null) {
      this.offset = new Point3i();
    } else {
      this.offset = offset;
    }
    this.component = component;
  }
  
  public Point3i getOffset() {
    return offset;
  }
  
  public IStructureComponent getComponent() {
    return component;
  }
  
  
  
}
