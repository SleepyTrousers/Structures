package crazypants.structures.gen.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import crazypants.structures.Log;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.gen.structure.Border;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class JsonUtil {

  public static JsonObject getObjectField(JsonObject json, String field) {
    if(!json.has(field)) {
      return null;
    }
    JsonElement el = json.get(field);
    if(!el.isJsonNull() && el.isJsonObject()) {
      return el.getAsJsonObject();
    }
    return null;
  }

  public static JsonArray getArrayField(JsonObject json, String field) {
    if(!json.has(field)) {
      return null;
    }
    JsonElement el = json.get(field);
    if(!el.isJsonNull() && el.isJsonArray()) {
      return el.getAsJsonArray();
    }
    return null;
  }

  public static boolean getBooleanField(JsonObject json, String field, boolean def) {
    if(json.has(field)) {
      String str = json.get(field).getAsString();
      if("true".equalsIgnoreCase(str)) {
        return true;
      }
      if("false".equalsIgnoreCase(str)) {
        return false;
      }
      return def;
    }
    return def;
  }

  public static List<String> getStringArrayField(JsonObject obj, String field) {
    JsonElement je = obj.get(field);
    if(je == null) {
      return Collections.emptyList();
    }
    if(!je.isJsonArray()) {
      return Collections.emptyList();
    }
    List<String> res = new ArrayList<String>();
    JsonArray arr = je.getAsJsonArray();
    for (int i = 0; i < arr.size(); i++) {
      JsonElement e = arr.get(i);
      if(e != null && e.isJsonPrimitive() && e.getAsJsonPrimitive().isString()) {
        res.add(e.getAsString());
      }
    }
    return res;
  }

  public static int getIntField(JsonObject obj, String field, int def) {
    JsonElement je = obj.get(field);
    if(je == null) {
      return def;
    }
    if(je.isJsonPrimitive() && je.getAsJsonPrimitive().isNumber()) {
      return je.getAsInt();
    }
    return def;
  }

  public static float getFloatField(JsonObject obj, String field, float def) {
    JsonElement je = obj.get(field);
    if(je == null) {
      return def;
    }
    if(je.isJsonPrimitive() && je.getAsJsonPrimitive().isNumber()) {
      return je.getAsFloat();
    }
    return def;
  }

  public static String getStringField(JsonObject obj, String field, String def) {
    JsonElement je = obj.get(field);
    if(je == null) {
      return def;
    }
    if(je.isJsonPrimitive() && je.getAsJsonPrimitive().isString()) {
      return je.getAsString();
    }
    return def;
  }

  public static TypedObject getTypedObjectField(JsonObject parent, String fieldName) {
    JsonObject obj = getObjectField(parent, fieldName);
    if(obj != null && !obj.isJsonNull() && obj.has("type")) {
      String type = obj.get("type").getAsString();
      return new TypedObject(type, obj);
    }
    return null;
  }

  public static List<TypedObject> getTypedObjectArray(JsonObject parent, String arrayName) {
    List<TypedObject> res = new ArrayList<TypedObject>();
    JsonArray arr = getArrayField(parent, arrayName);
    if(arr != null) {
      for (JsonElement e : arr) {
        if(e.isJsonObject()) {
          JsonObject valObj = e.getAsJsonObject();
          if(!valObj.isJsonNull() && valObj.has("type")) {
            String type = valObj.get("type").getAsString();
            res.add(new TypedObject(type, valObj));
          }
        }
      }
    }
    return res;
  }

  public static Border getBorder(JsonObject parent, Border def) {
    JsonObject obj = parent.getAsJsonObject("Border");
    if(obj == null) {
      return def;
    }
    Border border = new Border();
    if(obj.has("sizeXZ")) {
      border.setBorderXZ(getIntField(obj, "sizeXZ", border.get(ForgeDirection.NORTH)));
    }
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      border.set(dir, JsonUtil.getIntField(obj, dir.name().toLowerCase(), border.get(dir)));
    }
    return border;
  }

  public static Point3i getPoint3iField(JsonObject valObj, String field, Point3i def) {
    JsonArray arr = getArrayField(valObj, field);
    if(arr != null && !arr.isJsonNull() && arr.size() == 3) {
      return new Point3i(arr.get(0).getAsInt(), arr.get(1).getAsInt(), arr.get(2).getAsInt());
    }
    return def;
  }
  
  public static NBTTagCompound parseNBT(String nbtTxt) {
    if(nbtTxt == null || nbtTxt.isEmpty()) {
      return null;
    }
    try {
      NBTBase nbtbase = JsonToNBT.func_150315_a(nbtTxt);
      return (NBTTagCompound) nbtbase;
    } catch (NBTException e) {
      Log.warn("EntityUtil.parseNBT: Could not parse NBT " + nbtTxt + " Error: " + e);
      e.printStackTrace();
      return null;
    }
  }

  public static ItemStack getItemStack(JsonObject json, String element) {
    JsonObject obj = getObjectField(json, element);
    if(obj == null) {
      return null;
    }
    String uid = getStringField(obj, "uid", null);
    if(uid == null) {
      return null;
    }
    UniqueIdentifier u = new UniqueIdentifier(uid);
    ItemStack res = GameRegistry.findItemStack(u.modId, u.name, 1);
    if(res == null) {
      return res;
    }
    res.setItemDamage(getIntField(obj, "meta", res.getItemDamage()));
    
    String nbtStr = getStringField(obj, "nbt", null);
    if(nbtStr != null) {
      NBTTagCompound nbt = parseNBT(nbtStr);
      if(nbt != null) {
        res.setTagCompound(nbt);  
      }      
    }

    return res;
  }

  public static class TypedObject {
    final String type;
    JsonObject obj;

    TypedObject(String type, JsonObject obj) {
      this.type = type;
      this.obj = obj;
    }

  }

}
