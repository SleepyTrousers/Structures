package crazypants.structures.gen.io;

import com.google.gson.JsonObject;

import crazypants.structures.api.ITyped;
import crazypants.structures.api.gen.IChunkValidator;
import crazypants.structures.api.gen.ISiteValidator;
import crazypants.structures.gen.structure.decorator.LootTableDecorator;
import crazypants.structures.gen.structure.preperation.ClearPreperation;
import crazypants.structures.gen.structure.preperation.FillPreperation;
import crazypants.structures.gen.structure.sampler.SurfaceLocationSampler;
import crazypants.structures.gen.structure.validator.BiomeValidatorAll;
import crazypants.structures.gen.structure.validator.BiomeValidatorAny;
import crazypants.structures.gen.structure.validator.DimensionValidator;
import crazypants.structures.gen.structure.validator.LevelGroundValidator;
import crazypants.structures.gen.structure.validator.RandomValidator;
import crazypants.structures.gen.structure.validator.SpacingValidator;
import crazypants.structures.runtime.action.CompositeAction;
import crazypants.structures.runtime.action.ExecuteCommandAction;
import crazypants.structures.runtime.action.RandomizerAction;
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

public class DefaultParsers {

  public static void register() {

    //Location samplers    
    add(new GsonParserAdapter(new SurfaceLocationSampler()));

    //validators    
    add(new RandomValidator());
    add(new DimensionValidator());        
    add(new LevelGroundValidator());
    add(new BiomeValidatorAny());
    add(new BiomeValidatorAll());
    add(new SpacingValParser());    

    //site preps    
    add(new ClearPreperation());
    add(new FillPreperation());
     
    //Decorators
    add(new LootTableDecorator());
    
    //behaviours
    add(new ResidentSpawner());    
    add(new VirtualSpawnerBehaviour());
    add(new ServerTickBehaviour());

    //conditions    
    add(new AndCondition());
    add(new OrCondition());
    add(new BlockExistsCondition());
    add(new PlayerInRangeCondition());
    add(new MaxEntitiesInRangeCondition());
    add(new ElapasedTimeCondition());
    add(new TickCountCondition());
    
    //actions
    add(new GsonParserAdapter(new ExecuteCommandAction()));
    add(new GsonParserAdapter(new CompositeAction()));    
    add(new GsonParserAdapter(new RandomizerAction()));
  }

  private static void add(ITyped typed) {
    ParserRegister.instance.register(new GsonParserAdapter(typed));
  }
  
  private static void add(ParserAdapater fact) {
    ParserRegister.instance.register(fact);
  }
   

  //-----------------------------------------------------------------
  static class SpacingValParser extends ParserAdapater {

    public SpacingValParser() {
      super("SpacingValidator");
    }

    @Override
    public IChunkValidator createChunkValidator(String uid, JsonObject json) {     
      SpacingValidator res = GsonIO.INSTANCE.getGson().fromJson(json, SpacingValidator.class);
      res.setValidateChunk(true);
      res.setValidateLocation(false);      
      return res;
    }

    @Override
    public ISiteValidator createSiteValidator(String uid, JsonObject json) {      
      SpacingValidator res = GsonIO.INSTANCE.getGson().fromJson(json, SpacingValidator.class);
      res.setValidateChunk(false);
      res.setValidateLocation(true);      
      return res;
    }

  }

}
