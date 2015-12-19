package crazypants.structures.gen.structure.loot;

import com.google.gson.annotations.Expose;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;

public class LootEntry {

  @Expose
  int minChanceToGenerate;

  @Expose
  int maxChanceToGenerate;

  @Expose
  int weight;
  
  @Expose
  ItemStack item;

  public LootEntry() { 
    minChanceToGenerate = 1;
    maxChanceToGenerate = 1;
    weight = 30;
  }
  
  public LootEntry(LootEntry other) {
    minChanceToGenerate = other.minChanceToGenerate;
    maxChanceToGenerate = other.maxChanceToGenerate;
    weight = other.weight;
    if(other.item != null) {
      item = new ItemStack(other.item.getItem(), other.item.stackSize, other.item.getItemDamage());
    }
  }
  
  public WeightedRandomChestContent createContent() {
    return new WeightedRandomChestContent(item, minChanceToGenerate, maxChanceToGenerate, weight);
  }

  public ItemStack getItem() {
    return item;
  }

  public void setItem(ItemStack item) {
    this.item = item;
  }

  public int getTheMinimumChanceToGenerateItem() {
    return minChanceToGenerate;
  }

  public void setTheMinimumChanceToGenerateItem(int theMinimumChanceToGenerateItem) {
    this.minChanceToGenerate = theMinimumChanceToGenerateItem;
  }

  public int getTheMaximumChanceToGenerateItem() {
    return maxChanceToGenerate;
  }

  public void setTheMaximumChanceToGenerateItem(int theMaximumChanceToGenerateItem) {
    this.maxChanceToGenerate = theMaximumChanceToGenerateItem;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

}
