package crazypants.structures.gen.villager;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import crazypants.structures.api.AttributeDoc;
import crazypants.structures.api.AttributeEditor;
import crazypants.structures.api.ListElementType;
import crazypants.structures.api.gen.IResource;
import crazypants.structures.api.gen.IVillagerGenerator;
import crazypants.structures.api.gen.WeightedTemplate;
import crazypants.structures.gen.io.resource.StructureResourceManager;
import net.minecraft.village.MerchantRecipe;

public class VillagerTemplate implements IResource {

  @AttributeDoc(text = "The unique name of the template. Corresponds to a file with the same name and " + StructureResourceManager.VILLAGER_EXT + " extension")
  private String uid;

  @AttributeDoc(text = "The heigher the weight, the higher the chance the village building will generate. "
      + "For example, a vanilla church and house has a wieght of 20 while a blacksmith has a weight of 15")
  @Expose
  private int weight;

  @AttributeDoc(text = "The minimum number to generate per village")
  @Expose
  private int minGeneratedPerVillage;

  @AttributeDoc(text = "The maximum number to generate per village")
  @Expose
  private int maxGeneratedPerVillage;

  @AttributeDoc(text = "The template to use for generating the village building. One template is chosen for each building constructed.")
  @ListElementType(elementType = WeightedTemplate.class)
  @Expose
  private List<WeightedTemplate> plainsTemplates = new ArrayList<WeightedTemplate>();

  @AttributeDoc(text = "The template to use for generating the village building in a desert. If none are added the plains templates will be used.")
  @ListElementType(elementType = WeightedTemplate.class)
  @Expose
  private List<WeightedTemplate> desertTemplates = new ArrayList<WeightedTemplate>();

  @AttributeDoc(text = "The unique ID for the villager. Must not clash with other mods. New buildings can be added to a village "
      + "without creating a new villager by setting thsi to -1.")
  @Expose
  private int villagerId;
  
  @AttributeDoc(text = "The name of image used as the villagers texture. Images loaded from a config dir or zip file must be "
      + "prepended with 'esresource:' For example, esresource:myImage.png.")
  @AttributeEditor(name = "texture")
  @Expose
  private String texture;

  @AttributeDoc(text = "Adds custum villager trades")
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
    if(!hasValidTemplate(plainsTemplates)) {
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

  private boolean hasValidTemplate(List<WeightedTemplate> templates) {
    if(templates == null || templates.isEmpty()) {
      return false;
    }
    for (WeightedTemplate tmpl : templates) {
      if(tmpl.getTemplate() == null) {
        return false;
      }
    }
    return true;
  }

  public IVillagerGenerator createGenerator() {
    VillagerGenerator res = new VillagerGenerator(uid);

    for (WeightedTemplate tmpl : plainsTemplates) {
      if(tmpl != null && tmpl.getTemplate() != null) {
        res.addPlainsTemplate(tmpl);
      }
    }
    if(desertTemplates != null) {
      for (WeightedTemplate tmpl : desertTemplates) {
        if(tmpl != null && tmpl.getTemplate() != null) {
          res.addDesertTemplate(tmpl);
        }
      }
    }
    res.setWeight(weight, minGeneratedPerVillage, maxGeneratedPerVillage);

    res.setTexture(texture);
    res.setVillagerId(villagerId);

    if(trades != null) {
      for (MerchantRecipeWrapper rec : trades) {
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

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public int getMinGeneratedPerVillage() {
    return minGeneratedPerVillage;
  }

  public void setMinGeneratedPerVillage(int minGeneratedPerVillage) {
    this.minGeneratedPerVillage = minGeneratedPerVillage;
  }

  public int getMaxGeneratedPerVillage() {
    return maxGeneratedPerVillage;
  }

  public void setMaxGeneratedPerVillage(int maxGeneratedPerVillage) {
    this.maxGeneratedPerVillage = maxGeneratedPerVillage;
  }

  public List<WeightedTemplate> getPlainsTemplates() {
    return plainsTemplates;
  }

  public void setPlainsTemplates(List<WeightedTemplate> plainsTemplates) {
    this.plainsTemplates = plainsTemplates;
  }

  public List<WeightedTemplate> getDesertTemplates() {
    return desertTemplates;
  }

  public void setDesertTemplates(List<WeightedTemplate> desertTemplates) {
    this.desertTemplates = desertTemplates;
  }

  public int getVillagerId() {
    return villagerId;
  }

  public void setVillagerId(int villagerId) {
    this.villagerId = villagerId;
  }

  public String getTexture() {
    return texture;
  }

  public void setTexture(String texture) {
    this.texture = texture;
  }

  public List<MerchantRecipeWrapper> getTrades() {
    return trades;
  }

  public void setTrades(List<MerchantRecipeWrapper> trades) {
    this.trades = trades;
  }

}
