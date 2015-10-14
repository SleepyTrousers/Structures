package crazypants.structures.gen;

import static net.minecraftforge.common.util.ForgeDirection.DOWN;
import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.UP;
import static net.minecraftforge.common.util.ForgeDirection.WEST;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import crazypants.structures.api.util.Rotation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailDetector;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.block.BlockVine;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Based on the forge rotation helper, but that only rotates blocks in world, I
 * need to do it before placing the block in the world.
 */
public class RotationHelper {

  public static enum BlockType {
    LOG(0xC),
    DISPENSER(0x7),
    BED(0x3),
    RAIL(0xF),
    RAIL_POWERED(0x7),
    RAIL_ASCENDING(0x0), //guessed mask
    RAIL_CORNER(0x0), //guessed mask
    TORCH(0xF),
    STAIR(0x3),
    CHEST(0x7),
    SIGNPOST(0xF),
    DOOR(0x3),
    LEVER(0x7),
    BUTTON(0x7),
    REDSTONE_REPEATER(0x3),
    TRAPDOOR(0x3),
    MUSHROOM_CAP(0xF),
    MUSHROOM_CAP_CORNER(0x0), //guessed mask
    MUSHROOM_CAP_SIDE(0x0), //guessed mask
    VINE(0xF),
    SKULL(0x7),
    ANVIL(0x1),
    TRIP_WIRE_HOOK(0x3);

//    final int mask;

    private BlockType(int mask) {
//      this.mask = mask;
    }

  }

  private static final Map<BlockType, BiMap<Integer, ForgeDirection>> MAPPINGS = new HashMap<BlockType, BiMap<Integer, ForgeDirection>>();

  static {
    BiMap<Integer, ForgeDirection> biMap;

    biMap = HashBiMap.create(3);
    biMap.put(0x0, UP);
    biMap.put(0x4, EAST);
    biMap.put(0x8, SOUTH);
    MAPPINGS.put(BlockType.LOG, biMap);

    biMap = HashBiMap.create(4);
    biMap.put(0x0, SOUTH);
    biMap.put(0x1, WEST);
    biMap.put(0x2, NORTH);
    biMap.put(0x3, EAST);
    MAPPINGS.put(BlockType.BED, biMap);

    biMap = HashBiMap.create(4);
    biMap.put(0x2, EAST);
    biMap.put(0x3, WEST);
    biMap.put(0x4, NORTH);
    biMap.put(0x5, SOUTH);
    MAPPINGS.put(BlockType.RAIL_ASCENDING, biMap);

    biMap = HashBiMap.create(4);
    biMap.put(0x6, WEST);
    biMap.put(0x7, NORTH);
    biMap.put(0x8, EAST);
    biMap.put(0x9, SOUTH);
    MAPPINGS.put(BlockType.RAIL_CORNER, biMap);

    biMap = HashBiMap.create(6);
    biMap.put(0x1, EAST);
    biMap.put(0x2, WEST);
    biMap.put(0x3, SOUTH);
    biMap.put(0x4, NORTH);
    biMap.put(0x5, UP);
    biMap.put(0x7, DOWN);
    MAPPINGS.put(BlockType.LEVER, biMap);

    biMap = HashBiMap.create(4);
    biMap.put(0x0, WEST);
    biMap.put(0x1, NORTH);
    biMap.put(0x2, EAST);
    biMap.put(0x3, SOUTH);
    MAPPINGS.put(BlockType.DOOR, biMap);

    biMap = HashBiMap.create(4);
    biMap.put(0x0, NORTH);
    biMap.put(0x1, EAST);
    biMap.put(0x2, SOUTH);
    biMap.put(0x3, WEST);
    MAPPINGS.put(BlockType.REDSTONE_REPEATER, biMap);

    biMap = HashBiMap.create(4);
    biMap.put(0x1, EAST);
    biMap.put(0x3, SOUTH);
    biMap.put(0x7, NORTH);
    biMap.put(0x9, WEST);
    MAPPINGS.put(BlockType.MUSHROOM_CAP_CORNER, biMap);

    biMap = HashBiMap.create(4);
    biMap.put(0x2, NORTH);
    biMap.put(0x4, WEST);
    biMap.put(0x6, EAST);
    biMap.put(0x8, SOUTH);
    MAPPINGS.put(BlockType.MUSHROOM_CAP_SIDE, biMap);

    biMap = HashBiMap.create(2);
    biMap.put(0x0, SOUTH);
    biMap.put(0x1, EAST);
    MAPPINGS.put(BlockType.ANVIL, biMap);
  }   
  
