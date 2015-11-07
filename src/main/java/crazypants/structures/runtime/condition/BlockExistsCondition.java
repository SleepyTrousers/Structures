package crazypants.structures.runtime.condition;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.util.Point3i;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class BlockExistsCondition extends PositionedCondition {

  private Block block;  
  private int meta = -1;

  public BlockExistsCondition() {    
  }
  
  @Override
  protected boolean doIsConditonMet(World world, IStructure structure, Point3i worldPos) {    
    Block blk = world.getBlock(worldPos.x, worldPos.y, worldPos.z);
    if(blk != block) {
      return false;
    }
    if(meta > 0) {
      int m = world.getBlockMetadata(worldPos.x, worldPos.y, worldPos.z);
      if(meta != m) {
        return false;
      }
    }
    return true;
  }
  
  public Block getBlock() {
    return block;
  }

  public void setBlock(Block block) {
    this.block = block;
  }

  public int getMeta() {
    return meta;
  }

  public void setMeta(int meta) {
    this.meta = meta;
  }

  

}
