package crazypants.structures.gen.structure;

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
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.structures.Log;
import crazypants.structures.StructureUtils;
import crazypants.structures.api.gen.IStructureComponent;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.api.util.StructureUtil;
import crazypants.structures.api.util.VecUtil;
import crazypants.structures.gen.RotationHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTripWireHook;
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
  private final String uid;
  private final int surfaceOffset;

  private final StructureBlock fillerBlock;
  private final StructureBlock topBlock;
  
  private final HashMultimap<String, Point3i> taggedLocations =  HashMultimap.create();

  public StructureComponentNBT(AxisAlignedBB bb, Point3i size, String uid, int surfaceOffset, StructureBlock fillerBlock, StructureBlock topBlock) {
    this.bb = bb;
    this.size = size;
    this.uid = uid;
    this.surfaceOffset = surfaceOffset;
    this.fillerBlock = fillerBlock;
    this.topBlock = topBlock;
  }

  public StructureComponentNBT(String uid, InputStream is) throws IOException {
    this(uid, CompressedStreamTools.read(new DataInputStream(is)));
  }

  public StructureComponentNBT(String uid, NBTTagCompound root) throws IOException {

    this.uid = uid;

    NBTTagList dataList = (NBTTagList) root.getTag("data");
    for (int i = 0; i < dataList.tagCount(); i++) {
      NBTTagCompound entryTag = dataList.getCompoundTagAt(i);
      NBTTagCompound blockTag = entryTag.getCompoundTag("block");
      StructureBlock sb = new StructureBlock(blockTag);
      List<Point3i> coords = StructureUtils.readCoords(entryTag);
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

    StructureUtils.readTaggedLocations(taggedLocations, root);

    if(uid == null || bb == null || blocks.isEmpty()) {
      throw new IOException("Invalid NBT");
    }
  }

  

  public void writeToNBT(NBTTagCompound root) {

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

    StructureUtils.writeTaggedLocationToNBT(taggedLocations, root);

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
    byte[] bytes = StructureUtils.writeCoordsToByteArray(entry.getValue());
    entryTag.setByteArray("coords", bytes);
    entryTag.setInteger("numCoords", entry.getValue().size());
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
    
    return block instanceof BlockTorch || block instanceof BlockSand || block instanceof BlockGravel || block instanceof BlockAnvil || block instanceof BlockTripWireHook || block instanceof BlockRedstoneWire;
  }

  private void fillBlocks(World world, int x, int y, int z, Rotation rot, StructureBoundingBox genBounds, List<Point3i> coords, Block filler) {
    for (Point3i coord : coords) {
      Point3i bc = VecUtil.transformLocationToWorld(x, y, z, rot, size, coord);
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
        Point3i bc = VecUtil.transformLocationToWorld(x, y, z, rot, size, coord);

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

  public Map<StructureBlock, List<Point3i>> getBlocks() {
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
    taggedLocations.put(tag, loc);    
  }

  @Override
  public Collection<Point3i> getTaggedLocations(String tag) {
    Set<Point3i> res = taggedLocations.get(tag);
    if(res == null) {
      return Collections.emptyList();
    }
    return res;
  }
  
  @Override
  public Collection<String> getTagsAtLocation(Point3i loc) {
    return StructureUtils.getTagsAtLocation(taggedLocations, loc);    
  }

  @Override
  public String toString() {
    return "StructureComponent [uid=" + uid + "]";
  }

  @Override
  public Multimap<String, Point3i> getTaggedLocations() {
    return taggedLocations;
  }

  public void setTags(Multimap<String, Point3i> tags) {
    taggedLocations.clear();
    if(tags != null) {
      taggedLocations.putAll(tags);
    }    
  }

  
}