  public static int rotateMetadata(Block block, int meta, Rotation rot) {    
    int result = meta;
    for(int i=0;i<rot.ordinal();i++) {
      result = rotateMetadata(block, result);
    }
    return result;
  }
  
  private static int rotateMetadata(Block block, int meta) {
    BlockType type = getBlockType(block);
    if(type == null) {
      return meta;
    }
    return rotateMetadata(ForgeDirection.UP, type, meta);
  }
  
  private static int rotateMetadata(ForgeDirection axis, BlockType blockType, int meta) {
    if(blockType == BlockType.RAIL || blockType == BlockType.RAIL_POWERED) {
      if(meta == 0x0 || meta == 0x1) {
        return ~meta & 0x1;
      }
      if(meta >= 0x2 && meta <= 0x5) {
        blockType = BlockType.RAIL_ASCENDING;
      }
      if(meta >= 0x6 && meta <= 0x9 && blockType == BlockType.RAIL) {
        blockType = BlockType.RAIL_CORNER;
      }
    }
    if(blockType == BlockType.SIGNPOST) {
      return (axis == UP) ? (meta + 0x4) % 0x10 : (meta + 0xC) % 0x10;
    }
    if(blockType == BlockType.LEVER && (axis == UP || axis == DOWN)) {
      switch (meta) {
      case 0x5:
        return 0x6;
      case 0x6:
        return 0x5;
      case 0x7:
        return 0x0;
      case 0x0:
        return 0x7;
      }
    }    
    
    if(blockType == BlockType.MUSHROOM_CAP) {
      if(meta % 0x2 == 0) {
        blockType = BlockType.MUSHROOM_CAP_SIDE;
      } else {
        blockType = BlockType.MUSHROOM_CAP_CORNER;
      }
    }
    if(blockType == BlockType.VINE) {
      return ((meta << 1) | ((meta & 0x8) >> 3));
    }

    ForgeDirection orientation = metadataToDirection(blockType, meta);
    ForgeDirection rotated = orientation.getRotation(axis);
    return directionToMetadata(blockType, rotated, meta);
  }

  private static BlockType getBlockType(Block block) {

    if(block instanceof BlockBed || block instanceof BlockPumpkin || block instanceof BlockFenceGate || block instanceof BlockEndPortalFrame
        || block instanceof BlockCocoa) {
      return BlockType.BED;
    }
    if(block instanceof BlockRail) {
      return BlockType.RAIL;
    }
    if(block instanceof BlockRailPowered || block instanceof BlockRailDetector) {
      return BlockType.RAIL_POWERED;
    }
    if(block instanceof BlockStairs) {
      return BlockType.STAIR;
    }
    if(block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockFurnace || block instanceof BlockLadder
        || block == Blocks.wall_sign) {
      return BlockType.CHEST;
    }
    if(block == Blocks.standing_sign) {
      return BlockType.SIGNPOST;
    }
    if(block instanceof BlockDoor) {
      return BlockType.DOOR;
    }
    if(block instanceof BlockButton) {
      return BlockType.BUTTON;
    }
    if(block instanceof BlockRedstoneRepeater || block instanceof BlockRedstoneComparator) {
      return BlockType.REDSTONE_REPEATER;
    }
    if(block instanceof BlockTrapDoor) {
      return BlockType.TRAPDOOR;
    }
    if(block instanceof BlockHugeMushroom) {
      return BlockType.MUSHROOM_CAP;
    }
    if(block instanceof BlockVine) {
      return BlockType.VINE;
    }
    if(block instanceof BlockSkull) {
      return BlockType.SKULL;
    }
    if(block instanceof BlockAnvil) {
      return BlockType.ANVIL;
    }
    if(block instanceof BlockLog) {
      return BlockType.LOG;
    }
    if(block instanceof BlockDispenser || block instanceof BlockPistonBase || block instanceof BlockPistonExtension || block instanceof BlockHopper) {
      return BlockType.DISPENSER;
    }
    if(block instanceof BlockTorch) {
      return BlockType.TORCH;
    }
    if(block instanceof BlockLever) {
      return BlockType.LEVER;
    }
    if(block instanceof BlockTripWireHook) {
      return BlockType.TRIP_WIRE_HOOK;
    }
    
    return null;
  }

