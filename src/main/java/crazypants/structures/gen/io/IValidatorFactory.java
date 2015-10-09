package crazypants.structures.gen.io;

import com.google.gson.JsonObject;

import crazypants.structures.api.gen.IChunkValidator;

public interface IValidatorFactory extends IFactory {

  IChunkValidator createValidator(String uid, JsonObject json);

}
