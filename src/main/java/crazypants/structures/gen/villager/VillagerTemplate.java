package crazypants.structures.gen.villager;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import crazypants.structures.api.ListElementType;
import crazypants.structures.api.gen.IResource;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.gen.IVillagerGenerator;
import net.minecraft.village.MerchantRecipe;

public class VillagerTemplate implements IResource {

  private String uid;

  @Expose
  private int weight;

  @Expose
  private int minGeneratedPerVillage;
  
  @Expose
  private int maxGeneratedPerVillage;

  @ListElementType(elementType = IStructureTemplate.class)
  @Expose
  private List<IStructureTemplate> plainsTemplates = new ArrayList<IStructureTemplate>();

  @ListElementType(elementType = IStructureTemplate.class)
  @Expose
  private List<IStructureTemplate> desertTemplates = new ArrayList<IStructureTemplate>();

  @Expose
  private int villagerId;

  @Expose
  private String texture;

  @ListElementType(elementType = MerchantRecipeWrapper.class)
  @Expose
  private List<MerchantRecipeWrapper> trades = new ArrayList<MerchantRecipeWrapper>();

  public boolean isValid() {
    if(villagerId > 0) {
      if(texture == null) {
        return false;
      }
      if(trades == null || trades.isEmpty()) {
        return false;
      }
    }
    if(uid == null) {
      return false;
    }
    if(plainsTemplates == null || plainsTemplates.isEmpty()) {
      return false;
    }
    if(weight <= 0) {
      return false;
    }
    if(maxGeneratedPerVillage <= 0) {
      return false;
    }
    return true;
  }

  public IVillagerGenerator createGenerator() {
    VillagerGenerator res = new VillagerGenerator(uid);

    for (IStructureTemplate tmpl : plainsTemplates) {
      if(tmpl != null) {
        res.addPlainsTemplate(tmpl.getUid());
      }
    }
    if(desertTemplates != null) {
      for (IStructureTemplate tmpl : desertTemplates) {
        if(tmpl != null) {
          res.addDesertTemplate(tmpl.getUid());
        }
      }
    }
    res.setWeight(weight, minGeneratedPerVillage, maxGeneratedPerVillage);
    
    res.setTexture(texture);
    res.setVillagerId(villagerId);
    
    if(trades != null) {
      for(MerchantRecipeWrapper rec : trades) {
        if(rec != null) {
          MerchantRecipe recipe = rec.createRecipe();
          if(recipe != null) {
            res.addRecipe(recipe);
          }
        }
      }
    }

    return res;
  }

  @Override
  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

}
