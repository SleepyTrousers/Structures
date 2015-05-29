package crazypants.structures.gen.io;

import com.google.gson.JsonObject;

import crazypants.structures.gen.structure.validator.ISiteValidator;

public interface ISiteValidatorFactory {

  ISiteValidator createSiteValidator(String uid, JsonObject json);
  
}
