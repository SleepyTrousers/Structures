package crazypants.structures.gen.io;

import com.google.gson.annotations.Expose;

import crazypants.structures.gen.structure.StructureGenerator;
import crazypants.structures.gen.structure.StructureTemplate;
import crazypants.structures.gen.structure.loot.LootCategories;
import crazypants.structures.gen.villager.VillagerTemplate;

/**
 * Used to save / load the resources
 */
public class ResourceWrapper {

  @Expose
  private StructureTemplate structureTemplate;

  @Expose
  private StructureGenerator structureGenerator;

  @Expose
  private VillagerTemplate villagerGenerator;

  @Expose
  private LootCategories lootCategories;
                         
  public StructureTemplate getStructureTemplate() {
    return structureTemplate;
  }

  public void setStructureTemplate(StructureTemplate structureTemplate) {
    this.structureTemplate = structureTemplate;
  }

  public StructureGenerator getStructureGenerator() {
    return structureGenerator;
  }

  public void setStructureGenerator(StructureGenerator structureGenerator) {
    this.structureGenerator = structureGenerator;
  }

  public VillagerTemplate getVillagerTemplate() {
    return villagerGenerator;
  }

  public void setVillagerTemplate(VillagerTemplate villagerGenerator) {
    this.villagerGenerator = villagerGenerator;
  }

  public VillagerTemplate getVillagerGenerator() {
    return villagerGenerator;
  }

  public void setVillagerGenerator(VillagerTemplate villagerGenerator) {
    this.villagerGenerator = villagerGenerator;
  }

  public LootCategories getLootCategories() {
    return lootCategories;
  }

  public void setLootCategories(LootCategories lootCategories) {
    this.lootCategories = lootCategories;
  }

}
