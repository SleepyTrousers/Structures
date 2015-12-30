package crazypants.structures.gen.structure.validator;

import java.util.Random;

import com.google.gson.annotations.Expose;

import crazypants.structures.AbstractTyped;
import crazypants.structures.api.AttributeDoc;
import crazypants.structures.api.gen.IChunkValidator;
import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.api.gen.IWorldStructures;
import crazypants.structures.config.Config;
import net.minecraft.world.World;

public class RandomValidator extends AbstractTyped implements IChunkValidator {

  @AttributeDoc(text="The chance that a call to 'isValidChunk' will return true. A value of 1 always returns true, 0 always returns false")
  @Expose
  private float chancePerChunk;

  public RandomValidator() {
    this(0.01f);
  }
  
  public RandomValidator(float chancePerChunk) {
    super("RandomValidator");
    this.chancePerChunk = chancePerChunk;
  }  

  public float getChancePerChunk() {
    return chancePerChunk;
  }

  public void setChancePerChunk(float chancePerChunk) {
    this.chancePerChunk = chancePerChunk;
  }

  @Override
  public boolean isValidChunk(IStructureGenerator template, IWorldStructures structures, World world, Random random, int chunkX, int chunkZ) {        
    if(random.nextFloat() <= (chancePerChunk * Config.chanceMultiplier)) {
      return true;
    }
    return false;
  }

}
