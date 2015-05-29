package crazypants.structures.gen.io;

import com.google.gson.JsonObject;

import crazypants.structures.gen.structure.validator.IChunkValidator;

public interface IValidatorFactory extends IFactory {

  IChunkValidator createValidator(String uid, JsonObject json);

}
