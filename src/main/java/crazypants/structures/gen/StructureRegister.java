package crazypants.structures.gen;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import crazypants.structures.Log;
import crazypants.structures.gen.io.StructureResourceManager;
import crazypants.structures.gen.structure.StructureGenerator;
import crazypants.structures.gen.structure.StructureTemplate;

public class StructureRegister {

  public static final StructureRegister instance = createInstance();

  private static StructureRegister createInstance() {
    StructureRegister reg = new StructureRegister();
    reg.init();
    return reg;
  }

  private final Map<String, StructureGenerator> generators = new HashMap<String, StructureGenerator>();
  private final Map<String, StructureTemplate> templates = new HashMap<String, StructureTemplate>();
  //Keep these separately so they can be retried ever reload attempt
  private final Set<String> genUids = new HashSet<String>();
  

  private StructureResourceManager resourceManager;

  private StructureRegister() {
  }

  private void init() {
    resourceManager = new StructureResourceManager(this);
  }

  public StructureResourceManager getResourceManager() {
    return resourceManager;
  }

  public void registerJsonGenerator(String json) throws Exception {
    StructureGenerator tp = resourceManager.parseJsonGenerator(json);
    registerGenerator(tp);
  }

  public void registerGenerator(StructureGenerator gen) {
    generators.put(gen.getUid(), gen);
    genUids.add(gen.getUid());
  }

  public StructureGenerator getGenerator(String uid) {
    return getGenerator(uid, false);
  }
  
  public StructureGenerator getGenerator(String uid, boolean doLoadIfNull) {
    if(uid == null) {
      return null;
    }
    StructureGenerator res = generators.get(uid);    
    if(res != null || !doLoadIfNull) {
      return res;
    }    
    try {
      res = resourceManager.loadGenerator(uid);      
    } catch (Exception e) {
      Log.error("StructureRegister: Could not load gnerator for " + uid + " Ex: " + e);
      e.printStackTrace();
    }
    if(res != null) {
      registerGenerator(res);
    }
    genUids.add(uid);
    return res;
  }

  public Collection<StructureGenerator> getGenerators() {
    return generators.values();
  }

  public void registerStructureTemplate(String uid, NBTTagCompound nbt) throws IOException {
    templates.put(uid, new StructureTemplate(nbt));
  }

  public void registerStructureTemplate(StructureTemplate st) {
    templates.put(st.getUid(), st);
  }

  public StructureTemplate getStructureTemplate(String uid) {
    return getStructureTemplate(uid, false);
  }
  
  public StructureTemplate getStructureTemplate(String uid, boolean doLoadIfNull) {
    if(!doLoadIfNull || templates.containsKey(uid)) {
      return templates.get(uid);
    }
    StructureTemplate sd = null;
    try {
      sd = resourceManager.loadStructureTemplate(uid);
    } catch (IOException e) {
      Log.error("StructureRegister: Could not load structure template for " + uid + " Ex: " + e);
      //e.printStackTrace();
    } finally {
      templates.put(uid, sd);
    }
    return sd;
  }

  public void reload() {
    templates.clear();
    generators.clear();
    for (String uid : genUids) { 
      StructureGenerator tmp;
      try {
        tmp = resourceManager.loadGenerator(uid);
        if(tmp != null) {
          registerGenerator(tmp);
        }
      } catch (Exception e) {
        Log.error("StructureRegister: Could not load structure data for " + uid + " Ex: " + e);
        //e.printStackTrace();
      }
    }

  }

}
