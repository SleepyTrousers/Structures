package crazypants.structures.api.io;

import com.google.gson.JsonObject;

import crazypants.structures.api.gen.ISitePreperation;

public interface ISitePreperationParser extends IParser {

  ISitePreperation createPreperation(String uid, JsonObject json);

}
