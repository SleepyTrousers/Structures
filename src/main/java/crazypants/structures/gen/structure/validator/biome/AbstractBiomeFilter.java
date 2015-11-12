package crazypants.structures.gen.structure.validator.biome;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import crazypants.structures.AbstractTyped;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

public abstract class AbstractBiomeFilter extends AbstractTyped implements IBiomeFilter {

  protected AbstractBiomeFilter(String type) {
    super(type);
  }

  @Expose
  protected List<BiomeDictionary.Type> types = new ArrayList<BiomeDictionary.Type>();
  
  @Expose
  protected List<BiomeDictionary.Type> typeExcludes = new ArrayList<BiomeDictionary.Type>();

  @Expose
  protected List<String> names = new ArrayList<String>();
  
  @Expose
  protected List<String> nameExcludes = new ArrayList<String>();

  @Override
  public void addBiomeDescriptor(IBiomeDescriptor biome) {
    if(biome.getType() != null) {
      if(biome.isExclude()) {
        typeExcludes.add(biome.getType());
      } else {
        types.add(biome.getType());
      }
    } else if(biome.getName() != null) {
      if(biome.isExclude()) {
        nameExcludes.add(biome.getName());
      } else {
        names.add(biome.getName());
      }
    }
  }

  protected boolean isExcluded(BiomeGenBase candidate) {
    for (BiomeDictionary.Type exType : typeExcludes) {
      if(BiomeDictionary.isBiomeOfType(candidate, exType)) {       
        return true;

      }
    }
    for (String exName : nameExcludes) {
      if(exName != null && exName.equals(candidate.biomeName)) {
        System.out.print("Excluded " + candidate.biomeName + ", ");
        return false;
      }
    }
    return false;
  }

}
