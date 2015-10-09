package crazypants.structures.gen.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

import crazypants.structures.api.gen.IChunkValidator;
import crazypants.structures.api.gen.IDecorator;
import crazypants.structures.api.gen.ILocationSampler;
import crazypants.structures.api.gen.ISitePreperation;
import crazypants.structures.api.gen.ISiteValidator;

public class CompositeRuleFactory implements IRuleFactory {

  private final List<IRuleFactory> factories = new ArrayList<IRuleFactory>();
  private final Map<String, IRuleFactory> uidFactories = new HashMap<String, IRuleFactory>();

  public void add(IRuleFactory factory) {
    factories.add(factory);
  }

  @Override
  public boolean canCreate(String uid) {
    return getFactory(uid) != null;
  }

  private IRuleFactory getFactory(String uid) {
    if (!uidFactories.containsKey(uid)) {
      //make sure we only do this once
      uidFactories.put(uid, null);
      for (IRuleFactory f : factories) {
        if (f.canCreate(uid)) {
          uidFactories.put(uid, f);
          return f;
        }
      }
    }
    return uidFactories.get(uid);
  }

  @Override
  public ILocationSampler createSampler(String uid, JsonObject json) {
    IRuleFactory f = getFactory(uid);
    if (f != null) {
      return f.createSampler(uid, json);
    }
    return null;
  }

  @Override
  public IChunkValidator createValidator(String uid, JsonObject json) {
    IRuleFactory f = getFactory(uid);
    if (f != null) {
      return f.createValidator(uid, json);
    }
    return null;
  }

  @Override
  public ISitePreperation createPreperation(String uid, JsonObject json) {
    IRuleFactory f = getFactory(uid);
    if (f != null) {
      return f.createPreperation(uid, json);
    }
    return null;
  }

  @Override
  public ISiteValidator createSiteValidator(String uid, JsonObject json) {
    IRuleFactory f = getFactory(uid);
    if (f != null) {
      return f.createSiteValidator(uid, json);
    }
    return null;
  }

  @Override
  public IDecorator createDecorator(String uid, JsonObject json) {    
    IRuleFactory f = getFactory(uid);
    if (f != null) {
      return f.createDecorator(uid, json);
    }
    return null;
  }
  
  

}
