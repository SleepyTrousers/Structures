package crazypants.structures.gen.io.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import scala.actors.threadpool.Arrays;

public class DirectoryResourcePath extends AbstractResourcePath {

  private final File dir;

  public DirectoryResourcePath(File root) {
    dir = root;
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public List<String> getChildren() {
    String[] kids = dir.list();
    if(kids == null || kids.length == 0) {
      return Collections.emptyList();
    }
    return Arrays.asList(kids);
  }

  @Override
  public boolean exists(String name) {
    if(dir == null) {
      return false;
    }
    return new File(dir, name).exists();
  }

  @Override
  public InputStream getStream(String name) {
    try {
      return new FileInputStream(new File(dir, name));
    } catch (FileNotFoundException e) {
      return null;
    }
  }

  public File getDirectory() {
    return dir;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dir == null) ? 0 : dir.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj)
      return true;
    if(obj == null)
      return false;
    if(getClass() != obj.getClass())
      return false;
    DirectoryResourcePath other = (DirectoryResourcePath) obj;
    if(dir == null) {
      if(other.dir != null)
        return false;
    } else if(!dir.equals(other.dir))
      return false;
    return true;
  }

}