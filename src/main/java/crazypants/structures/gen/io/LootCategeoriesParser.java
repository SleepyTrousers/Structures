package crazypants.structures.gen.io;

public final class LootCategeoriesParser {

  private LootCategeoriesParser() {
  }

  public static LootCategories parseLootCategories(String uid, String json) throws Exception {
    ResourceWrapper rw = GsonIO.INSTANCE.getGson().fromJson(json, ResourceWrapper.class);    
    if(rw != null && rw.getLootCategories() != null) {
      LootCategories res = rw.getLootCategories();
      res.setUid(uid);
      return res;
    } 
    throw new Exception("LootCategoriesParser: Not loot categories found in " + uid);    
  }

}
