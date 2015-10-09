package crazypants.structures.gen.io;

import com.google.gson.JsonObject;

import crazypants.structures.api.gen.IChunkValidator;
import crazypants.structures.api.gen.IDecorator;
import crazypants.structures.api.gen.ILocationSampler;
import crazypants.structures.api.gen.ISitePreperation;
import crazypants.structures.api.gen.ISiteValidator;

public abstract class AbstractSingleParserFactory implements ISiteValidatorParser, IChunkValidatorParser, ILocationSamplerParser, ISitePreperationParser, IDecoratorParser  {

  private final String uid;

  protected AbstractSingleParserFactory(String uid) {    
    this.uid = uid;
  }

  public String getUid() {
    return uid;
  }

  @Override
  public boolean canParse(String uid) {    
    return uid != null && uid.equals(this.uid);
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
  
  
  
}
