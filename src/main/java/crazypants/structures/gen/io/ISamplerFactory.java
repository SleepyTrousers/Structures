package crazypants.structures.gen.io;

import com.google.gson.JsonObject;

import crazypants.structures.gen.structure.sampler.ILocationSampler;

public interface ISamplerFactory extends IFactory {

  ILocationSampler createSampler(String uid, JsonObject json);
  
}
