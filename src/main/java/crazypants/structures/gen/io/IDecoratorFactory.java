package crazypants.structures.gen.io;

import com.google.gson.JsonObject;

import crazypants.structures.gen.structure.decorator.IDecorator;

public interface IDecoratorFactory {

  IDecorator createDecorator(String uid, JsonObject json);
  
}
