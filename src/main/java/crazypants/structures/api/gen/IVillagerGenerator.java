package crazypants.structures.api.gen;

import crazypants.structures.gen.villager.CreationHandler;

public interface IVillagerGenerator {

  String getUid();
  
  CreationHandler getCreationHandler();
  
  void register();
  
  void onReload();
  
}
