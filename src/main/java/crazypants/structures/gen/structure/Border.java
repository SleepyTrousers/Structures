package crazypants.structures.gen.structure;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.util.EnumFacing;

public class Border {

  //private final Map<ForgeDirection, Integer> border = new HashMap<ForgeDirection, Integer>();
  
  private final Map<EnumFacing, Integer> border = new EnumMap<EnumFacing, Integer>(EnumFacing.class);
  

  public void setBorderXZ(int size) {
    setBorder(size, size, size, size);
  }

  public void setBorderY(int down, int up) {
    border.put(EnumFacing.DOWN, down);
    border.put(EnumFacing.UP, up);
  }

  public void setBorder(int north, int south, int east, int west) {
    border.put(EnumFacing.EAST, east);
    border.put(EnumFacing.WEST, west);
    border.put(EnumFacing.NORTH, north);
    border.put(EnumFacing.SOUTH, south);
  }

  public void setBorder(int north, int south, int east, int west, int up, int down) {
    border.put(EnumFacing.DOWN, down);
    border.put(EnumFacing.UP, up);
    border.put(EnumFacing.EAST, east);
    border.put(EnumFacing.WEST, west);
    border.put(EnumFacing.NORTH, north);
    border.put(EnumFacing.SOUTH, south);
  }

  public void set(EnumFacing dir, int val) {
    border.put(dir, val);
  }
  
  public int get(EnumFacing dir) {
    Integer res = border.get(dir);
    if(res == null) {
      return 0;
    }
    return res;
  }

}
