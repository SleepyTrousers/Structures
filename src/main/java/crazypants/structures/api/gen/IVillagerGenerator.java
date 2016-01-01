package crazypants.structures.api.gen;

import crazypants.structures.gen.structure.loot.LootCategories;
import crazypants.structures.gen.villager.CreationHandler;

public interface IVillagerGenerator extends IResource {

  CreationHandler getCreationHandler();
  
  LootCategories getLootCategories();
  
  void register();
  
  void onReload();
  
}
