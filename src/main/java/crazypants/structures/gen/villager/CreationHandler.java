package crazypants.structures.gen.villager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;

public class CreationHandler implements IVillageCreationHandler {
   
  private int villagerId = -1;
  
  private final String uid;
  private final List<String> templates = new ArrayList<String>();
  private final List<String> desertTemplates = new ArrayList<String>();
  
  private PieceWeight weight;

  public CreationHandler(String uid) {
    this.uid = uid;
    weight = new PieceWeight(VillageHouse.class, 9, 1);
  }

  public int getVillagerId() {
    return villagerId;
  }
  
  public void setVilagerId(int id) {
    this.villagerId = id;
  }
  
  public void addPlainsTemplate(String tmp) {
    templates.add(tmp);
  }
  
  public void addDesertTemplate(String tmp) {
    desertTemplates.add(tmp);
  }

  @Override
  public PieceWeight getVillagePieceWeight(Random random, int i) {
    return new PieceWeight(weight.villagePieceClass, weight.villagePieceWeight,weight.villagePiecesLimit);
  }
  
  public void setVillagePieceWeight(PieceWeight weight) {
    this.weight = weight;
  }

  public void validate() throws Exception {
    if(weight == null) {
      throw new Exception("Weight not set for villager " + uid);
    }
    if(villagerId < 0) {
      throw new Exception("Invalid villager id: " + villagerId + " for villager " + uid);
    }
    if(templates.isEmpty()) {
      throw new Exception("No plains templates specified for villager " + uid);
    }
  }
  
  @Override
  public Class<?> getComponentClass() {
    return VillageHouse.class;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Object buildComponent(PieceWeight villagePiece, Start startPiece, List pieces, Random random, int x, int y, int z, int coordBaseMode, int p5) {
    
    String templateUid;
    if(startPiece.inDesert && !desertTemplates.isEmpty()) {
      templateUid = desertTemplates.get(random.nextInt(desertTemplates.size()));
    } else {
      templateUid = templates.get(random.nextInt(templates.size()));
    }
    VillageHouse comp = new VillageHouse(templateUid, villagerId, x, y, z, coordBaseMode);

    VillageHouse res = canVillageGoDeeper(comp.getBoundingBox()) && StructureComponent.findIntersecting(pieces, comp.getBoundingBox()) == null
        ? comp : null;
    
    return res;
  }

  protected boolean canVillageGoDeeper(StructureBoundingBox p_74895_0_) {
    return p_74895_0_ != null && p_74895_0_.minY > 10;
  }

}