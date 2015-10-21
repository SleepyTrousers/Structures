package crazypants.structures.gen.io;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

public final class LootTableParser {

  public LootTableParser() {
  }
  
  public void parseLootTableCategories(String uid, String json) throws Exception {
    try {            
      JsonObject to = new JsonParser().parse(json).getAsJsonObject();
      parseLootTableCategories(to);
    }catch (Exception e) {
      e.printStackTrace();
      throw new Exception("LootTableParser: Could not parse " + uid, e);
    }
  }
  
  public void parseLootTableCategories(JsonObject to) {
    if (to.has("LootTableCategories")) {
      JsonArray arr = to.getAsJsonArray("LootTableCategories");
      for (JsonElement e : arr) {
        if (!e.isJsonNull() && e.isJsonObject()) {
          JsonObject valObj = e.getAsJsonObject();
          if (!valObj.isJsonNull() && valObj.has("category")) {
            String category = valObj.get("category").getAsString();
            if (category != null && !category.trim().isEmpty()) {
              ChestGenHooks info = ChestGenHooks.getInfo(category);
              info.setMin(JsonUtil.getIntField(valObj, "minCount", info.getMin()));
              info.setMax(JsonUtil.getIntField(valObj, "maxCount", info.getMax()));             
              addContentsToCategory(valObj, info);
            }
          }
        }
      }
    }
  }

  private void addContentsToCategory(JsonObject to, ChestGenHooks category) {
    JsonArray contentsArr = to.getAsJsonArray("contents");
    if (contentsArr != null && !contentsArr.isJsonNull()) {
      for (JsonElement rndContent : contentsArr) {
        WeightedRandomChestContent rndCont = parseContentItem(rndContent);
        if (rndCont != null) {
          category.addItem(rndCont);
        }
      }
    }
  }

  private WeightedRandomChestContent parseContentItem(JsonElement rndContent) {
    if (rndContent == null || !rndContent.isJsonObject()) {
      return null;
    }
    JsonObject obj = rndContent.getAsJsonObject();    
    int minSize = JsonUtil.getIntField(obj, "minSize", 1);
    int maxSize = JsonUtil.getIntField(obj, "maxSize", 1);
    int weight = JsonUtil.getIntField(obj, "weight", 0);
    if (minSize > maxSize || weight == 0) {
      return null;
    }
    ItemStack stack = JsonUtil.getItemStack(obj, "itemStack");
    if (stack == null) {
      return null;
    }
    return new WeightedRandomChestContent(stack, minSize, maxSize, weight);
  }

}
