package crazypants.structures.gen.villager;

import crazypants.structures.Log;
import crazypants.structures.api.gen.IVillagerGenerator;
import crazypants.structures.api.gen.WeightedTemplate;
import crazypants.structures.gen.structure.loot.LootCategories;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;

public class VillagerGenerator implements IVillagerGenerator {

  private final String uid;

  private final TradeHandler tradeHandler;

  private final CreationHandler creationHandler;

  private ResourceLocation texture;
  
  private LootCategories lootCategories;

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
    if(texture == null) {
      this.texture = null; 
    } else {
      this.texture = new ResourceLocation(texture);
    }
  }

  public void setWeight(int weight, int minNum, int maxNum) {
    creationHandler.setVillagePieceWeight(new PieceWeight(StructuresVillageHouse.class, weight, maxNum), minNum);
  }

  public void setSpawnLocation(String villagerSpawnLocation) {
    creationHandler.setSpawnLocation(villagerSpawnLocation);
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

  public void setLootCategories(LootCategories lootCategories) {
    this.lootCategories = lootCategories;
  }
  
  @Override
  public LootCategories getLootCategories() {
    return lootCategories;
  }

  @Override
  public CreationHandler getCreationHandler() {
    return creationHandler;
  }

  @Override
  public void register() {
    if(getVillagerId() > 0) {
    //TODO: 1.8
//      if(!VillagerRegistry.getRegisteredVillagers().contains(getVillagerId())) {
//        VillagerRegistry.instance().registerVillagerId(getVillagerId());
//      }
    }
    onReload();
  }

  @Override
  public void onReload() {
    if(creationHandler.hasVillager()) {
      //TODO: 1.8
//      VillagerRegistry.instance().registerVillagerSkin(getVillagerId(), texture);
//      VillagerRegistry.instance().registerVillageTradeHandler(getVillagerId(), tradeHandler);
    }    
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
