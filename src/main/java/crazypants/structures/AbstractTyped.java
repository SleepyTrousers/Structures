package crazypants.structures;

import com.google.gson.annotations.Expose;

import crazypants.structures.api.ITyped;

public abstract class AbstractTyped implements ITyped {

  @Expose
  private String type;
  
  protected AbstractTyped(String type) {
    this.type = type;
  }

  @Override
  public String getType() {  
    return type;
  }
  
}
