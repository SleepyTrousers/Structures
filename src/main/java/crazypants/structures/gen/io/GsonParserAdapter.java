package crazypants.structures.gen.io;

import com.google.gson.JsonObject;

import crazypants.structures.api.ITyped;
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
  
  public GsonParserAdapter(ITyped typed) {
    this(typed.getType(), typed.getClass());
  }
  
  public GsonParserAdapter(String type, Class<?> clz) {
    super(type);
    this.clz = clz;        
  }

  @Override
  public ILocationSampler createSampler(String uid, JsonObject json) {    
    if(ILocationSampler.class.isAssignableFrom(clz)) {
      return (ILocationSampler) GsonIO.INSTANCE.getGson().fromJson(json, clz);
    }
    return null;
  }

  @Override
  public IChunkValidator createChunkValidator(String uid, JsonObject json) {
    if(IChunkValidator.class.isAssignableFrom(clz)) {
      return (IChunkValidator) GsonIO.INSTANCE.getGson().fromJson(json, clz);
    }
    return null;
  }

  @Override
  public ISiteValidator createSiteValidator(String uid, JsonObject json) {
    if(ISiteValidator.class.isAssignableFrom(clz)) {
      return (ISiteValidator) GsonIO.INSTANCE.getGson().fromJson(json, clz);
    }
    return null;
  }

  @Override
  public ISitePreperation createPreperation(String uid, JsonObject json) {
    if(ISitePreperation.class.isAssignableFrom(clz)) {
      return (ISitePreperation) GsonIO.INSTANCE.getGson().fromJson(json, clz);
    }
    return null;
  }

  @Override
  public IDecorator createDecorator(String uid, JsonObject json) {
    if(IDecorator.class.isAssignableFrom(clz)) {
      return (IDecorator) GsonIO.INSTANCE.getGson().fromJson(json, clz);
    }
    return null;
  }

  @Override
  public IBehaviour createBehaviour(String uid, JsonObject json) {
    if(IBehaviour.class.isAssignableFrom(clz)) {
      return (IBehaviour) GsonIO.INSTANCE.getGson().fromJson(json, clz);
    }
    return null;
  }

  @Override
  public ICondition createCondition(String uid, JsonObject json) {
    if(ICondition.class.isAssignableFrom(clz)) {
      return (ICondition) GsonIO.INSTANCE.getGson().fromJson(json, clz);
    }
    return null;
  }

  @Override
  public IAction createAction(String uid, JsonObject json) {
    if(IAction.class.isAssignableFrom(clz)) {
      return (IAction) GsonIO.INSTANCE.getGson().fromJson(json, clz);
    }
    return null;
  }
  
  
  
  

}
