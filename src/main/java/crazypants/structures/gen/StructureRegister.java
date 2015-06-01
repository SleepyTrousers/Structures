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
import crazypants.structures.gen.structure.StructureComponent;
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
  private Map<String, StructureTemplate> templates = new HashMap<String, StructureTemplate>();
  private final Map<String, StructureComponent> components = new HashMap<String, StructureComponent>();
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

  public void registerStructureComponent(String uid, NBTTagCompound nbt) throws IOException {
    components.put(uid, new StructureComponent(nbt));
  }

  public void registerStructureComponent(StructureComponent st) {
    components.put(st.getUid(), st);
  }
  
  public Collection<StructureComponent> getStructureComponents() {
    return components.values();    
  }

  public StructureComponent getStructureComponent(String uid) {
    return getStructureComponent(uid, false);
  }
  
  public StructureComponent getStructureComponent(String uid, boolean doLoadIfNull) {
    if(!doLoadIfNull || components.containsKey(uid)) {
      return components.get(uid);
    }
    StructureComponent sd = null;
    try {
      sd = resourceManager.loadStructureComponent(uid);
    } catch (IOException e) {
      Log.error("StructureRegister: Could not load structure component: " + uid + " Ex: " + e);
    } finally {
      components.put(uid, sd);
    }
    return sd;
  }
  
  public StructureTemplate getStructureTemplate(String uid, boolean doLoadIfNull) {
    if(!doLoadIfNull || templates.containsKey(uid)) {
      return templates.get(uid);
    }
    StructureTemplate sd = null;
    try {
      sd = resourceManager.loadTemplate(uid);
    } catch (Exception e) {
      Log.error("StructureRegister: Could not load structure template: " + uid + " Ex: " + e);
    } finally {
      templates.put(uid, sd);
    }
    return sd;
  }

  public void reload() {
    components.clear();
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

  public Collection<StructureTemplate> getStructureTemplates() {
    return templates.values();
  }

}
