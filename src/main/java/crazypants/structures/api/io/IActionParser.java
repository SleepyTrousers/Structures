package crazypants.structures.api.io;

import com.google.gson.JsonObject;

import crazypants.structures.api.runtime.IAction;

public interface IActionParser extends IParser {

  IAction createAction(String uid, JsonObject json);
  
}
