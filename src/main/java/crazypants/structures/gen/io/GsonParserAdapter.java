package crazypants.structures.gen.io;

import static crazypants.structures.gen.io.GsonIO.GSON;

import com.google.gson.JsonObject;

import crazypants.structures.api.gen.IChunkValidator;
import crazypants.structures.api.gen.IDecorator;
import crazypants.structures.api.gen.ILocationSampler;
import crazypants.structures.api.gen.ISitePreperation;
import crazypants.structures.api.gen.ISiteValidator;
import crazypants.structures.api.runtime.IAction;
import crazypants.structures.api.runtime.IBehaviour;
import crazypants.structures.api.runtime.ICondition;

public class GsonParserAdapter extends ParserAdapater {

  private Class<?> clz;
  
  public GsonParserAdapter(String uid, Class<?> clz) {
    super(uid);
    this.clz = clz;        
  }

  @Override
  public ILocationSampler createSampler(String uid, JsonObject json) {    
    if(ILocationSampler.class.isAssignableFrom(clz)) {
      return (ILocationSampler) GSON.fromJson(json, clz);
    }
    return null;
  }

  @Override
  public IChunkValidator createChunkValidator(String uid, JsonObject json) {
    if(IChunkValidator.class.isAssignableFrom(clz)) {
      return (IChunkValidator) GSON.fromJson(json, clz);
    }
    return null;
  }

  @Override
  public ISiteValidator createSiteValidator(String uid, JsonObject json) {
    if(ISiteValidator.class.isAssignableFrom(clz)) {
      return (ISiteValidator) GSON.fromJson(json, clz);
    }
    return null;
  }

  @Override
  public ISitePreperation createPreperation(String uid, JsonObject json) {
    if(ISitePreperation.class.isAssignableFrom(clz)) {
      return (ISitePreperation) GSON.fromJson(json, clz);
    }
    return null;
  }

  @Override
  public IDecorator createDecorator(String uid, JsonObject json) {
    if(IDecorator.class.isAssignableFrom(clz)) {
      return (IDecorator) GSON.fromJson(json, clz);
    }
    return null;
  }

  @Override
  public IBehaviour createBehaviour(String uid, JsonObject json) {
    if(IBehaviour.class.isAssignableFrom(clz)) {
      return (IBehaviour) GSON.fromJson(json, clz);
    }
    return null;
  }

  @Override
  public ICondition createCondition(String uid, JsonObject json) {
    if(ICondition.class.isAssignableFrom(clz)) {
      return (ICondition) GSON.fromJson(json, clz);
    }
    return null;
  }

  @Override
  public IAction createAction(String uid, JsonObject json) {
    if(IAction.class.isAssignableFrom(clz)) {
      return (IAction) GSON.fromJson(json, clz);
    }
    return null;
  }
  
  
  
  

}
