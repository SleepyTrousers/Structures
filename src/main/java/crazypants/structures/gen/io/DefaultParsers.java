package crazypants.structures.gen.io;

import com.google.gson.JsonObject;

import crazypants.structures.api.ITyped;
import crazypants.structures.api.gen.IChunkValidator;
import crazypants.structures.api.gen.ISiteValidator;
import crazypants.structures.api.io.IParser;
import crazypants.structures.gen.structure.decorator.CompositeDecorator;
import crazypants.structures.gen.structure.decorator.LootTableDecorator;
import crazypants.structures.gen.structure.preperation.ClearPreperation;
import crazypants.structures.gen.structure.preperation.CompositePreperation;
import crazypants.structures.gen.structure.preperation.FillPreperation;
import crazypants.structures.gen.structure.sampler.SurfaceLocationSampler;
import crazypants.structures.gen.structure.validator.BiomeValidatorAll;
import crazypants.structures.gen.structure.validator.BiomeValidatorAny;
import crazypants.structures.gen.structure.validator.CompositeSiteValidator;
import crazypants.structures.gen.structure.validator.CompositeValidator;
import crazypants.structures.gen.structure.validator.DimensionValidator;
import crazypants.structures.gen.structure.validator.LevelGroundValidator;
import crazypants.structures.gen.structure.validator.RandomValidator;
import crazypants.structures.gen.structure.validator.SpacingValidator;
import crazypants.structures.runtime.action.CompositeAction;
import crazypants.structures.runtime.action.ExecuteCommandAction;
import crazypants.structures.runtime.action.RandomizerAction;
import crazypants.structures.runtime.behaviour.CompositeBehaviour;
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
    register(new GsonParserAdapter(new SurfaceLocationSampler()));

    //validators
    register(new CompositeValidator());
    register(new CompositeSiteValidator());
    register(new RandomValidator());
    register(new DimensionValidator());        
    register(new LevelGroundValidator());
    register(new BiomeValidatorAny());
    register(new BiomeValidatorAll());
    register(new SpacingValParser());    

    //site preps    
    register(new CompositePreperation());
    register(new ClearPreperation());    
    register(new FillPreperation());
     
    //Decorators
    register(new CompositeDecorator());
    register(new LootTableDecorator());
    
    //behaviours
    register(new CompositeBehaviour());
    register(new ResidentSpawner());    
    register(new VirtualSpawnerBehaviour());
    register(new ServerTickBehaviour());

    //conditions    
    register(new AndCondition());
    register(new OrCondition());
    register(new BlockExistsCondition());
    register(new PlayerInRangeCondition());
    register(new MaxEntitiesInRangeCondition());
    register(new ElapasedTimeCondition());
    register(new TickCountCondition());
    
    //actions
    register(new CompositeAction());
    register(new ExecuteCommandAction());        
    register(new RandomizerAction());
  }

  private static void register(ITyped typed) {
    ParserRegister.instance.register(new GsonParserAdapter(typed));
  }
  
  private static void register(IParser parser) {
    ParserRegister.instance.register(parser);
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
