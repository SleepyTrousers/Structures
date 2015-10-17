package crazypants.structures.gen.io;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import crazypants.structures.Log;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.gen.IVillagerGenerator;
import crazypants.structures.gen.StructureRegister;
import crazypants.structures.gen.villager.VillagerGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

public class VillagerParser {

  public IVillagerGenerator parseVillagerConfig(StructureRegister reg, String uid, String json) throws Exception {
    
    if(uid == null) {
      throw new Exception("Null uid specified");
    }
    if(json == null) {
      throw new Exception("Null json specified");
    }
    if(reg == null) {
      throw new Exception("Null register specified");
    }

    VillagerGenerator res = new VillagerGenerator(uid);
    try {

      JsonObject to = new JsonParser().parse(json).getAsJsonObject();
      to = to.getAsJsonObject("VillagerGenerator");

      if(to.has("villagerId")) {
        res.setVillagerId(to.get("villagerId").getAsInt());
      }
      if(to.has("texture")) {
        res.setTexture(to.get("texture").getAsString());
      }

      if(to.has("weight")) {
        int weight = to.get("weight").getAsInt();
        int num = 1;
        if(to.has("maxNum")) {
          num = to.get("maxNum").getAsInt();
        }
        res.setWeight(weight, num);
      }

      List<String> templateUids = new ArrayList<String>();
      if(to.has("templatesPlains")) {
        getTemapletsUids(reg, to.get("templatesPlains"), templateUids);
        for (String str : templateUids) {
          res.addPlainsTemplate(str);
        }
      }
      if(to.has("templatesDesert")) {
        templateUids.clear();
        getTemapletsUids(reg, to.get("templatesDesert"), templateUids);
        for (String str : templateUids) {
          res.addDesertTemplate(str);
        }
      }

      if(to.has("trades")) {
        JsonElement el = to.get("trades");
        if(el != null && el.isJsonArray()) {
          JsonArray templates = el.getAsJsonArray();
          for (JsonElement e : templates) {
            if(!e.isJsonNull() && e.isJsonObject()) {              
              JsonObject tradeObj = e.getAsJsonObject();                            
              ItemStack inputOne = parseItemStack("input1", tradeObj);
              ItemStack inputTwo = parseItemStack("input2", tradeObj);
              ItemStack output = parseItemStack("output", tradeObj);
              if(inputOne == null || output == null) {
                Log.warn("VillagerParser.parseVillagerConfig: Invalid trade ingonred in villager " + uid + " input1: " + inputOne + " input2: " + inputTwo
                    + " output:" + output);
              } else {
                MerchantRecipe recipe = new MerchantRecipe(inputOne, inputTwo, output);
                res.addRecipe(recipe);
              }
            }
          }
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      throw new Exception("TemplateParser: Could not parse generator " + uid, e);
    }

    res.validate();

    return res;
  }

  private ItemStack parseItemStack(String stackName, JsonObject tradeObj) {
    JsonObject stackObj = null;
    if(tradeObj.has(stackName)) {
      JsonElement stackElement = tradeObj.get(stackName);
      if(!stackElement.isJsonNull() && stackElement.isJsonObject()) {    
        stackObj = stackElement.getAsJsonObject();
      }
    }
    if(stackObj == null) {
      return null;
    }
    
    if(!stackObj.has("item")) {
     return null;
    }     
    String itemId = stackObj.get("item").getAsString();
    UniqueIdentifier uid = new UniqueIdentifier(itemId);
    Item item = GameRegistry.findItem(uid.modId, uid.name);
    if(item == null) {
      return null;
    }    
    int num = 1;
    if(stackObj.has("number")) {
      num = stackObj.get("number").getAsInt();
    }
    int meta = 0;
    if(stackObj.has("meta")) {
      meta = stackObj.get("meta").getAsInt();
    }      
    return new ItemStack(item, num, meta);
  }

  private void getTemapletsUids(StructureRegister reg, JsonElement el, List<String> templateUids) {
    if(el != null && el.isJsonArray()) {
      JsonArray templates = el.getAsJsonArray();
      for (JsonElement e : templates) {
        if(e.isJsonObject()) {
          JsonObject valObj = e.getAsJsonObject();
          if(!valObj.isJsonNull() && valObj.has("uid")) {
            String tpUid = valObj.get("uid").getAsString();
            //Do the load so everything is parsed at registration time to find issues early
            IStructureTemplate st = reg.getStructureTemplate(tpUid, true);
            if(st != null) {
              templateUids.add(tpUid);
            }
          }
        }
      }
    }
  }
}
