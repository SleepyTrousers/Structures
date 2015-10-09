package crazypants.structures.gen.io;

import com.google.gson.JsonObject;

import crazypants.structures.api.gen.IChunkValidator;

public interface IChunkValidatorParser extends IParser {

  IChunkValidator createChunkValidator(String uid, JsonObject json);

}
