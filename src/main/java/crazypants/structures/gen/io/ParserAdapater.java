package crazypants.structures.gen.io;

import com.google.gson.JsonObject;

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
import crazypants.structures.api.io.ISitePreperationParser;
import crazypants.structures.api.io.ISiteValidatorParser;
import crazypants.structures.api.runtime.IAction;
import crazypants.structures.api.runtime.IBehaviour;
import crazypants.structures.api.runtime.ICondition;

public abstract class ParserAdapater implements ISiteValidatorParser, IChunkValidatorParser, ILocationSamplerParser, ISitePreperationParser, IDecoratorParser, IBehaviourParser, IConditionParser, IActionParser  {

  private final String type;

  protected ParserAdapater(String uid) {    
    this.type = uid;
  }

  public String getUid() {
    return type;
  }

  @Override
  public boolean canParse(String uid) {    
    return uid != null && uid.equals(this.type);
  }

  @Override
  public ILocationSampler createSampler(String uid, JsonObject json) {
    return null;
  }

  @Override
  public IChunkValidator createChunkValidator(String uid, JsonObject json) {
    return null;
  }

  @Override
  public ISiteValidator createSiteValidator(String uid, JsonObject json) {
    return null;
  }

  @Override
  public ISitePreperation createPreperation(String uid, JsonObject json) {
    return null;
  }

  @Override
  public IDecorator createDecorator(String uid, JsonObject json) {
    return null;
  }

  @Override
  public IBehaviour createBehaviour(String uid, JsonObject json) {  
    return null;
  }

  @Override
  public ICondition createCondition(String uid, JsonObject json) {  
    return null;
  }

  @Override
  public IAction createAction(String uid, JsonObject json) {  
    return null;
  }
  
}
