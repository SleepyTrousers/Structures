package crazypants.structures.gen.io;

import com.google.gson.annotations.Expose;

import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.gen.IVillagerGenerator;

/**
 * Used to save / load the templates
 */
public class ResourceWrapper {

  @Expose
  private IStructureTemplate structureTemplate;
  
  @Expose
  private IStructureGenerator structureGenerator;
  
  @Expose
  private IVillagerGenerator villagerGenerator;

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

  public IVillagerGenerator getVillagerGenerator() {
    return villagerGenerator;
  }

  public void setVillagerGenerator(IVillagerGenerator villagerGenerator) {
    this.villagerGenerator = villagerGenerator;
  }
  
}
