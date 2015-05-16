package crazypants.structures.gen.io;

import com.google.gson.JsonObject;

import crazypants.structures.gen.structure.validator.ILocationValidator;

public interface IValidatorFactory extends IFactory {

  ILocationValidator createValidator(String uid, JsonObject json);

}
