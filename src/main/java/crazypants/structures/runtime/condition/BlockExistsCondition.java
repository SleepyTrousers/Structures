package crazypants.structures.runtime.condition;

import com.google.gson.annotations.Expose;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.util.Point3i;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockExistsCondition extends PositionedCondition {

  @Expose
  private IBlockState blockState;
    
  public BlockExistsCondition() { 
    super("BlockExists");
  }
  
  @Override
  protected boolean doIsConditonMet(World world, IStructure structure, Point3i worldPos) {  
    
    IBlockState blk = world.getBlockState(new BlockPos(worldPos.x, worldPos.y, worldPos.z));
    if(blockState == null){
      return blk.getBlock().isAir(world, worldPos.getBlockPos());
    }
    
    if(blk.getBlock() != blockState.getBlock()) {
      return false;
    }
    int meta = blk.getBlock().getMetaFromState(blk);    
    int m = blockState.getBlock().getMetaFromState(blockState);
    if(meta != m) {
      return false;
    }    
    return true;
  }
  
  public IBlockState getBlockState() {
    return blockState;
  }

  public void setBlockState(IBlockState block) {
    this.blockState = block;
  }
  
}
