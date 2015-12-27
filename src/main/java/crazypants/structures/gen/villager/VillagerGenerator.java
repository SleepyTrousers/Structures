package crazypants.structures.gen.villager;

import cpw.mods.fml.common.registry.VillagerRegistry;
import crazypants.structures.Log;
import crazypants.structures.api.gen.IVillagerGenerator;
import crazypants.structures.api.gen.WeightedTemplate;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;

public class VillagerGenerator implements IVillagerGenerator {

  private final String uid;

  private final TradeHandler tradeHandler;

  private final CreationHandler creationHandler;

  private ResourceLocation texture;

  public VillagerGenerator(String uid) {
    this.uid = uid;
    tradeHandler = new TradeHandler();
    creationHandler = new CreationHandler(uid);
    setWeight(9, 1, 1);
  }

  public String getUid(String uid) {
    return uid;
  }

  @Override
  public String getUid() {
    return uid;
  }

  public int getVillagerId() {
    return creationHandler.getVillagerId();
  }

  public void setVillagerId(int id) {
    creationHandler.setVilagerId(id);
  }

  public void setTexture(String texture) {
    this.texture = new ResourceLocation(texture);
  }

  public void setWeight(int weight, int minNum, int maxNum) {
    creationHandler.setVillagePieceWeight(new PieceWeight(VillageHouse.class, weight, maxNum), minNum);
  }

  public void addPlainsTemplate(WeightedTemplate template) {
    creationHandler.addPlainsTemplate(template);
  }

  public void addDesertTemplate(WeightedTemplate template) {
    creationHandler.addDesertTemplate(template);
  }

  public void addRecipe(MerchantRecipe recipe) {
    tradeHandler.addRecipe(recipe);
  }

  @Override
  public CreationHandler getCreationHandler() {
    return creationHandler;
  }
  
  @Override
  public void register() {
    if(getVillagerId() > 0) {
      if(!VillagerRegistry.getRegisteredVillagers().contains(getVillagerId())) {
        VillagerRegistry.instance().registerVillagerId(getVillagerId());
      }
    }
    onReload();
  }

  @Override
  public void onReload() {
    if(creationHandler.hasVillager()) {      
      VillagerRegistry.instance().registerVillagerSkin(getVillagerId(), texture);
      VillagerRegistry.instance().registerVillageTradeHandler(getVillagerId(), tradeHandler);
    };
  }
  
  public void validate() throws Exception {
    if(creationHandler.hasVillager()) {
      if(texture == null) {
        Log.warn("VillagerGenerator.register: No texture specified for villager " + uid);
      }

      if(!tradeHandler.hasTrades()) {
        Log.warn("VillagerGenerator.register: No trades added for villager" + uid);
      }
    }
    creationHandler.validate();
  }

}
