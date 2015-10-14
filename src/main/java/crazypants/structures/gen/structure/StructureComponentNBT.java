package crazypants.structures.gen.structure;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.structures.Log;
import crazypants.structures.api.gen.IStructureComponent;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.api.util.StructureUtil;
import crazypants.structures.api.util.VecUtil;
import crazypants.structures.gen.RotationHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockTorch;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class StructureComponentNBT implements IStructureComponent {

  private final AxisAlignedBB bb;

  private final Point3i size;

  private final Map<StructureBlock, List<Point3i>> blocks = new HashMap<StructureBlock, List<Point3i>>();
  private final Map<String, List<Point3i>> taggedLocations = new HashMap<String, List<Point3i>>();

  private final String uid;
  private final int surfaceOffset;

  private final StructureBlock fillerBlock;
  private final StructureBlock topBlock;

  //  public StructureComponent(String uid, IBlockAccess world, AxisAlignedBB worldBnds, int surfaceOffset) {
  //
  //    this.uid = uid;
  //
  //    createComponent(world, worldBnds, surfaceOffset);
  //
  //  }

  public StructureComponentNBT(AxisAlignedBB bb, Point3i size, String uid, int surfaceOffset, StructureBlock fillerBlock, StructureBlock topBlock) {
    this.bb = bb;
    this.size = size;
    this.uid = uid;
    this.surfaceOffset = surfaceOffset;
    this.fillerBlock = fillerBlock;
    this.topBlock = topBlock;
  }

  public StructureComponentNBT(InputStream is) throws IOException {
    this(CompressedStreamTools.read(new DataInputStream(is)));
  }

  public StructureComponentNBT(NBTTagCompound root) throws IOException {
    uid = root.getString("uid");

    NBTTagList dataList = (NBTTagList) root.getTag("data");
    for (int i = 0; i < dataList.tagCount(); i++) {
      NBTTagCompound entryTag = dataList.getCompoundTagAt(i);
      NBTTagCompound blockTag = entryTag.getCompoundTag("block");
      StructureBlock sb = new StructureBlock(blockTag);
      List<Point3i> coords = readCoords(entryTag);
      blocks.put(sb, coords);
    }

    bb = AxisAlignedBB.getBoundingBox(root.getInteger("minX"), root.getInteger("minY"), root.getInteger("minZ"), root.getInteger("maxX"),
        root.getInteger("maxY"), root.getInteger("maxZ"));

    size = new Point3i((int) Math.abs(bb.maxX - bb.minX), (int) Math.abs(bb.maxY - bb.minY), (int) Math.abs(bb.maxZ - bb.minZ));

    surfaceOffset = root.getInteger("surfaceOffset");
    if(root.hasKey("fillerBlock")) {
      fillerBlock = new StructureBlock(root.getCompoundTag("fillerBlock"));
      //enable == comparison when building
      if(blocks.containsKey(fillerBlock)) {
        blocks.put(fillerBlock, blocks.get(fillerBlock));
      }
    } else {
      fillerBlock = null;
    }
    if(root.hasKey("topBlock")) {
      topBlock = new StructureBlock(root.getCompoundTag("topBlock"));
      //enable == comparison when building
      if(blocks.containsKey(topBlock)) {
        blocks.put(topBlock, blocks.get(topBlock));
      }
    } else {
      topBlock = null;
    }

    if(root.hasKey("taggedLocations")) {
      NBTTagList locs = (NBTTagList) root.getTag("taggedLocations");
      if(locs != null) {
        for (int i = 0; i < locs.tagCount(); i++) {
          NBTTagCompound tag = locs.getCompoundTagAt(i);
          String tagStr = tag.getString("tag");
          if(tagStr != null && !tagStr.trim().isEmpty()) {
            byte[] coordData = tag.getByteArray("coords");
            int numCoords = tag.getInteger("numCoords");
            if(coordData != null && numCoords > 0) {
              List<Point3i> points = new ArrayList<Point3i>();
              readPoints(points, coordData, numCoords);
              taggedLocations.put(tagStr, points);
            }
          }
        }
      }
    }

    if(uid == null || bb == null || blocks.isEmpty()) {
      throw new IOException("Invalid NBT");
    }
  }

  public void writeToNBT(NBTTagCompound root) {

    root.setString("uid", uid);

    root.setInteger("minX", (int) bb.minX);
    root.setInteger("minY", (int) bb.minY);
    root.setInteger("minZ", (int) bb.minZ);
    root.setInteger("maxX", (int) bb.maxX);
    root.setInteger("maxY", (int) bb.maxY);
    root.setInteger("maxZ", (int) bb.maxZ);
    root.setInteger("surfaceOffset", surfaceOffset);

    if(fillerBlock != null) {
      root.setTag("fillerBlock", fillerBlock.asNbt());
    }
    if(topBlock != null) {
      root.setTag("topBlock", topBlock.asNbt());
    }

    if(taggedLocations != null && !taggedLocations.isEmpty()) {
      NBTTagList locationsList = new NBTTagList();
      for (Entry<String, List<Point3i>> e : taggedLocations.entrySet()) {
        if(e.getValue() != null && !e.getValue().isEmpty()) {
          NBTTagCompound loc = new NBTTagCompound();
          loc.setString("tag", e.getKey());
          loc.setInteger("numCoords", e.getValue().size());
          loc.setByteArray("coords", writeToByteArray(e.getValue()));
          locationsList.appendTag(loc);
        }
      }
      root.setTag("taggedLocations", locationsList);
    }

    NBTTagList entryList = new NBTTagList();
    root.setTag("data", entryList);

    for (Entry<StructureBlock, List<Point3i>> entry : blocks.entrySet()) {

      NBTTagCompound entryTag = new NBTTagCompound();
      entryTag.setTag("block", entry.getKey().asNbt());
      writeCoords(entry, entryTag);
      entryList.appendTag(entryTag);
    }

  }

  private void writeCoords(Entry<StructureBlock, List<Point3i>> entry, NBTTagCompound entryTag) {
    byte[] bytes = writeToByteArray(entry.getValue());
    entryTag.setByteArray("coords", bytes);
    entryTag.setInteger("numCoords", entry.getValue().size());
  }

  private byte[] writeToByteArray(Collection<Point3i> coords) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      for (Point3i coord : coords) {
        writePoint(dos, coord);
      }
    } catch (IOException e) {
      Log.error("StructureTemplate: Ccould not write coords: " + e);
    }
    byte[] bytes = bos.toByteArray();
    return bytes;
  }

  private List<Point3i> readCoords(NBTTagCompound entryTag) {
    byte[] bytes = entryTag.getByteArray("coords");
    int numCoords = entryTag.getInteger("numCoords");
    List<Point3i> coords = new ArrayList<Point3i>(numCoords);
    readPoints(coords, bytes, numCoords);
    return coords;
  }

  private void readPoints(List<Point3i> readInto, byte[] readFrom, int numCoords) {
    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(readFrom));
    try {
      for (int i = 0; i < numCoords; i++) {
        readInto.add(readPoint(dis));
      }
    } catch (IOException e) {
      Log.error("StructureTemplate: Ccould not read coords: " + e);
    }
  }

  private Point3i readPoint(DataInputStream dis) throws IOException {
    return new Point3i(dis.readShort(), dis.readShort(), dis.readShort());
  }

  private void writePoint(DataOutputStream dos, Point3i coord) throws IOException {
    dos.writeShort((short) coord.x);
    dos.writeShort((short) coord.y);
    dos.writeShort((short) coord.z);
  }

  public void write(OutputStream os) throws IOException {
    NBTTagCompound root = new NBTTagCompound();
    writeToNBT(root);
    CompressedStreamTools.write(root, new DataOutputStream(os));
  }

  @Override
  public String getUid() {
    return uid;
  }

  @Override
  public int getSurfaceOffset() {
    return surfaceOffset;
  }

  @Override
  public void build(World world, int x, int y, int z, Rotation rot, StructureBoundingBox genBounds) {

    if(rot == null) {
      rot = Rotation.DEG_0;
    }

    Block fillBlk = null;
    Block surfBlk = null;
    BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
    if(biome != null) {
      fillBlk = biome.fillerBlock;
      surfBlk = biome.topBlock;
    }

    List<Entry<StructureBlock, List<Point3i>>> defered = new ArrayList<Map.Entry<StructureBlock, List<Point3i>>>();

    Map<StructureBlock, List<Point3i>> blks = getBlocks();
    for (Entry<StructureBlock, List<Point3i>> entry : blks.entrySet()) {

      StructureBlock sb = entry.getKey();
      List<Point3i> coords = entry.getValue();
      if(fillBlk != null && sb == fillerBlock) {
        fillBlocks(world, x, y, z, rot, genBounds, coords, fillBlk);
      } else if(surfBlk != null && sb == topBlock) {
        fillBlocks(world, x, y, z, rot, genBounds, coords, surfBlk);
      } else {
        if(isDefered(sb)) {
          defered.add(entry);
        } else {
          placeBlocks(world, x, y, z, rot, genBounds, sb, coords);
        }
      }
    }

    for (Entry<StructureBlock, List<Point3i>> entry : defered) {
      StructureBlock sb = entry.getKey();
      List<Point3i> coords = entry.getValue();
      placeBlocks(world, x, y, z, rot, genBounds, sb, coords);
    }

  }

  private boolean isDefered(StructureBlock sb) {
    Block block = GameRegistry.findBlock(sb.getModId(), sb.getBlockName());
    if(block == null) {
      return false;
    }
    return block instanceof BlockTorch || block instanceof BlockSand || block instanceof BlockGravel || block instanceof BlockAnvil;
  }

  private void fillBlocks(World world, int x, int y, int z, Rotation rot, StructureBoundingBox genBounds, List<Point3i> coords, Block filler) {
    for (Point3i coord : coords) {
      Point3i bc = VecUtil.transformStructureCoodToWorld(x, y, z, rot, size, coord);
      if((genBounds == null || genBounds.isVecInside(bc.x, bc.y, bc.z))
          && StructureUtil.isIgnoredAsSurface(world, x, z, y, world.getBlock(x, y, z), true, false)) {
        world.setBlock(bc.x, bc.y, bc.z, filler, 0, 2);
      }
    }
    return;
  }

  private void placeBlocks(World world, int x, int y, int z, Rotation rot, StructureBoundingBox genBounds, StructureBlock sb, List<Point3i> coords) {

    Block block = GameRegistry.findBlock(sb.getModId(), sb.getBlockName());
    if(block == null) {
      Log.error("Could not find block " + sb.getModId() + ":" + sb.getBlockName() + " when generating structure: " + uid);
    } else {

      for (Point3i coord : coords) {
        Point3i bc = VecUtil.transformStructureCoodToWorld(x, y, z, rot, size, coord);

        if(genBounds == null || genBounds.isVecInside(bc.x, bc.y, bc.z)) {

          int meta = RotationHelper.rotateMetadata(block, sb.getMetaData(), rot);
          world.setBlock(bc.x, bc.y, bc.z, block, meta, 2);

          if(sb.getTileEntity() != null) {
            TileEntity te = TileEntity.createAndLoadEntity(sb.getTileEntity());
            if(te != null) {
              te.xCoord = bc.x;
              te.yCoord = bc.y;
              te.zCoord = bc.z;
              world.setTileEntity(bc.x, bc.y, bc.z, te);
            }
          }
          //Chest will change the meta on block placed, so need to set it back
          if(world.getBlockMetadata(bc.x, bc.y, bc.z) != meta) {
            world.setBlockMetadataWithNotify(bc.x, bc.y, bc.z, meta, 3);
          }
          //          //Chest will change the meta on block placed, so need to set it back
          //          if(world.getBlockMetadata(bc.x, bc.y, bc.z) != sb.getMetaData()) {
          //            world.setBlockMetadataWithNotify(bc.x, bc.y, bc.z, sb.getMetaData(), 3);
          //          }
          //          for (int i = 0; i < rot.ordinal(); i++) {
          //            block.rotateBlock(world, bc.x, bc.y, bc.z, ForgeDirection.UP);
          //          }
        }
      }
    }
  }

  @Override
  public AxisAlignedBB getBounds() {
    return bb;
  }

  @Override
  public Point3i getSize() {
    return size;
  }

  private Map<StructureBlock, List<Point3i>> getBlocks() {
    return blocks;
  }

  public void addBlock(StructureBlock block, short x, short y, short z) {
    if(!blocks.containsKey(block)) {
      blocks.put(block, new ArrayList<Point3i>());
    }
    blocks.get(block).add(new Point3i(x, y, z));
  }

  public void addTagForLocation(String tag, Point3i loc) {
    if(tag == null || loc == null) {
      return;
    }
    List<Point3i> res = taggedLocations.get(tag);
    if(res == null) {
      res = new ArrayList<Point3i>();
      taggedLocations.put(tag, res);
    }
    res.add(loc);
  }

  @Override
  public List<Point3i> getTaggedLocations(String tag) {
    List<Point3i> res = taggedLocations.get(tag);
    if(res == null) {
      return Collections.emptyList();
    }
    return res;
  }

  @Override
  public String toString() {
    return "StructureTemplate [uid=" + uid + "]";
  }

  
}
