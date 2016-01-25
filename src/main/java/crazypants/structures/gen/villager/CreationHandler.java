package crazypants.structures.gen.villager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.gen.WeightedTemplate;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageCreationHandler;

public class CreationHandler implements IVillageCreationHandler {

  private int villagerId = -1;

  private final String uid;
  private final List<WeightedTemplate> templates = new ArrayList<WeightedTemplate>();
  private final List<WeightedTemplate> desertTemplates = new ArrayList<WeightedTemplate>();

  private PieceWeight weight;
  private int minNum;

  private String villagerSpawnLocation;

  public CreationHandler(String uid) {
    this.uid = uid;
    weight = new PieceWeight(StructuresVillageHouse.class, 9, 1);
  }

  public String getUid() {
    return uid;
  }

  public boolean hasVillager() {
    return villagerId > 0;
  }

  public int getVillagerId() {
    return villagerId;
  }

  public void setVilagerId(int id) {
    this.villagerId = id;
  }

  public void addPlainsTemplate(WeightedTemplate tmp) {
    templates.add(tmp);
  }

  public void addDesertTemplate(WeightedTemplate tmp) {
    desertTemplates.add(tmp);
  }

  public void setSpawnLocation(String villagerSpawnLocation) {
    this.villagerSpawnLocation = villagerSpawnLocation;
  }

  /** terraionType = World terrain type, 0 for normal, 1 for flat map */
  @Override
  public PieceWeight getVillagePieceWeight(Random random, int terrainType) {
    int maxNum = MathHelper.getRandomIntegerInRange(random, minNum, weight.villagePiecesLimit);
    return new PieceWeight(weight.villagePieceClass, weight.villagePieceWeight, maxNum);
  }

  public void setVillagePieceWeight(PieceWeight weight, int minNum) {
    this.weight = weight;
    this.minNum = minNum;
  }

  public void validate() throws Exception {
    if (weight == null) {
      throw new Exception("Weight not set for villager " + uid);
    }
    if (templates.isEmpty()) {
      throw new Exception("No plains templates specified for villager " + uid);
    }
  }

  @Override
  public Class<?> getComponentClass() {
    return StructuresVillageHouse.class;
  }

  @Override
  public Village buildComponent(PieceWeight villagePiece, Start startPiece, List<StructureComponent> pieces, Random random, int x, int y, int z,
      EnumFacing facing, int p5) {

    IStructureTemplate template = null;
    if (startPiece.inDesert && !desertTemplates.isEmpty()) {
      template = WeightedTemplate.getTemplate(desertTemplates);
    }
    if (template == null) {
      template = WeightedTemplate.getTemplate(templates);
    }
    if (template == null) {
      return null;
    }
    StructuresVillageHouse comp = new StructuresVillageHouse(template, villagerSpawnLocation, villagerId, x, y, z, facing);    
    if (StructureComponent.findIntersecting(pieces, comp.getBoundingBox()) != null || !canVillageGoDeeper(comp.getBoundingBox())) {
      return null;
    } 
    return comp;
  }

  protected boolean canVillageGoDeeper(StructureBoundingBox p_74895_0_) {
    return p_74895_0_ != null && p_74895_0_.minY > 10;
  }

}