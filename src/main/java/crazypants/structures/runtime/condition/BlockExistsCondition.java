package crazypants.structures.runtime.condition;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.ICondition;
import crazypants.structures.api.util.Point3i;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class BlockExistsCondition implements ICondition {

  private Block block;
  private Point3i localCoord;
  private int meta = -1;

  public BlockExistsCondition() {    
  }
  
  public BlockExistsCondition(Block block, Point3i localCoord) {  
   this(block, -1, localCoord);
  }  
  
  public BlockExistsCondition(Block block, int meta, Point3i localCoord) {  
    this.block = block;
    this.localCoord = localCoord;
    this.meta = meta;
  }

  public Block getBlock() {
    return block;
  }

  public void setBlock(Block block) {
    this.block = block;
  }

  public Point3i getLocalCoord() {
    return localCoord;
  }

  public void setLocalCoord(Point3i localCoord) {
    this.localCoord = localCoord;
  }

  public int getMeta() {
    return meta;
  }

  public void setMeta(int meta) {
    this.meta = meta;
  }

  @Override
  public boolean isConditionMet(World world, IStructure structure) {
    if(block == null || localCoord == null) {
      return false;
    }

    Point3i coord = structure.transformLocalToWorld(localCoord);

    Block blk = world.getBlock(coord.x, coord.y, coord.z);
    if(blk != block) {
      return false;
    }

    if(meta > 0) {
      int m = world.getBlockMetadata(coord.x, coord.y, coord.z);
      if(meta != m) {
        return false;
      }
    }
    return true;
  }

  @Override
  public ICondition createPerStructureInstance(World world, IStructure structure) {
    //This is a stateless check so no need to create a new instance
    return this;
  }

}
