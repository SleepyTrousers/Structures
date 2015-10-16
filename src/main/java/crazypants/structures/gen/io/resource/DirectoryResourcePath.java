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

}