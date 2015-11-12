package crazypants.structures.gen.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

import crazypants.structures.Log;
import crazypants.structures.api.gen.IChunkValidator;
import crazypants.structures.api.gen.IDecorator;
import crazypants.structures.api.gen.ILocationSampler;
import crazypants.structures.api.gen.ISitePreperation;
import crazypants.structures.api.gen.ISiteValidator;
import crazypants.structures.api.io.IActionParser;
import crazypants.structures.api.io.IBehaviourParser;
import crazypants.structures.api.io.IChunkValidatorParser;
import crazypants.structures.api.io.IConditionParser;
import crazypants.structures.api.io.IDecoratorParser;
import crazypants.structures.api.io.ILocationSamplerParser;
import crazypants.structures.api.io.IParser;
import crazypants.structures.api.io.ISitePreperationParser;
import crazypants.structures.api.io.ISiteValidatorParser;
import crazypants.structures.api.runtime.IAction;
import crazypants.structures.api.runtime.IBehaviour;
import crazypants.structures.api.runtime.ICondition;

public class ParserRegister {

  private final List<IParser> factories = new ArrayList<IParser>();

  private final Map<String, ILocationSamplerParser> samplerParsers = new HashMap<String, ILocationSamplerParser>();

  private final Map<String, IChunkValidatorParser> chunkValParsers = new HashMap<String, IChunkValidatorParser>();

  private final Map<String, ISitePreperationParser> prepParsers = new HashMap<String, ISitePreperationParser>();

  private final Map<String, ISiteValidatorParser> siteValParsers = new HashMap<String, ISiteValidatorParser>();

  private final Map<String, IDecoratorParser> decParsers = new HashMap<String, IDecoratorParser>();
    
  private final Map<String, IBehaviourParser> behavParsers = new HashMap<String, IBehaviourParser>();
  
  private final Map<String, IConditionParser> conditionParsers = new HashMap<String, IConditionParser>();
  
  private final Map<String, IActionParser> actionParsers = new HashMap<String, IActionParser>();

  public static final ParserRegister instance = new ParserRegister();

  static {
    DefaultParsers.register();    
  }

  private final NullFactory nullFactory = new NullFactory();

  private ParserRegister() {
  }
  
  public boolean register(Class<? extends IParser> parser) {
    try {
      register(parser.newInstance());
      return true;
    } catch (Exception e) {
      Log.warn("Could not create instance for parser with class " + parser == null ? null : parser.getName());
    }
    return false;
  }
  
  public void register(IParser fact) {
    factories.add(fact);
  }

  public ILocationSampler createSampler(String uid, JsonObject json) {
    ILocationSamplerParser f = samplerParsers.get(uid);
    if(f == null) {
      f = findFactory(uid, ILocationSamplerParser.class);
      samplerParsers.put(uid, f);
    }
    return f.createSampler(uid, json);
  }

  public IChunkValidator createChunkValidator(String uid, JsonObject json) {
    IChunkValidatorParser f = chunkValParsers.get(uid);
    if(f == null) {
      f = findFactory(uid, IChunkValidatorParser.class);
      chunkValParsers.put(uid, f);
    }
    return f.createChunkValidator(uid, json);
  }

  public ISitePreperation createPreperation(String uid, JsonObject json) {
    ISitePreperationParser f = prepParsers.get(uid);
    if(f == null) {
      f = findFactory(uid, ISitePreperationParser.class);
      prepParsers.put(uid, f);
    }
    return f.createPreperation(uid, json);
  }

  public ISiteValidator createSiteValidator(String uid, JsonObject json) {
    ISiteValidatorParser f = siteValParsers.get(uid);
    if(f == null) {
      f = findFactory(uid, ISiteValidatorParser.class);
      siteValParsers.put(uid, f);
    }
    return f.createSiteValidator(uid, json);
  }

  public IDecorator createDecorator(String uid, JsonObject json) {
    IDecoratorParser f = decParsers.get(uid);
    if(f == null) {
      f = findFactory(uid, IDecoratorParser.class);
      decParsers.put(uid, f);
    }
    return f.createDecorator(uid, json);
  }
  
  public IBehaviour createBehaviour(String uid, JsonObject json) {
    IBehaviourParser f = behavParsers.get(uid);
    if(f == null) {
      f = findFactory(uid, IBehaviourParser.class);
      behavParsers.put(uid, f);
    }
    return f.createBehaviour(uid, json);
  }
  
  public ICondition createCondition(String uid, JsonObject json) {
    IConditionParser f = conditionParsers.get(uid);
    if(f == null) {
      f = findFactory(uid, IConditionParser.class);
      conditionParsers.put(uid, f);
    }
    return f.createCondition(uid, json);
  }
  
  public IAction createAction(String uid, JsonObject json) {
    IActionParser f = actionParsers.get(uid);
    if(f == null) {
      f = findFactory(uid, IActionParser.class);
      if(f == null) {
        return null;
      }
      actionParsers.put(uid, f);
    }    
    return f.createAction(uid, json);
  }

  @SuppressWarnings("unchecked")
  private <T extends IParser> T findFactory(String uid, Class<T> type) {
    if(uid == null) {
      return (T) nullFactory;
    }
    for (IParser fact : factories) {
      if(fact.canParse(uid) && type.isAssignableFrom(fact.getClass())) {
        return (T) fact;
      }
    }
    return (T) nullFactory;
  }

  private class NullFactory extends ParserAdapater {

    private NullFactory() {
      super(null);
    }
  }

  

}
