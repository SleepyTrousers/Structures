package crazypants.structures.gen.io;

import com.google.gson.JsonObject;

import crazypants.structures.api.gen.ILocationSampler;

public interface ISamplerFactory extends IFactory {

  ILocationSampler createSampler(String uid, JsonObject json);
  
}
