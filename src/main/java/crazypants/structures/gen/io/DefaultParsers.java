package crazypants.structures.gen.io;

import static crazypants.structures.gen.io.GsonIO.GSON;

import java.util.List;

import com.google.gson.JsonObject;

import crazypants.structures.Log;
import crazypants.structures.api.gen.IChunkValidator;
import crazypants.structures.api.gen.IDecorator;
import crazypants.structures.api.gen.ILocationSampler;
import crazypants.structures.api.gen.ISitePreperation;
import crazypants.structures.api.gen.ISiteValidator;
import crazypants.structures.api.runtime.IAction;
import crazypants.structures.api.runtime.IBehaviour;
import crazypants.structures.api.runtime.ICondition;
import crazypants.structures.gen.io.JsonUtil.TypedObject;
import crazypants.structures.gen.structure.decorator.LootTableDecorator;
import crazypants.structures.gen.structure.preperation.ClearPreperation;
import crazypants.structures.gen.structure.preperation.FillPreperation;
import crazypants.structures.gen.structure.sampler.SurfaceLocationSampler;
import crazypants.structures.gen.structure.validator.BiomeValidator;
import crazypants.structures.gen.structure.validator.DimensionValidator;
import crazypants.structures.gen.structure.validator.LevelGroundValidator;
import crazypants.structures.gen.structure.validator.RandomValidator;
import crazypants.structures.gen.structure.validator.SpacingValidator;
import crazypants.structures.gen.structure.validator.biome.BiomeDescriptor;
import crazypants.structures.gen.structure.validator.biome.BiomeFilterAll;
import crazypants.structures.gen.structure.validator.biome.BiomeFilterAny;
import crazypants.structures.gen.structure.validator.biome.IBiomeFilter;
import crazypants.structures.runtime.action.CompositeAction;
import crazypants.structures.runtime.action.ExecuteCommandAction;
import crazypants.structures.runtime.action.RandomizerAction;
import crazypants.structures.runtime.behaviour.Positioned;
import crazypants.structures.runtime.behaviour.ResidentSpawner;
import crazypants.structures.runtime.behaviour.ServerTickBehaviour;
import crazypants.structures.runtime.behaviour.vspawner.VirtualSpawnerBehaviour;
import crazypants.structures.runtime.condition.AndCondition;
import crazypants.structures.runtime.condition.BlockExistsCondition;
import crazypants.structures.runtime.condition.ElapasedTimeCondition;
import crazypants.structures.runtime.condition.MaxEntitiesInRangeCondition;
import crazypants.structures.runtime.condition.OrCondition;
import crazypants.structures.runtime.condition.PlayerInRangeCondition;
import crazypants.structures.runtime.condition.TickCountCondition;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class DefaultParsers {

  public static void register() {

    //Location samplers
    add(new SurfaceSamplerParser());

    //validators
    add(new RandomValParser());
    add(new DimValParser());
    add(new SpacingValParser());
    add(new LevGrnValdParser());
    add(new BiomeValParser());

    //site preps
    add(new FillPrepParser());
    add(new ClearPrepParser());

    //Decorators
    add(new LootTableDecFact());

    //behaviours
    add(new ResidentSpawnerParser());
    add(new VirtualSpawnerParser());
    add(new ServerTickBehaviourParser());

    //conditions    
    add(new GsonParserAdapter("AndCondition",  AndCondition.class));
    add(new GsonParserAdapter("OrCondition", OrCondition.class));
    add(new GsonParserAdapter("BlockExists", BlockExistsCondition.class));
    add(new GsonParserAdapter("PlayerInRange", PlayerInRangeCondition.class));
    add(new GsonParserAdapter("MaxEntitiesInRange", MaxEntitiesInRangeCondition.class));
    add(new GsonParserAdapter("ElapasedTimeCondition", ElapasedTimeCondition.class));
    add(new GsonParserAdapter("TickCountCondition", TickCountCondition.class));
    
    //actions
    add(new GsonParserAdapter("ExecuteCommand", ExecuteCommandAction.class));
    add(new GsonParserAdapter("CompositeAction", CompositeAction.class));
    add(new GsonParserAdapter("RandomizerAction", RandomizerAction.class));
  }

  private static void add(ParserAdapater fact) {
    ParserRegister.instance.register(fact);
  }

  private static void readPositioned(Positioned positioned, JsonObject json) {
    positioned.setPosition(JsonUtil.getPoint3iField(json, "position", positioned.getPosition()));
    positioned.setTaggedPosition(JsonUtil.getStringField(json, "taggedPosition", positioned.getTaggedPosition()));
  }

  //-----------------------------------------------------------------
  static class SurfaceSamplerParser extends ParserAdapater {

    SurfaceSamplerParser() {
      super("SurfaceSampler");
    }

    @Override
    public ILocationSampler createSampler(String uid, JsonObject json) {
      SurfaceLocationSampler res = new SurfaceLocationSampler();
      res.setDistanceFromSurface(JsonUtil.getIntField(json, "distanceFromSurface", res.getDistanceFromSurface()));
      res.setCanGenerateOnFluid(JsonUtil.getBooleanField(json, "canGenerateOnFluid", res.isCanPlaceInFluid()));
      return res;
    }

  }

  //-----------------------------------------------------------------
  static class RandomValParser extends ParserAdapater {
    RandomValParser() {
      super("RandomValidator");
    }

    @Override
    public IChunkValidator createChunkValidator(String uid, JsonObject json) {
      return GSON.fromJson(json, RandomValidator.class);
    }

  }

  //-----------------------------------------------------------------
  static class DimValParser extends ParserAdapater {

    DimValParser() {
      super("DimensionValidator");
    }

    @Override
    public IChunkValidator createChunkValidator(String uid, JsonObject json) {
      DimensionValidator res = new DimensionValidator();
      res.addAll(JsonUtil.getStringArrayField(json, "names"), false);
      res.addAll(JsonUtil.getStringArrayField(json, "namesExcluded"), true);
      return res;
    }
  }

  //-----------------------------------------------------------------
  static class SpacingValParser extends ParserAdapater {

    public SpacingValParser() {
      super("SpacingValidator");
    }

    @Override
    public IChunkValidator createChunkValidator(String uid, JsonObject json) {
      SpacingValidator res = new SpacingValidator();
      res.setMinSpacing(JsonUtil.getIntField(json, "minSpacing", res.getMinSpacing()));
      res.setTemplateFilter(JsonUtil.getStringArrayField(json, "templateFilter"));
      res.setValidateChunk(true);
      res.setValidateLocation(false);
      return res;
    }

    @Override
    public ISiteValidator createSiteValidator(String uid, JsonObject json) {
      SpacingValidator res = new SpacingValidator();
      res.setMinSpacing(JsonUtil.getIntField(json, "minSpacing", res.getMinSpacing()));
      res.setTemplateFilter(JsonUtil.getStringArrayField(json, "templateFilter"));
      res.setValidateChunk(false);
      res.setValidateLocation(true);
      return res;
    }

  }

  //-----------------------------------------------------------------
  static class LevGrnValdParser extends ParserAdapater {
    public LevGrnValdParser() {
      super("LevelGroundValidator");
    }

    @Override
    public ISiteValidator createSiteValidator(String uid, JsonObject json) {
      LevelGroundValidator res = new LevelGroundValidator();
      res.setCanSpawnOnWater(JsonUtil.getBooleanField(json, "canSpawnOnWater", res.isCanSpawnOnWater()));
      res.setTolerance(JsonUtil.getIntField(json, "tolerance", res.getTolerance()));
      res.setSampleSpacing(JsonUtil.getIntField(json, "sampleSpacing", res.getTolerance()));
      res.setMaxSampleCount(JsonUtil.getIntField(json, "maxSamples", res.getTolerance()));
      res.setBorder(JsonUtil.getBorder(json, res.getBorder()));
      return res;
    }
  }

  //-----------------------------------------------------------------
  static class BiomeValParser extends ParserAdapater {

    BiomeValParser() {
      super("BiomeValidator");
    }

    @Override
    public IChunkValidator createChunkValidator(String uid, JsonObject json) {
      String typeElement = JsonUtil.getStringField(json, "match", "any");
      IBiomeFilter filter;
      if("all".equals(typeElement)) {
        filter = new BiomeFilterAll();
      } else {
        filter = new BiomeFilterAny();
      }
      addBiomeTypes(filter, JsonUtil.getStringArrayField(json, "types"), false);
      addBiomeTypes(filter, JsonUtil.getStringArrayField(json, "typesExcluded"), true);
      addBiomesByName(filter, JsonUtil.getStringArrayField(json, "names"), false);
      addBiomesByName(filter, JsonUtil.getStringArrayField(json, "namesExcluded"), true);

      return new BiomeValidator(filter);
    }

    private void addBiomesByName(IBiomeFilter filter, List<String> names, boolean isExcluded) {
      for (String name : names) {
        filter.addBiomeDescriptor(new BiomeDescriptor(name, isExcluded));
      }
    }

    private boolean addBiomeTypes(IBiomeFilter filter, List<String> types, boolean isExclude) {
      for (String typeStr : types) {
        try {
          Type type = BiomeDictionary.Type.valueOf(typeStr);
          filter.addBiomeDescriptor(new BiomeDescriptor(type, isExclude));
        } catch (Exception e) {
          Log.error("DefaultRuleFactory.BiomeValFact: Could not create biome type : " + typeStr);
          return false;
        }
      }
      return true;
    }
  }

  //-----------------------------------------------------------------
  static class ClearPrepParser extends ParserAdapater {

    ClearPrepParser() {
      super("ClearPreperation");
    }

    @Override
    public ISitePreperation createPreperation(String uid, JsonObject json) {
      ClearPreperation res = new ClearPreperation();
      res.setClearPlants(JsonUtil.getBooleanField(json, "clearPlants", res.isClearPlants()));
      res.setClearBellowGround(JsonUtil.getBooleanField(json, "clearBellowGround", res.getClearBellowGround()));
      res.setBorder(JsonUtil.getBorder(json, res.getBorder()));
      return res;
    }

  }

  //-----------------------------------------------------------------
  static class FillPrepParser extends ParserAdapater {

    FillPrepParser() {
      super("FillPreperation");
    }

    @Override
    public ISitePreperation createPreperation(String uid, JsonObject json) {
      FillPreperation res = new FillPreperation();
      res.setClearPlants(JsonUtil.getBooleanField(json, "clearPlants", res.isClearPlants()));
      res.setBorder(JsonUtil.getBorder(json, res.getBorder()));
      return res;
    }

  }

  //-----------------------------------------------------------------
  static class LootTableDecFact extends ParserAdapater {

    LootTableDecFact() {
      super("LootTableInventory");
    }

    @Override
    public IDecorator createDecorator(String uid, JsonObject json) {
      LootTableDecorator res = new LootTableDecorator();
      res.setCategory(JsonUtil.getStringField(json, "category", null));
      res.setTargets(JsonUtil.getStringArrayField(json, "targets"));
      return res;
    }
  }

  static class AbstractBehaviourParser extends ParserAdapater {

    public AbstractBehaviourParser(String uid) {
      super(uid);
    }

    protected ICondition parseCondition(JsonObject json, String f) {
      TypedObject obj = JsonUtil.getTypedObjectField(json, f);
      if(obj != null) {
        return ParserRegister.instance.createCondition(obj.type, obj.obj);
      }
      return null;
    }

    protected IAction parseAction(JsonObject json, String f) {
      TypedObject obj = JsonUtil.getTypedObjectField(json, f);
      if(obj != null) {
        return ParserRegister.instance.createAction(obj.type, obj.obj);
      }
      return null;
    }

  }

  //-----------------------------------------------------------------
  static class VirtualSpawnerParser extends AbstractBehaviourParser {

    VirtualSpawnerParser() {
      super("VirtualSpawner");
    }

    @Override
    public IBehaviour createBehaviour(String uid, JsonObject json) {
      VirtualSpawnerBehaviour res = new VirtualSpawnerBehaviour();
      readPositioned(res, json);
      res.setEntityTypeName(JsonUtil.getStringField(json, "entity", res.getEntityTypeName()));
      res.setEntityNbtText(JsonUtil.getStringField(json, "entityNbt", res.getEntityNbtText()));
      res.setNumberSpawned(JsonUtil.getIntField(json, "numSpawned", res.getNumberSpawned()));
      res.setSpawnRange(JsonUtil.getIntField(json, "spawnRange", res.getSpawnRange()));
      res.setPersistEntities(JsonUtil.getBooleanField(json, "persistEntities", res.isPersistEntities()));
      res.setUseVanillaSpawnChecks(JsonUtil.getBooleanField(json, "useVanillaSpawnChecks", res.isUseVanillaSpawnChecks()));
      res.setRenderParticles(JsonUtil.getBooleanField(json, "renderParticles", res.isRenderParticles()));
      res.setActiveCondition(parseCondition(json, "activeCondition"));
      res.setSpawnCondition(parseCondition(json, "spawnCondition"));
      return res;
    }

  }

  //ResidentSpawnerParser
  //-----------------------------------------------------------------
  static class ResidentSpawnerParser extends AbstractBehaviourParser {

    ResidentSpawnerParser() {
      super("ResidentSpawner");
    }

    @Override
    public IBehaviour createBehaviour(String uid, JsonObject json) {
      ResidentSpawner res = new ResidentSpawner();
      readPositioned(res, json);
      res.setEntity(JsonUtil.getStringField(json, "entity", res.getEntity()));
      res.setEntityNbtText(JsonUtil.getStringField(json, "entityNbt", res.getEntityNbtText()));
      res.setNumSpawned(JsonUtil.getIntField(json, "numSpawned", res.getNumSpawned()));
      res.setRespawnRate(JsonUtil.getIntField(json, "respawnRate", res.getNumSpawned()));
      res.setRespawnRate(JsonUtil.getIntField(json, "homeRadius", res.getHomeRadius()));
      res.setPreCondition(parseCondition(json, "preCondition"));
      res.setOnSpawnAction(parseAction(json, "onSpawnAction"));
      return res;
    }

  }

  //ServerTickBehaviourParser
  //-----------------------------------------------------------------
  static class ServerTickBehaviourParser extends AbstractBehaviourParser {

    ServerTickBehaviourParser() {
      super("ServerTickBehaviour");
    }

    @Override
    public IBehaviour createBehaviour(String uid, JsonObject json) {
      ServerTickBehaviour res = new ServerTickBehaviour();
      readPositioned(res, json);
      res.setExecutionInterval(JsonUtil.getIntField(json, "executionInterval", res.getExecutionInterval()));
      res.setCondition(parseCondition(json, "condition"));
      res.setAction(parseAction(json, "action"));
      return res;
    }

  }
 
}
