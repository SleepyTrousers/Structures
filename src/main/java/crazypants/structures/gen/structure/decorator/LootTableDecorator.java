package crazypants.structures.gen.structure.decorator;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import crazypants.structures.gen.ChunkBounds;
import crazypants.structures.gen.structure.Structure;
import crazypants.vec.Point3i;

public class LootTableDecorator implements IDecorator {

  private String category;
  private List<String> targets;
  
  public LootTableDecorator() {    
  }
  
  @Override
  public void decorate(Structure structure, World world, Random random, ChunkBounds bounds) {       
    for(String target : targets) {
      Collection<Point3i> locs = structure.getTaggedLocations(target);
      for(Point3i loc : locs) {
        IInventory inv = getInventory(world, loc);
        if(inv != null) {
          WeightedRandomChestContent.generateChestContents(random, ChestGenHooks.getItems(category, random), inv, ChestGenHooks.getCount(category, random));      
        }
      }
    }     
  }

  private IInventory getInventory(World world, Point3i loc) {
    TileEntity te = world.getTileEntity(loc.x, loc.y, loc.z);
    if(te instanceof IInventory) {
      return (IInventory)te;
    }
    return null;
  }

  public void setCategory(String category) {
    if(category != null) {
      category = category.trim();
    }
    this.category = category;      
  }

  public void setTargets(List<String> targets) {
    this.targets = targets;    
  }
  
  public boolean isValid() {
    return category != null && targets != null && !targets.isEmpty();
  }

}
