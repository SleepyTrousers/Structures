package crazypants.structures.gen.structure.validator;

import java.util.Random;

import net.minecraft.world.World;
import crazypants.structures.gen.WorldStructures;
import crazypants.structures.gen.structure.Structure;
import crazypants.structures.gen.structure.StructureGenerator;

public class RandomValidator implements ILocationValidator {

  private float chancePerChunk;

  public RandomValidator() {
    this(0.01f);
  }
  
  public RandomValidator(float chancePerChunk) {
    this.chancePerChunk = chancePerChunk;
  }  

  public float getChancePerChunk() {
    return chancePerChunk;
  }

  public void setChancePerChunk(float chancePerChunk) {
    this.chancePerChunk = chancePerChunk;
  }

  @Override
  public boolean isValidChunk(StructureGenerator template, WorldStructures structures, World world, Random random, int chunkX, int chunkZ) {        
    if(random.nextFloat() <= chancePerChunk) {
      return true;
    }
    return false;
  }

  @Override
  public boolean isValidLocation(Structure structure, WorldStructures existingStructures, World world, Random random, int chunkX, int chunkZ) {
    return true;
  }

}
