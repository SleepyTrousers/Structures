package crazypants.structures.api.io;

import com.google.gson.JsonObject;

import crazypants.structures.api.runtime.ICondition;

public interface IConditionParser extends IParser {

  ICondition createCondition(String uid, JsonObject json);
}