  private static ForgeDirection metadataToDirection(BlockType blockType, int meta) {
    if(blockType == BlockType.LEVER) {
      if(meta == 0x6) {
        meta = 0x5;
      } else if(meta == 0x0) {
        meta = 0x7;
      }
    }

    if(MAPPINGS.containsKey(blockType)) {
      BiMap<Integer, ForgeDirection> biMap = MAPPINGS.get(blockType);
      if(biMap.containsKey(meta)) {
        return biMap.get(meta);
      }
    }

    if(blockType == BlockType.TORCH) {
      return ForgeDirection.getOrientation(6 - meta);
    }
    if(blockType == BlockType.STAIR) {
      return ForgeDirection.getOrientation(5 - meta);
    }
    if(blockType == BlockType.CHEST || blockType == BlockType.DISPENSER || blockType == BlockType.SKULL) {
      return ForgeDirection.getOrientation(meta);
    }
    if(blockType == BlockType.BUTTON) {
      return ForgeDirection.getOrientation(6 - meta);
    }
    if(blockType == BlockType.TRAPDOOR) {
      //return ForgeDirection.getOrientation(meta + 2).getOpposite();
      int val = meta & 3;
      switch(val) {
      case 0:
        return ForgeDirection.SOUTH;
      case 1:
        return ForgeDirection.NORTH;
      case 2:
        return ForgeDirection.EAST;
      case 3:
        return ForgeDirection.WEST;      
      }
    }
    if(blockType == BlockType.TRIP_WIRE_HOOK) {
      int val = meta & 3;
      switch(val) {
      case 0:
        return ForgeDirection.NORTH;
      case 1:
        return ForgeDirection.EAST;
      case 2:
        return ForgeDirection.SOUTH;
      case 3:
        return ForgeDirection.WEST;      
      }
    }

    return ForgeDirection.UNKNOWN;
  }

  private static int directionToMetadata(BlockType blockType, ForgeDirection direction, int origMeta) {
    if((blockType == BlockType.LOG || blockType == BlockType.ANVIL) && (direction.offsetX + direction.offsetY + direction.offsetZ) < 0) {
      direction = direction.getOpposite();
    }

    if(MAPPINGS.containsKey(blockType)) {
      BiMap<ForgeDirection, Integer> biMap = MAPPINGS.get(blockType).inverse();
      if(biMap.containsKey(direction)) {
        return biMap.get(direction);
      }
    }

    if(blockType == BlockType.TORCH) {
      if(direction.ordinal() >= 1) {
        return 6 - direction.ordinal();
      }
    }
    if(blockType == BlockType.STAIR) {
      return 5 - direction.ordinal();
    }
    if(blockType == BlockType.CHEST || blockType == BlockType.DISPENSER || blockType == BlockType.SKULL) {
      return direction.ordinal();
    }
    if(blockType == BlockType.BUTTON) {
      if(direction.ordinal() >= 2) {
        return 6 - direction.ordinal();
      }
    }
    if(blockType == BlockType.TRAPDOOR) {
      int lastTwoBits = origMeta & 12;
      switch(direction) {
      case NORTH:
        return lastTwoBits + 1;
      case EAST:
        return lastTwoBits + 2;
      case SOUTH:
        return lastTwoBits;
      case WEST:
        return lastTwoBits + 3;
      default:
        break;
      }     
    }
    if(blockType == BlockType.TRIP_WIRE_HOOK) {
      int lastTwoBits = origMeta & 12;
      switch(direction) {
      case NORTH:
        return lastTwoBits;
      case EAST:
        return lastTwoBits + 1;
      case SOUTH:
        return lastTwoBits + 2;
      case WEST:
        return lastTwoBits + 3;
      default:
        break;
      }      
    }

    return -1;
  }

}
