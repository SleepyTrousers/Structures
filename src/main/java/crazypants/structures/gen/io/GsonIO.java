package crazypants.structures.gen.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Type;

import org.apache.commons.codec.binary.Base64;

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
import crazypants.structures.Log;
import crazypants.structures.api.gen.IChunkValidator;
import crazypants.structures.api.gen.IDecorator;
import crazypants.structures.api.gen.ILocationSampler;
import crazypants.structures.api.gen.ISitePreperation;
import crazypants.structures.api.gen.ISiteValidator;
import crazypants.structures.api.gen.IStructureComponent;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.gen.PositionedComponent;
import crazypants.structures.api.runtime.IAction;
import crazypants.structures.api.runtime.IBehaviour;
import crazypants.structures.api.runtime.ICondition;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.gen.StructureGenRegister;
import crazypants.structures.gen.structure.Border;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class GsonIO {

  public static final GsonIO INSTANCE = new GsonIO();

  private final GsonBuilder builder = new GsonBuilder();

  private Gson gson;

  private GsonIO() {
    builder.excludeFieldsWithoutExposeAnnotation();

    //Basic types
    builder.registerTypeAdapter(Point3i.class, new Point3iIO());
    builder.registerTypeAdapter(Border.class, new BorderIO());
    builder.registerTypeAdapter(Rotation.class, new RotationIO());

    //MC Types
    builder.registerTypeAdapter(Block.class, new BlockIO());
    builder.registerTypeAdapter(ItemStack.class, new ItemStackIO());

    //Resources
    builder.registerTypeAdapter(IStructureTemplate.class, new StructureTemplateIO());
    builder.registerTypeAdapter(PositionedComponent.class, new PositionedComponentIO());

    //Typed parsers    
    builder.registerTypeAdapter(ILocationSampler.class, new LocationSamplerIO());
    builder.registerTypeAdapter(IChunkValidator.class, new ChunkValIO());
    builder.registerTypeAdapter(ISiteValidator.class, new SiteValIO());
    builder.registerTypeAdapter(ISitePreperation.class, new SitePrepIO());
    builder.registerTypeAdapter(IDecorator.class, new DecoratorIO());
    builder.registerTypeAdapter(IAction.class, new ActionIO());
    builder.registerTypeAdapter(ICondition.class, new ConditionIO());
    builder.registerTypeAdapter(IBehaviour.class, new BehaviourIO());
    builder.setPrettyPrinting();

  }

  public void registerTypeAdapter(Type type, Object typeAdapter) {
    builder.registerTypeAdapter(type, typeAdapter);
    gson = null;
  }

  public Gson getGson() {
    if(gson == null) {
      gson = builder.create();
    }
    return gson;
  }

  //--------------------------------------------------
  private static class PositionedComponentIO implements JsonSerializer<PositionedComponent>, JsonDeserializer<PositionedComponent> {

    @Override
    public PositionedComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if(!json.isJsonObject() || json.isJsonNull()) {
        return null;
      }
      JsonObject obj = json.getAsJsonObject();
      if(!obj.has("uid")) {
        return null;
      }

      String uid = obj.get("uid").getAsString();
      IStructureComponent st = StructureGenRegister.instance.getStructureComponent(uid, true);
      if(st == null) {
        return null;
      }

      Point3i offset = JsonUtil.getPoint3iField(obj, "offset", new Point3i());
      return new PositionedComponent(st, offset);
    }

    @Override
    public JsonElement serialize(PositionedComponent src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject res = new JsonObject();
      res.addProperty("uid", src.getComponent().getUid());
      Point3i offset = src.getOffset();
      if(offset != null) {
        JsonArray arr = new JsonArray();
        arr.add(new JsonPrimitive(offset.x));
        arr.add(new JsonPrimitive(offset.y));
        arr.add(new JsonPrimitive(offset.z));
        res.add("offset", arr);
      }
      return res;
    }

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
  private static class ItemStackIO implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if(!json.isJsonObject()) {
        return null;
      }

      JsonObject obj = json.getAsJsonObject();
      String itemStr = JsonUtil.getStringField(obj, "item", null);

      UniqueIdentifier itemId = new UniqueIdentifier(itemStr);
      Item item = GameRegistry.findItem(itemId.modId, itemId.name);
      if(item == null) {
        throw new JsonParseException("No item found for " + itemStr);
      }
      ItemStack res = new ItemStack(item, JsonUtil.getIntField(obj, "number", 1), JsonUtil.getIntField(obj, "meta", 0));

      String nbt64 = JsonUtil.getStringField(obj, "nbt", null);
      if(nbt64 != null) {
        try {
          byte[] decodedBytes = Base64.decodeBase64(nbt64.getBytes());
          ByteArrayInputStream bais = new ByteArrayInputStream(decodedBytes);
          NBTTagCompound nbt = CompressedStreamTools.readCompressed(bais);
          res.stackTagCompound = nbt;
        } catch (Exception e) {
          Log.warn("GsonIO.ItemStackIO.deserialize: Could not parse nbt. " + e);
        }
      } else {
        try {
        NBTTagCompound nbt = JsonUtil.parseNBT(JsonUtil.getStringField(obj, "nbtString", null));
        if(nbt != null) {
          res.stackTagCompound = nbt;
        }
        } catch (Exception e) {
          Log.warn("GsonIO.ItemStackIO.deserialize: Could not parse nbt string. " + e);
        }
      }
      

      return res;
    }

    @Override
    public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
      if(src == null) {
        return JsonNull.INSTANCE;
      }
      JsonObject res = new JsonObject();

      UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(src.getItem());
      res.addProperty("item", id.modId + ":" + id.name);
      res.addProperty("number", src.stackSize);
      res.addProperty("meta", src.getItemDamage());
      if(src.stackTagCompound != null) {
        try {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          CompressedStreamTools.writeCompressed(src.stackTagCompound, new DataOutputStream(baos));
          byte[] encodedBytes = Base64.encodeBase64(baos.toByteArray());
          res.addProperty("nbt", new String(encodedBytes));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      return res;
    }

  }

  //--------------------------------------------------
  private static class RotationIO implements JsonSerializer<Rotation>, JsonDeserializer<Rotation> {

    @Override
    public Rotation deserialize(JsonElement je, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if(!je.isJsonPrimitive()) {
        return null;
      }
      JsonPrimitive prim = je.getAsJsonPrimitive();
      if(prim.isNumber()) {
        return Rotation.get(prim.getAsInt());
      }
      if(prim.isString()) {
        return Rotation.valueOf(prim.getAsString());
      }
      return null;
    }

    @Override
    public JsonElement serialize(Rotation src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.getRotationDegrees());
    }

  }

  //--------------------------------------------------
  private static class BorderIO implements JsonSerializer<Border>, JsonDeserializer<Border> {

    @Override
    public Border deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if(!json.isJsonObject()) {
        return null;
      }

      JsonObject obj = json.getAsJsonObject();

      Border border = new Border();
      if(obj.has("sizeXZ")) {
        border.setBorderXZ(JsonUtil.getIntField(obj, "sizeXZ", border.get(ForgeDirection.NORTH)));
      }
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        border.set(dir, JsonUtil.getIntField(obj, dir.name().toLowerCase(), border.get(dir)));
      }
      return border;
    }

    @Override
    public JsonElement serialize(Border src, Type typeOfSrc, JsonSerializationContext context) {
      if(src == null) {
        return JsonNull.INSTANCE;
      }
      JsonObject res = new JsonObject();
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        int val = src.get(dir);
        res.addProperty(dir.name().toLowerCase(), val);
      }
      return res;
    }

  }

  //--------------------------------------------------
  private static class ActionIO implements JsonSerializer<IAction>, JsonDeserializer<IAction> {

    @Override
    public IAction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if(!json.isJsonObject() || json.isJsonNull()) {
        return null;
      }
      JsonObject obj = json.getAsJsonObject();
      if(!obj.has("type")) {
        return null;
      }
      return ParserRegister.instance.createAction(obj.get("type").getAsString(), obj);
    }

    @Override
    public JsonElement serialize(IAction src, Type typeOfSrc, JsonSerializationContext context) {
      return INSTANCE.getGson().toJsonTree(src);
    }

  }

  //--------------------------------------------------
  private static class ConditionIO implements JsonSerializer<ICondition>, JsonDeserializer<ICondition> {

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

    @Override
    public JsonElement serialize(ICondition src, Type typeOfSrc, JsonSerializationContext context) {
      return INSTANCE.getGson().toJsonTree(src);
    }
  }

  //--------------------------------------------------
  private static class BehaviourIO implements JsonSerializer<IBehaviour>, JsonDeserializer<IBehaviour> {

    @Override
    public IBehaviour deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if(!json.isJsonObject() || json.isJsonNull()) {
        return null;
      }
      JsonObject obj = json.getAsJsonObject();
      if(!obj.has("type")) {
        return null;
      }
      return ParserRegister.instance.createBehaviour(obj.get("type").getAsString(), obj);
    }

    @Override
    public JsonElement serialize(IBehaviour src, Type typeOfSrc, JsonSerializationContext context) {
      return INSTANCE.getGson().toJsonTree(src);
    }

  }

  //--------------------------------------------------
  private static class LocationSamplerIO implements JsonSerializer<ILocationSampler>, JsonDeserializer<ILocationSampler> {

    @Override
    public ILocationSampler deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if(!json.isJsonObject() || json.isJsonNull()) {
        return null;
      }
      JsonObject obj = json.getAsJsonObject();
      if(!obj.has("type")) {
        return null;
      }
      return ParserRegister.instance.createSampler(obj.get("type").getAsString(), obj);
    }

    @Override
    public JsonElement serialize(ILocationSampler src, Type typeOfSrc, JsonSerializationContext context) {
      return INSTANCE.getGson().toJsonTree(src);
    }

  }

  //--------------------------------------------------
  private static class ChunkValIO implements JsonSerializer<IChunkValidator>, JsonDeserializer<IChunkValidator> {

    @Override
    public IChunkValidator deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if(!json.isJsonObject() || json.isJsonNull()) {
        return null;
      }
      JsonObject obj = json.getAsJsonObject();
      if(!obj.has("type")) {
        return null;
      }
      return ParserRegister.instance.createChunkValidator(obj.get("type").getAsString(), obj);
    }

    @Override
    public JsonElement serialize(IChunkValidator src, Type typeOfSrc, JsonSerializationContext context) {
      return INSTANCE.getGson().toJsonTree(src);
    }

  }

  //--------------------------------------------------
  private static class SiteValIO implements JsonSerializer<ISiteValidator>, JsonDeserializer<ISiteValidator> {

    @Override
    public ISiteValidator deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if(!json.isJsonObject() || json.isJsonNull()) {
        return null;
      }
      JsonObject obj = json.getAsJsonObject();
      if(!obj.has("type")) {
        return null;
      }
      return ParserRegister.instance.createSiteValidator(obj.get("type").getAsString(), obj);
    }

    @Override
    public JsonElement serialize(ISiteValidator src, Type typeOfSrc, JsonSerializationContext context) {
      return INSTANCE.getGson().toJsonTree(src);
    }

  }

  //--------------------------------------------------
  private static class SitePrepIO implements JsonSerializer<ISitePreperation>, JsonDeserializer<ISitePreperation> {

    @Override
    public ISitePreperation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if(!json.isJsonObject()) {
        return null;
      }
      JsonObject obj = json.getAsJsonObject();
      if(!obj.has("type")) {
        return null;
      }
      return ParserRegister.instance.createPreperation(obj.get("type").getAsString(), obj);
    }

    @Override
    public JsonElement serialize(ISitePreperation src, Type typeOfSrc, JsonSerializationContext context) {
      return INSTANCE.getGson().toJsonTree(src);
    }

  }

  private static class DecoratorIO implements JsonSerializer<IDecorator>, JsonDeserializer<IDecorator> {

    @Override
    public IDecorator deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if(!json.isJsonObject()) {
        return null;
      }
      JsonObject obj = json.getAsJsonObject();
      if(!obj.has("type")) {
        return null;
      }
      return ParserRegister.instance.createDecorator(obj.get("type").getAsString(), obj);
    }

    @Override
    public JsonElement serialize(IDecorator src, Type typeOfSrc, JsonSerializationContext context) {
      return INSTANCE.getGson().toJsonTree(src);
    }

  }

  //------------------------- Templates

  private static class StructureTemplateIO implements JsonSerializer<IStructureTemplate>, JsonDeserializer<IStructureTemplate> {

    @Override
    public IStructureTemplate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if(!json.isJsonObject()) {
        return null;
      }

      if(!json.isJsonObject() || json.isJsonNull()) {
        return null;
      }
      JsonObject obj = json.getAsJsonObject();
      if(!obj.has("uid")) {
        return null;
      }

      String uid = obj.get("uid").getAsString();
      IStructureTemplate st = StructureGenRegister.instance.getStructureTemplate(uid, true);
      return st;
    }

    @Override
    public JsonElement serialize(IStructureTemplate src, Type typeOfSrc, JsonSerializationContext context) {
      if(src == null || src.getUid() == null) {
        return JsonNull.INSTANCE;
      }
      JsonObject res = new JsonObject();
      res.addProperty("uid", src.getUid());
      return res;
    }

  }

}
