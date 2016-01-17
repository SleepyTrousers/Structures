package crazypants.structures.gen.villager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;

//TODO: 1.8 Use NBT now
public class TradeHandler {//implements IVillageTradeHandler {

  private List<MerchantRecipe> recipes = new ArrayList<MerchantRecipe>();
  
  public void addRecipe(MerchantRecipe recipe) {
    recipes.add(recipe);
  }
  
//  @Override
  public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random) {
    for(MerchantRecipe recipe : recipes) {
      recipeList.add(new MerchantRecipe(recipe.getItemToBuy(), recipe.getSecondItemToBuy(), recipe.getItemToSell()));
    }
  }

  public boolean hasTrades() {
    return !recipes.isEmpty();
  }
}
