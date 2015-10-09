package crazypants.structures.gen.io;

import com.google.gson.JsonObject;

import crazypants.structures.api.gen.ISitePreperation;

public interface IPreperationFactory extends IFactory {

  ISitePreperation createPreperation(String uid, JsonObject json);

}
