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
      sb.append(dir.toString());
      sb.append("=");
      sb.append(get(dir));
      sb.append(" ");
    }
    sb.append("]");
    return sb.toString();
  }
  
  

}
