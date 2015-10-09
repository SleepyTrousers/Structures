package crazypants.structures.gen.io;

import com.google.gson.JsonObject;

import crazypants.structures.api.gen.IDecorator;

public interface IDecoratorFactory {

  IDecorator createDecorator(String uid, JsonObject json);
  
}
