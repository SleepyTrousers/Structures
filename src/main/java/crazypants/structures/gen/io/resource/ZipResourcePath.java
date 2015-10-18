package crazypants.structures.gen.io.resource;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import crazypants.structures.Log;

public class ZipResourcePath extends AbstractResourcePath {

  private final ZipFile zf;

  public ZipResourcePath(File root) {
    ZipFile f = null;
    try {
      f = new ZipFile(root);
    } catch (Exception e) {
      e.printStackTrace();
      Log.warn("Could not read zip file: " + root);      
    }
    zf = f;
  }

  @Override
  public boolean exists(String name) {    
    if(zf == null) {
      return false;
    }
    return zf.getEntry(name) != null;
  }

  @Override
  public InputStream getStream(String name) {
    if(zf == null) {
      return null;
    }
    ZipEntry ze = zf.getEntry(name);
    if(ze == null) {
      return null;
    }    
    InputStream is = null;
    try {
      is = zf.getInputStream(ze);
    } catch (Exception e) {
      return null;
    }    
    return  is;
  }

  @Override
  public List<String> getChildren() {
    if(zf == null) {
      return Collections.emptyList();
    }
    List<String> result = new ArrayList<String>();
    Enumeration<? extends ZipEntry> entries = zf.entries();    
    while(entries.hasMoreElements()) {
      ZipEntry ze = entries.nextElement();
      if(!ze.isDirectory()) {
        result.add(ze.getName());
      }
    }
    return result;
  }

}
