package crazypants.structures.api.gen;

import crazypants.structures.gen.villager.CreationHandler;

public interface IVillagerGenerator extends IResource {

  CreationHandler getCreationHandler();
  
  void register();
  
  void onReload();
  
}
