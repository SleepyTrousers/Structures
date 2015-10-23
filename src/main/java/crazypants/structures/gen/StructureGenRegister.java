package crazypants.structures.gen;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cpw.mods.fml.common.registry.VillagerRegistry;
import crazypants.structures.Log;
import crazypants.structures.api.gen.IStructureComponent;
import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.gen.IVillagerGenerator;
import crazypants.structures.gen.io.resource.IResourcePath;
import crazypants.structures.gen.io.resource.StructureResourceManager;
import crazypants.structures.gen.structure.StructureComponentNBT;
import crazypants.structures.gen.villager.CompositeCreationHandler;
import crazypants.structures.gen.villager.VillageHouse;
import net.minecraft.world.gen.structure.MapGenStructureIO;

public class StructureGenRegister {

  public static final StructureGenRegister instance = createInstance();

  private static StructureGenRegister createInstance() {
    StructureGenRegister reg = new StructureGenRegister();
    reg.init();
    return reg;
  }

  private final Map<String, IStructureGenerator> generators = new HashMap<String, IStructureGenerator>();
  private Map<String, IStructureTemplate> templates = new HashMap<String, IStructureTemplate>();
  private final Map<String, IStructureComponent> components = new HashMap<String, IStructureComponent>();
  //Keep these separately so they can be retried ever reload attempt
  private final Set<String> genUids = new HashSet<String>();
  private final Set<String> villagerUids = new HashSet<String>();

  //Need to use a composite handler as we can only have one handler per Village component class.
  //All our village houses use the same class, so we can only use one creation handler, which then delegates
  //to the 'per config' handlers
  private CompositeCreationHandler villagerGen;

  private StructureResourceManager resourceManager;

  private StructureGenRegister() {
  }

  private void init() {
    resourceManager = new StructureResourceManager(this);
    villagerGen = new CompositeCreationHandler();
    VillagerRegistry.instance().registerVillageCreationHandler(villagerGen);
    MapGenStructureIO.func_143031_a(VillageHouse.class, "EnderStructuresHouse");
  }

  public StructureResourceManager getResourceManager() {
    return resourceManager;
  }

  public void registerGenerator(IStructureGenerator gen) {
    if(gen == null) {
      return;
    }
    generators.put(gen.getUid(), gen);
    genUids.add(gen.getUid());
    Log.info("StructureGenRegister: Registered Structure Gen: " + gen.getUid());
  }

  public void registerVillagerGenerator(IVillagerGenerator gen) {
    if(gen == null) {
      return;
    }
    villagerUids.add(gen.getUid());
    villagerGen.addCreationHandler(gen.getCreationHandler());    
    gen.register();
    Log.info("StructureGenRegister: Registered Village Gen: " + gen.getUid());
  }

  public void registerTemplate(IStructureTemplate st) {
    if(st == null) {
      return;
    }
    templates.put(st.getUid(), st);

    Log.info("StructureGenRegister: Registered Template: " + st.getUid());
  }

  public void registerStructureComponent(IStructureComponent sc) {
    if(sc == null) {
      return;
    }
    components.put(sc.getUid(), sc);
    Log.info("StructureGenRegister: Registered Component: " + sc.getUid());
  }

  public Collection<IStructureGenerator> getGenerators() {
    return generators.values();
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

  public void loadAndRegisterAllResources(IResourcePath path, boolean onlyRootResources) {

    List<String> uids = path.getChildUids(StructureResourceManager.GENERATOR_EXT);
    for (String uid : uids) {
      try {
        registerGenerator(resourceManager.loadGenerator(uid));
      } catch (Exception e) {
        Log.warn("StructureResourceManager.loadAndRegisterAllResources: Error loading component " + uid + " Exception: " + e);
      }
    }

    uids = path.getChildUids(StructureResourceManager.VILLAGER_EXT);
    for (String uid : uids) {
      try {
        IVillagerGenerator gen = resourceManager.loadVillager(uid);
        if(gen != null) {
          registerVillagerGenerator(gen);
        }
      } catch (Exception e) {
        Log.warn("StructureResourceManager.loadAndRegisterAllResources: Error loading component " + uid + " Exception: " + e);
      }
    }

    uids = path.getChildUids(StructureResourceManager.LOOT_EXT);
    for (String uid : uids) {
      try {
        resourceManager.loadLootTableDefination(uid);
      } catch (Exception e) {
        Log.warn("StructureResourceManager.loadAndRegisterAllResources: Could not load loot table categories from: " + uid + " error: " + e);
      }
    }

    if(!onlyRootResources) {
      uids = path.getChildUids(StructureResourceManager.TEMPLATE_EXT);
      for (String uid : uids) {
        try {
          IStructureTemplate tmp = resourceManager.loadTemplate(uid);
          registerTemplate(tmp);
        } catch (Exception e) {
          Log.warn("StructureResourceManager.loadAndRegisterAllResources: Error loading template " + uid + " Exception: " + e);
        }
      }

      uids = path.getChildUids(StructureResourceManager.COMPONENT_EXT);
      for (String uid : uids) {
        try {
          StructureComponentNBT sc = resourceManager.loadStructureComponent(uid);
          registerStructureComponent(sc);
        } catch (Exception e) {
          Log.warn("StructureResourceManager.loadAndRegisterAllResources: Error loading component " + uid + " Exception: " + e);
        }
      }
    }

  }

  public void clear() {
    components.clear();
    generators.clear();
    villagerUids.clear();
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
      }
    }
    for (String uid : villagerUids) {
      IVillagerGenerator tmp;
      try {
        tmp = resourceManager.loadVillager(uid);
        if(tmp != null) {
          villagerUids.add(tmp.getUid());
          tmp.onReload();
        }
      } catch (Exception e) {
        Log.error("StructureRegister: Could not load structure data for " + uid + " Ex: " + e);
      }
    }

  }

  public Collection<IStructureTemplate> getStructureTemplates() {
    return templates.values();
  }

}
