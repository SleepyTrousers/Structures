package crazypants.structures.gen.io.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractResourcePath implements IResourcePath {


  @Override
  public List<String> getChildren(String extension) {
    List<String> kids = getChildren();
    if(kids == null || kids.isEmpty()|| extension == null) {
      return Collections.emptyList();
    }    
    if(!extension.startsWith(".")) {
      extension = "." + extension;
    }
    List<String> res = new ArrayList<String>();
    for (String kid : kids) {
      if(kid != null && kid.endsWith(extension)) {
        String uid = kid.substring(0, kid.length() - extension.length());
        res.add(uid);
      }
    }
    return res;
  }

}
