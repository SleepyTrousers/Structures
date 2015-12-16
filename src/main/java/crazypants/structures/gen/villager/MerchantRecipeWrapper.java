package crazypants.structures.gen.villager;

import com.google.gson.annotations.Expose;

import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

public class MerchantRecipeWrapper {

  @Expose
  private ItemStack itemToBuy;
  
  @Expose
  private ItemStack secondItemToBuy;
  
  @Expose
  private ItemStack itemToSell;
    

  public boolean isValid() {    
    return itemToBuy != null && itemToSell != null;
  }
  
  public MerchantRecipe createRecipe() {
    if(!isValid()) {
      return null;
    }
    return new MerchantRecipe(itemToBuy, secondItemToBuy, itemToSell);
  }
  
}
