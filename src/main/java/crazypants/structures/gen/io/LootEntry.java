package crazypants.structures.gen.io;

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
