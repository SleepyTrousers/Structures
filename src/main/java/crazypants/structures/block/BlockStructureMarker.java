package crazypants.structures.block;

import crazypants.structures.EnderStructures;
import crazypants.structures.EnderStructuresTab;
import crazypants.structures.gen.StructureRegister;
import crazypants.structures.gen.structure.StructureComponent;
import crazypants.structures.item.ExportManager;
import crazypants.vec.Point3i;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockStructureMarker extends Block {

  public static final String NAME = "blockStructureMarker";

  public static BlockStructureMarker create() {    
    BlockStructureMarker res = new BlockStructureMarker();
    res.init();
    return res;
  }

  protected BlockStructureMarker() {
    super(Material.rock);
    setHardness(0.5F);
    setUnlocalizedName(NAME);
    setStepSound(Block.soundTypeStone);
    setHarvestLevel("pickaxe", 0);
    setCreativeTab(EnderStructuresTab.tabEnderStructures);
  }

  protected void init() {
    GameRegistry.registerBlock(this, NAME);    
  }
  
  
  

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityPlayer, EnumFacing side, float hitX, float hitY, float hitZ) {
    if(entityPlayer.isSneaking()) {
      return false;
    }

    //have to do it on the server to get TileEntity data
    if(world.isRemote) {
      return true;
    }
    generateAndExport(world, pos, entityPlayer);
    return true;
  }

  public StructureComponent generateAndExport(World world, BlockPos pos, EntityPlayer entityPlayer) {
    StructureComponent st = generateTemplate(ExportManager.instance.getNextExportUid(), world, pos, entityPlayer);
    if(st != null) {
      ExportManager.writeToFile(entityPlayer, st, true);
      StructureRegister.instance.registerStructureComponent(st);
//      StructureRegister.instance.getGenerator(st.getUid(), true);
    }
    return st;
  }
  
  public static StructureComponent generateTemplate(String name, IBlockAccess world, BlockPos pos, EntityPlayer entityPlayer) {
    StructureBounds bb = getStructureBounds(world, pos.getX(), pos.getY(), pos.getZ(), entityPlayer);
    if(bb == null) {      
      return null;
    }
    return new StructureComponent(name, world, bb.bb, bb.surfaceOffset);
  }

  public static StructureBounds getStructureBounds(IBlockAccess world, int x, int y, int z, EntityPlayer entityPlayer) {
    short scanDistance = 100;
    Point3i axis = new Point3i(1, 0, 0);
    Point3i xLoc = findBlock(EnderStructures.blockStructureMarker, world, x, y, z, scanDistance, axis, true);
    axis.set(0, 1, 0);
    Point3i yLoc = findBlock(EnderStructures.blockStructureMarker, world, x, y, z, scanDistance, axis, false);
    axis.set(0, 0, 1);
    Point3i zLoc = findBlock(EnderStructures.blockStructureMarker, world, x, y, z, scanDistance, axis, true);
    
    if(xLoc == null || yLoc == null || zLoc == null) {
      String ax = "" + (xLoc == null ? " x " : "") + (yLoc == null ? " y " : "") + (zLoc == null ? " z " : "");      
      entityPlayer.addChatComponentMessage(new ChatComponentText("No marker/s found along: " + ax));
      return null;
    }
    
    Point3i min = new Point3i(Math.min(x, xLoc.x), Math.min(y, yLoc.y), Math.min(z, zLoc.z));
    //inside the area
    min.x += 1;
    min.y += 1;
    min.z += 1;
    Point3i max = new Point3i(Math.max(x, xLoc.x), Math.max(y, yLoc.y), Math.max(z, zLoc.z));    
    AxisAlignedBB bb = new AxisAlignedBB(min.x, min.y,min.z, max.x,max.y,max.z);  
    
    
    int surfaceOffset = 0;
    axis.set(0, 1, 0);
    yLoc = findBlock(EnderStructures.blockGroundLevelMarker, world, x, y, z, scanDistance, axis, false);
    if(yLoc != null) {
      surfaceOffset = yLoc.y - min.y;
    }
    System.out.println("BlockStructureMarker.getStructureBounds: " + surfaceOffset);
    return new StructureBounds(bb, surfaceOffset);    
  }
  
  private static Point3i findBlock(Block blk, IBlockAccess world, int x, int y, int z, short scanDistance, Point3i axis, boolean bothDirs) {
    int steps = 0;
    
    Point3i tst = new Point3i(x, y, z);
    do {
      tst.add(axis);
      if(world.getBlockState(tst.pos()).getBlock() == blk) {
        return tst;
      }
      steps++;
    } while(steps < scanDistance);
    
    if(bothDirs) {
      axis.scale(-1);
      return findBlock(blk, world, x, y, z, scanDistance, axis, false);
    }    
    return null;
  }

  static class StructureBounds {
    final AxisAlignedBB bb;
    final int surfaceOffset;
    
    public StructureBounds(AxisAlignedBB bb, int surfaceOffset) {    
      this.bb = bb;
      this.surfaceOffset = surfaceOffset;
    }
    
    
  }

}
