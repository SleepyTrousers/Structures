package crazypants.structures.gen.io;

import com.google.gson.annotations.Expose;

import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.gen.villager.VillagerTemplate;

/**
 * Used to save / load the resources
 */
public class ResourceWrapper {

  @Expose
  private IStructureTemplate structureTemplate;
  
  @Expose
  private IStructureGenerator structureGenerator;
  
  @Expose
  private VillagerTemplate villagerGenerator;

  public IStructureTemplate getStructureTemplate() {
    return structureTemplate;
  }

  public void setStructureTemplate(IStructureTemplate structureTemplate) {
    this.structureTemplate = structureTemplate;
  }

  public IStructureGenerator getStructureGenerator() {
    return structureGenerator;
  }

  public void setStructureGenerator(IStructureGenerator structureGenerator) {
    this.structureGenerator = structureGenerator;
  }

  public VillagerTemplate getVillagerTemplate() {
    return villagerGenerator;
  }

  public void setVillagerTemplate(VillagerTemplate villagerGenerator) {
    this.villagerGenerator = villagerGenerator;
  }
  
}
