package crazypants.structures.gen;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import crazypants.structures.Log;
import crazypants.structures.api.gen.IStructureComponent;
import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.gen.io.StructureResourceManager;
import crazypants.structures.gen.structure.StructureComponentNBT;
import net.minecraft.nbt.NBTTagCompound;

public class StructureRegister {

  public static final StructureRegister instance = createInstance();

  private static StructureRegister createInstance() {
    StructureRegister reg = new StructureRegister();
    reg.init();
    return reg;
  }

  private final Map<String, IStructureGenerator> generators = new HashMap<String, IStructureGenerator>();
  private Map<String, IStructureTemplate> templates = new HashMap<String, IStructureTemplate>();
  private final Map<String, IStructureComponent> components = new HashMap<String, IStructureComponent>();
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

  public void registerGenerator(IStructureGenerator gen) {
    generators.put(gen.getUid(), gen);
    genUids.add(gen.getUid());
  }
  
//  public void registerJsonGenerator(String json) throws Exception {
//    IStructureGenerator tp = resourceManager.parseJsonGenerator(json);
//    registerGenerator(tp);
//  }

//  public IStructureGenerator getGenerator(String uid) {
//    return getGenerator(uid, false);
//  }
  
//  public IStructureGenerator getGenerator(String uid, boolean doLoadIfNull) {
//    if(uid == null) {
//      return null;
//    }
//    IStructureGenerator res = generators.get(uid);    
//    if(res != null || !doLoadIfNull) {
//      return res;
//    }    
//    try {
//      res = resourceManager.loadGenerator(uid);      
//    } catch (Exception e) {
//      Log.error("StructureRegister: Could not load gnerator for " + uid + " Ex: " + e);
//      e.printStackTrace();
//    }
//    if(res != null) {
//      registerGenerator(res);
//    }
//    genUids.add(uid);
//    return res;
//  }

  public Collection<IStructureGenerator> getGenerators() {
    return generators.values();
  }

  public void registerStructureComponent(String uid, NBTTagCompound nbt) throws IOException {
    components.put(uid, new StructureComponentNBT(nbt));
  }

  public void registerStructureComponent(IStructureComponent st) {
    components.put(st.getUid(), st);
  }
  
  public Collection<IStructureComponent> getStructureComponents() {
    return components.values();    
  }

  public IStructureComponent getStructureComponent(String uid) {
    return getStructureComponent(uid, false);
  }
  
  public IStructureComponent getStructureComponent(String uid, boolean doLoadIfNull) {
    if(!doLoadIfNull || components.containsKey(uid)) {
      return components.get(uid);
    }
    StructureComponentNBT sd = null;
    try {
      sd = resourceManager.loadStructureComponent(uid);
    } catch (IOException e) {
      Log.error("StructureRegister: Could not load structure component: " + uid + " Ex: " + e);
    } finally {
      components.put(uid, sd);
    }
    return sd;
  }
  
  public IStructureTemplate getStructureTemplate(String uid, boolean doLoadIfNull) {
    if(!doLoadIfNull || templates.containsKey(uid)) {
      return templates.get(uid);
    }
    IStructureTemplate sd = null;
    try {
      sd = resourceManager.loadTemplate(uid);
    } catch (Exception e) {
      Log.error("StructureRegister: Could not load structure template: " + uid + " Ex: " + e);
    } finally {
      templates.put(uid, sd);
    }
    return sd;
  }
  
  public void clear() {
    components.clear();
    generators.clear();
  }
  

  public void reload() {
    clear();
    for (String uid : genUids) { 
      IStructureGenerator tmp;
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

  public Collection<IStructureTemplate> getStructureTemplates() {
    return templates.values();
  }

  

}
