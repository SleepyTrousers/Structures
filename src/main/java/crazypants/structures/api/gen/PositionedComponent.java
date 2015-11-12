package crazypants.structures.api.gen;

import com.google.gson.annotations.Expose;

import crazypants.structures.api.util.Point3i;

public class PositionedComponent {

  @Expose
  private Point3i offset;
  
  @Expose
  private IStructureComponent component;
  
  public PositionedComponent() {
    this(null);
  }
  
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
