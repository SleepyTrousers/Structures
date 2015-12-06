package crazypants.structures.gen.structure;

import java.util.HashMap;
import java.util.Map;

import crazypants.structures.api.gen.IStructure;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.util.ForgeDirection;

public class Border {
  
  private Map<ForgeDirection, Integer> border = new HashMap<ForgeDirection, Integer>();

  public Border() {
    for(ForgeDirection dir : ForgeDirection.values()) {
      set(dir, 0);
    }
  }
  
  public Border(int north, int south, int east, int west, int up, int down) {
    border.put(ForgeDirection.DOWN, down);
    border.put(ForgeDirection.UP, up);
    border.put(ForgeDirection.EAST, east);
    border.put(ForgeDirection.WEST, west);
    border.put(ForgeDirection.NORTH, north);
    border.put(ForgeDirection.SOUTH, south);
  }
  
  public void setBorderXZ(int size) {
    setBorder(size, size, size, size);
  }

  public void setBorderY(int down, int up) {
    border.put(ForgeDirection.DOWN, down);
    border.put(ForgeDirection.UP, up);
  }

  public void setBorder(int north, int south, int east, int west) {
    border.put(ForgeDirection.EAST, east);
    border.put(ForgeDirection.WEST, west);
    border.put(ForgeDirection.NORTH, north);
    border.put(ForgeDirection.SOUTH, south);
  }

  public void setBorder(int north, int south, int east, int west, int up, int down) {
    border.put(ForgeDirection.DOWN, down);
    border.put(ForgeDirection.UP, up);
    border.put(ForgeDirection.EAST, east);
    border.put(ForgeDirection.WEST, west);
    border.put(ForgeDirection.NORTH, north);
    border.put(ForgeDirection.SOUTH, south);
  }

  public StructureBoundingBox getBounds(IStructure structure) {
    //TODO: Rotations? 
    AxisAlignedBB bb = structure.getBounds();
    int minX = (int) bb.minX - border.get(ForgeDirection.WEST);
    int maxX = (int) bb.maxX + border.get(ForgeDirection.EAST);
    int minY = (int) bb.minY - border.get(ForgeDirection.DOWN);
    int maxY = (int) bb.maxY + border.get(ForgeDirection.UP);
    int minZ = (int) bb.minZ - border.get(ForgeDirection.NORTH);
    int maxZ = (int) bb.maxZ + border.get(ForgeDirection.SOUTH);
    return new StructureBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);        
  }
  
  public void set(ForgeDirection dir, int val) {
    border.put(dir, val);
  }
  
  public int get(ForgeDirection dir) {
    Integer res = border.get(dir);
    if(res == null) {
      return 0;
    }
    return res;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[ ");
    for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      sb.append(dir.toString().substring(0,1));
      sb.append("=");
      sb.append(get(dir));
      sb.append(" ");
    }
    sb.append("]");
    return sb.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((border == null) ? 0 : border.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj)
      return true;
    if(obj == null)
      return false;
    if(getClass() != obj.getClass())
      return false;
    Border other = (Border) obj;
    if(border == null) {
      if(other.border != null)
        return false;
    } else if(!border.equals(other.border))
      return false;
    return true;
  }
  
  

}
