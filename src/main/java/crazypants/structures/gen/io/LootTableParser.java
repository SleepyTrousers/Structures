package crazypants.structures.gen.io;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
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
    // TODO Auto-generated method stub
    
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
              info.setMin(JsonUtil.getIntElement(valObj, "minCount", info.getMin()));
              info.setMax(JsonUtil.getIntElement(valObj, "maxCount", info.getMax()));             
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
    int minSize = JsonUtil.getIntElement(obj, "minSize", 1);
    int maxSize = JsonUtil.getIntElement(obj, "maxSize", 1);
    int weight = JsonUtil.getIntElement(obj, "weight", 0);
    if (minSize > maxSize || weight == 0) {
      return null;
    }
    ItemStack stack = parseItemStack(obj.get("itemStack"));
    if (stack == null) {
      return null;
    }
    return new WeightedRandomChestContent(stack, minSize, maxSize, weight);
  }

  private ItemStack parseItemStack(JsonElement stk) {
    if (stk == null || !stk.isJsonObject()) {
      return null;
    }
    JsonObject obj = stk.getAsJsonObject();
    String uid = JsonUtil.getStringElement(obj, "uid", null);
    if (uid == null) {
      return null;
    }
    UniqueIdentifier u = new UniqueIdentifier(uid);
    ItemStack res = GameRegistry.findItemStack(u.modId, u.name, 1);
    if (res == null) {
      return res;
    }
    res.setItemDamage(JsonUtil.getIntElement(obj, "meta", res.getItemDamage()));

    return res;
  }

  

}
