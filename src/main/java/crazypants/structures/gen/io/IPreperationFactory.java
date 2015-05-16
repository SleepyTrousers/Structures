package crazypants.structures.gen.io;

import com.google.gson.JsonObject;

import crazypants.structures.gen.structure.preperation.ISitePreperation;

public interface IPreperationFactory extends IFactory {

  ISitePreperation createPreperation(String uid, JsonObject json);

}
