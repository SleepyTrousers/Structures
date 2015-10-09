package crazypants.structures.gen.io;

import com.google.gson.JsonObject;

import crazypants.structures.api.gen.ISiteValidator;

public interface ISiteValidatorFactory {

  ISiteValidator createSiteValidator(String uid, JsonObject json);
  
}
