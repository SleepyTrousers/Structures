package crazypants.structures.gen.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import crazypants.structures.api.ITyped;

public class TypeRegister {

  public static TypeRegister INSTANCE = new TypeRegister();

  private Map<String, ITyped> types = new HashMap<String, ITyped>();

  private TypeRegister() {
  }

  public void register(ITyped type) {
    if(type == null) {
      return;
    }
    types.put(type.getType(), type);
  }

  @SuppressWarnings("unchecked")
  public <T extends ITyped> List<T> getTypesOfType(Class<T> typeOfType) {
    List<T> res = new ArrayList<T>();
    for (ITyped type : types.values()) {
      if(type != null) {
        if(typeOfType.isAssignableFrom(type.getClass())) {
          res.add((T)type);
        }
      }
    }
    return res;
  }

}
