package crazypants.structures.api.util;

import net.minecraft.util.AxisAlignedBB;

public enum Rotation {
  DEG_0(0),
  DEG_90(90),
  DEG_180(180),
  DEG_270(270);

  final int val;

  private Rotation(int val) {
    this.val = val;
  }

  //Keeps all point +'ve
  public void rotate(Point3i bc, int maxX, int maxZ) {
    if(this == Rotation.DEG_0) {
      return;
    }
    if(this == Rotation.DEG_90) {
      bc.set(maxZ - bc.z, bc.y, bc.x);
    } else if(this == Rotation.DEG_180) {
      bc.set(maxX - bc.x, bc.y, maxZ - bc.z);
    } else if(this == Rotation.DEG_270) {
      bc.set(bc.z, bc.y, maxX - bc.x);
    }
  }
  
  public void rotate(Point3i bc) {
    if(this == Rotation.DEG_0) {
      return;
    }
    if(this == Rotation.DEG_90) {
      bc.set(-bc.z, bc.y, bc.x);
    } else if(this == Rotation.DEG_180) {
      bc.set(-bc.x, bc.y, -bc.z);
    } else if(this == Rotation.DEG_270) {
      bc.set(bc.z, bc.y, -bc.x);
    }
  }

  //must have an x/z origin of o 
  public AxisAlignedBB rotate(AxisAlignedBB bb) {
    if(this == Rotation.DEG_0 || this == Rotation.DEG_180) {
      return bb;
    }
    Point3i sz = VecUtil.size(bb);
    return AxisAlignedBB.getBoundingBox(0, 0, 0, sz.z, sz.y, sz.x);
  }

  public Rotation next() {
    int ord = ordinal() + 1;
    if(ord > values().length - 1) {
      ord = 0;
    }
    return values()[ord];
  }

  public static Rotation get(int deg) {
    for (Rotation r : values()) {
      if(r.val == deg) {
        return r;
      }
    }
    return null;
  }
  
  public int getRotationDegrees() {
    return val;
  }
}