package crazypants.structures.api.io;

import com.google.gson.JsonObject;

import crazypants.structures.api.runtime.IBehaviour;

public interface IBehaviourParser extends IParser {

  IBehaviour createBehaviour(String uid, JsonObject json);
}
