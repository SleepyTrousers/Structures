package crazypants.structures.gen.io;

import java.util.List;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import crazypants.structures.Log;
import crazypants.structures.api.gen.IChunkValidator;
import crazypants.structures.api.gen.IDecorator;
import crazypants.structures.api.gen.ILocationSampler;
import crazypants.structures.api.gen.ISitePreperation;
import crazypants.structures.api.gen.ISiteValidator;
import crazypants.structures.api.runtime.IBehaviour;
import crazypants.structures.api.runtime.ICondition;
import crazypants.structures.api.util.Point3i;
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
import crazypants.structures.runtime.AndCondition;
import crazypants.structures.runtime.OrCondition;
import crazypants.structures.runtime.condition.BlockExistsCondition;
import crazypants.structures.runtime.condition.ElapasedTimeCondition;
import crazypants.structures.runtime.condition.MaxEntitiesInRangeCondition;
import crazypants.structures.runtime.condition.PlayerInRangeCondition;
import crazypants.structures.runtime.condition.TickCountCondition;
import crazypants.structures.runtime.vspawner.VirtualSpawnerBehaviour;
import net.minecraft.block.Block;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class DefaultParsers {

  public static void register() {
    add(new SurfaceSamplerFact());
    add(new RandomValFac());
    add(new DimValFact());
    add(new SpacingValFact());
    add(new LevGrndFact());
    add(new BiomeValFact());
    add(new FillPrepFact());
    add(new ClearPrepFact());
    add(new LootTableDecFact());
    add(new VirtualSpawnerFact());
    add(new AndConditionFact());
    add(new OrConditionFact());
    add(new BlockExistsConditionFact());
    add(new PlayerInRangeConditionFact());
    add(new MaxEntitiesInRangeFact());
    add(new ElapasedTimeConditionFact());
    add(new TickCountConditionFact());
  }

  private static void add(AbstractSingleParserFactory fact) {
    ParserRegister.instance.register(fact);
  }

  //-----------------------------------------------------------------
  static class SurfaceSamplerFact extends AbstractSingleParserFactory {

    SurfaceSamplerFact() {
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
  static class RandomValFac extends AbstractSingleParserFactory {
    RandomValFac() {
      super("RandomValidator");
    }

    @Override
    public IChunkValidator createChunkValidator(String uid, JsonObject json) {
      RandomValidator res = new RandomValidator();
      res.setChancePerChunk(JsonUtil.getFloatField(json, "chancePerChunk", res.getChancePerChunk()));
      return res;
    }

  }

  //-----------------------------------------------------------------
  static class DimValFact extends AbstractSingleParserFactory {

    DimValFact() {
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
  static class SpacingValFact extends AbstractSingleParserFactory {

    public SpacingValFact() {
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
  static class LevGrndFact extends AbstractSingleParserFactory {
    public LevGrndFact() {
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
  static class BiomeValFact extends AbstractSingleParserFactory {

    BiomeValFact() {
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
  static class ClearPrepFact extends AbstractSingleParserFactory {

    ClearPrepFact() {
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
  static class FillPrepFact extends AbstractSingleParserFactory {

    FillPrepFact() {
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
  static class LootTableDecFact extends AbstractSingleParserFactory {

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

  //-----------------------------------------------------------------
  static class VirtualSpawnerFact extends AbstractSingleParserFactory {

    VirtualSpawnerFact() {
      super("VirtualSpawner");
    }

    @Override
    public IBehaviour createBehaviour(String uid, JsonObject json) {
      VirtualSpawnerBehaviour res = new VirtualSpawnerBehaviour();
      String entStr = JsonUtil.getStringField(json, "entity", null);
      if(entStr == null) {
        Log.warn("DefaultParsers.VirtualSpawnerFact.createBehaviour: No entity specified for Virtual Spawner.");
        return null;
      }
      res.setEntityTypeName(entStr);
      res.setNumberSpawned(JsonUtil.getIntField(json, "numSpawned", res.getNumberSpawned()));
      res.setSpawnRange(JsonUtil.getIntField(json, "spawnRange", res.getSpawnRange()));      
      res.setPersistEntities(JsonUtil.getBooleanField(json, "persistEntities", res.isPersistEntities()));
      res.setUseVanillaSpawnChecks(JsonUtil.getBooleanField(json, "useVanillaSpawnChecks", res.isUseVanillaSpawnChecks()));
      res.setRenderParticles(JsonUtil.getBooleanField(json, "renderParticles", res.isRenderParticles()));
      res.setStructureLocalPosition(JsonUtil.getPoint3iField(json, "position", res.getStructureLocalPosition()));

      
      ICondition con = createCondition(json, "activeCondition");
      if(con != null) {
        res.setActiveCondition(con);
      }
      con = createCondition(json, "spawnCondition");
      if(con != null) {
        res.setSpawnCondition(con);
      }

      return res;
    }

    private ICondition createCondition(JsonObject json, String f) {
      TypedObject obj = JsonUtil.getTypedObjectField(json, f);
      if(obj != null) {
        return ParserRegister.instance.createCondition(obj.type, obj.obj);        
      }
      return null;
    }
  }

  //-----------------------------------------------------------------
  static class AndConditionFact extends AbstractSingleParserFactory {

    AndConditionFact() {
      super("AndCondition");
    }

    @Override
    public ICondition createCondition(String uid, JsonObject json) {

      AndCondition res = new AndCondition();
      List<TypedObject> arrayContents = JsonUtil.getTypedObjectArray(json, "conditions");
      for (TypedObject o : arrayContents) {
        ICondition con = ParserRegister.instance.createCondition(o.type, o.obj);
        if(con != null) {
          res.addCondition(con);
        }
      }
      return res;
    }
  }

  //-----------------------------------------------------------------
  static class OrConditionFact extends AbstractSingleParserFactory {

    OrConditionFact() {
      super("AndCondition");
    }

    @Override
    public ICondition createCondition(String uid, JsonObject json) {
      OrCondition res = new OrCondition();
      List<TypedObject> arrayContents = JsonUtil.getTypedObjectArray(json, "conditions");
      for (TypedObject o : arrayContents) {
        ICondition con = ParserRegister.instance.createCondition(o.type, o.obj);
        if(con != null) {
          res.addCondition(con);
        }
      }
      return res;
    }
  }

  //-----------------------------------------------------------------
  static class BlockExistsConditionFact extends AbstractSingleParserFactory {

    BlockExistsConditionFact() {
      super("BlockExists");
    }

    @Override
    public ICondition createCondition(String uid, JsonObject json) {

      String blkStr = JsonUtil.getStringField(json, "block", null);
      if(blkStr == null) {
        return null;
      }
      UniqueIdentifier blkId = new UniqueIdentifier(blkStr);
      Block blk = GameRegistry.findBlock(blkId.modId, blkId.name);
      if(blk == null) {
        return null;
      }
      int meta = JsonUtil.getIntField(json, "meta", -1);

      Point3i pos = JsonUtil.getPoint3iField(json, "position", null);
      if(pos == null) {
        return null;
      }
      return new BlockExistsCondition(blk, meta, pos);
    }
  }

  //-----------------------------------------------------------------
  static class PlayerInRangeConditionFact extends AbstractSingleParserFactory {

    PlayerInRangeConditionFact() {
      super("PlayerInRange");
    }

    @Override
    public ICondition createCondition(String uid, JsonObject json) {
      PlayerInRangeCondition con = new PlayerInRangeCondition();
      con.setRange(JsonUtil.getIntField(json, "range", con.getRange()));
      Point3i pos = JsonUtil.getPoint3iField(json, "position", null);
      if(pos != null) {
        con.setLocalPos(pos);
      }
      return con;
    }
  }

  //-----------------------------------------------------------------
  static class MaxEntitiesInRangeFact extends AbstractSingleParserFactory {

    MaxEntitiesInRangeFact() {
      super("MaxEntitiesInRange");
    }

    @Override
    public ICondition createCondition(String uid, JsonObject json) {
      MaxEntitiesInRangeCondition con = new MaxEntitiesInRangeCondition();
      con.setMaxEntities(JsonUtil.getIntField(json, "maxEntities", con.getMaxEntities()));
      con.setRange(JsonUtil.getIntField(json, "range", con.getRange()));      
      Point3i pos = JsonUtil.getPoint3iField(json, "position", null);
      if(pos != null) {
        con.setLocalPos(pos);
      }
      List<String> ents = JsonUtil.getStringArrayField(json, "entities");
      if(ents != null) {
        con.setEntities(ents);
      }      
      return con;
    }
  }
  
  //-----------------------------------------------------------------
  static class ElapasedTimeConditionFact extends AbstractSingleParserFactory {

    ElapasedTimeConditionFact() {
      super("ElapasedTimeCondition");
    }

    @Override
    public ICondition createCondition(String uid, JsonObject json) {
      ElapasedTimeCondition con = new ElapasedTimeCondition();
      con.setInitialTime(JsonUtil.getIntField(json, "initialTime", con.getInitialTime()));
      con.setMinTime(JsonUtil.getIntField(json, "minTime", con.getMinTime()));
      con.setMaxTime(JsonUtil.getIntField(json, "maxTime", con.getMaxTime()));
      con.setPersisted(JsonUtil.getBooleanField(json, "persisted", con.isPersisted()));            
      return con;
    }
  }
  
  //TickCountCondition
  //-----------------------------------------------------------------
  static class TickCountConditionFact extends AbstractSingleParserFactory {

    TickCountConditionFact() {
      super("TickCountCondition");
    }

    @Override
    public ICondition createCondition(String uid, JsonObject json) {
      TickCountCondition con = new TickCountCondition();
      con.setInitialCount(JsonUtil.getIntField(json, "initialCount", con.getInitialCount()));
      con.setMinCount(JsonUtil.getIntField(json, "minCount", con.getMinCount()));
      con.setMaxCount(JsonUtil.getIntField(json, "maxCount", con.getMaxCount()));
      con.setPersisted(JsonUtil.getBooleanField(json, "persisted", con.isPersisted()));            
      return con;
    }
  }
}
