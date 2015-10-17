package crazypants.structures.gen.io.resource;

import java.io.InputStream;
import java.util.List;

public interface IResourcePath {

  boolean exists(String name);
  
  InputStream getStream(String name);
  
  List<String> getChildren();
  
  List<String> getChildUids(String extension);
}
