package crazypants.structures.gen.io;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import crazypants.structures.api.runtime.IAction;
import crazypants.structures.api.runtime.ICondition;
import crazypants.structures.api.util.Point3i;
import net.minecraft.block.Block;

public class GsonIO {

  public static final Gson GSON = new GsonIO().createGson();

  private Gson createGson() {
    GsonBuilder builder = new GsonBuilder();
    builder.excludeFieldsWithoutExposeAnnotation();
    builder.registerTypeAdapter(Point3i.class, new Point3iIO());
    builder.registerTypeAdapter(IAction.class, new ActionIO());
    builder.registerTypeAdapter(ICondition.class, new ConditionIO());
    builder.registerTypeAdapter(Block.class, new BlockIO());
    return builder.create();
  }

  //--------------------------------------------------
  private static class Point3iIO implements JsonSerializer<Point3i>, JsonDeserializer<Point3i> {

    @Override
    public JsonElement serialize(Point3i src, Type typeOfSrc, JsonSerializationContext context) {
      if(src == null) {
        return JsonNull.INSTANCE;
      }
      JsonArray res = new JsonArray();
      res.add(new JsonPrimitive(src.x));
      res.add(new JsonPrimitive(src.y));
      res.add(new JsonPrimitive(src.z));
      return res;
    }

    @Override
    public Point3i deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

      if(!json.isJsonArray()) {
        return null;
      }
      JsonArray arr = json.getAsJsonArray();
      if(arr != null && !arr.isJsonNull() && arr.size() == 3) {
        return new Point3i(arr.get(0).getAsInt(), arr.get(1).getAsInt(), arr.get(2).getAsInt());
      }
      return null;
    }

  }

  //--------------------------------------------------
  private static class BlockIO implements JsonSerializer<Block>, JsonDeserializer<Block> {

    @Override
    public Block deserialize(JsonElement je, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if(!je.isJsonPrimitive() || !je.getAsJsonPrimitive().isString()) {
        return null;
      }

      UniqueIdentifier blkId = new UniqueIdentifier(je.getAsString());
      Block blk = GameRegistry.findBlock(blkId.modId, blkId.name);      
      return blk;
    }

    @Override
    public JsonElement serialize(Block src, Type typeOfSrc, JsonSerializationContext context) {
      UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(src);
      if(id == null) {
        return null;
      }
      return new JsonPrimitive(id.toString());
    }

  }

  //--------------------------------------------------
  private static class ActionIO implements JsonDeserializer<IAction> {

    @Override
    public IAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if(!json.isJsonObject() || json.isJsonNull()) {
        return null;
      }
      JsonObject obj = json.getAsJsonObject();
      return ParserRegister.instance.createAction(obj.get("type").getAsString(), obj);
    }

  }

  //--------------------------------------------------
  private static class ConditionIO implements JsonDeserializer<ICondition> {

    @Override
    public ICondition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if(!json.isJsonObject() || json.isJsonNull()) {
        return null;
      }
      JsonObject obj = json.getAsJsonObject();
      if(!obj.has("type")) {
        return null;
      }
      return ParserRegister.instance.createCondition(obj.get("type").getAsString(), obj);
    }

  }
}
