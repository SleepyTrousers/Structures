package crazypants.structures.api.io;

import com.google.gson.JsonObject;

import crazypants.structures.api.gen.ILocationSampler;

public interface ILocationSamplerParser extends IParser {

  ILocationSampler createSampler(String uid, JsonObject json);
  
}
