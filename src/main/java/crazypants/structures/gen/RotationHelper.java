package crazypants.structures.gen;

import static net.minecraft.util.EnumFacing.DOWN;
import static net.minecraft.util.EnumFacing.UP;

import crazypants.structures.api.util.Rotation;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

public class RotationHelper {

  public static int rotateMetadata(Block block, int meta, Rotation rot) {
    IBlockState state = block.getStateFromMeta(meta);
    for (IProperty<?> prop : state.getProperties().keySet()) {
      if (prop instanceof PropertyDirection) {
        PropertyDirection dir = (PropertyDirection) prop;
        EnumFacing val = state.getValue(dir);
        if (val != DOWN && val != UP) {
          for (int i = 0; i < rot.ordinal(); i++) {
            val = val.rotateY();
          }
          state = state.withProperty(dir, val);
        }        
      }
    }
    return state.getBlock().getMetaFromState(state);   
  }

  
}
