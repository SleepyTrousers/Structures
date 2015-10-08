package crazypants.structures.creator.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.structures.creator.CreatorUtil;
import crazypants.structures.creator.EnderStructuresCreator;
import crazypants.structures.creator.EnderStructuresCreatorTab;
import crazypants.structures.creator.item.ExportManager;
import crazypants.structures.gen.StructureRegister;
import crazypants.structures.gen.structure.StructureComponent;
import crazypants.vec.Point3i;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

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
    setBlockName(NAME);
    setStepSound(Block.soundTypeStone);
    setHarvestLevel("pickaxe", 0);
    setCreativeTab(EnderStructuresCreatorTab.tabEnderStructures);
  }

  protected void init() {
    GameRegistry.registerBlock(this, NAME);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iIconRegister) {
    blockIcon = iIconRegister.registerIcon(EnderStructuresCreator.MODID.toLowerCase() + ":" + NAME);
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float par7, float par8, float par9) {
    if(entityPlayer.isSneaking()) {
      return false;
    }

    //have to do it on the server to get TileEntity data
    if(world.isRemote) {
      return true;
    }
    generateAndExport(world, x, y, z, entityPlayer);
    return true;
  }

  public StructureComponent generateAndExport(World world, int x, int y, int z, EntityPlayer entityPlayer) {
    StructureComponent st = createComponentWithinMarkers(ExportManager.instance.getNextExportUid(), world, x, y, z, entityPlayer);
    if(st != null) {
      ExportManager.writeToFile(entityPlayer, st, true);
      StructureRegister.instance.registerStructureComponent(st);
      //      StructureRegister.instance.getGenerator(st.getUid(), true);
    }
    return st;
  }

  public static StructureComponent createComponentWithinMarkers(String name, IBlockAccess world, int x, int y, int z, EntityPlayer entityPlayer) {
    StructureBounds bb = getStructureBounds(world, x, y, z, entityPlayer);
    if(bb == null) {
      return null;
    }
    return CreatorUtil.createComponent(name, world, bb.bb, bb.surfaceOffset);
  }

  public static StructureBounds getStructureBounds(IBlockAccess world, int x, int y, int z, EntityPlayer entityPlayer) {
    short scanDistance = 100;
    Point3i axis = new Point3i(1, 0, 0);
    Point3i xLoc = CreatorUtil.findBlockOnAxis(EnderStructuresCreator.blockStructureMarker, world, x, y, z, scanDistance, axis, true);
    axis.set(0, 1, 0);
    Point3i yLoc = CreatorUtil.findBlockOnAxis(EnderStructuresCreator.blockStructureMarker, world, x, y, z, scanDistance, axis, false);
    axis.set(0, 0, 1);
    Point3i zLoc = CreatorUtil.findBlockOnAxis(EnderStructuresCreator.blockStructureMarker, world, x, y, z, scanDistance, axis, true);

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
    AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);

    int surfaceOffset = 0;
    axis.set(0, 1, 0);
    yLoc = CreatorUtil.findBlockOnAxis(EnderStructuresCreator.blockGroundLevelMarker, world, x, y, z, scanDistance, axis, false);
    if(yLoc != null) {
      surfaceOffset = yLoc.y - min.y;
    }
    System.out.println("BlockStructureMarker.getStructureBounds: " + surfaceOffset);
    return new StructureBounds(bb, surfaceOffset);
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
