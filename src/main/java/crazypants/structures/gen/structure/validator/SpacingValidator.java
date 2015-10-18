package crazypants.structures.gen.structure.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import crazypants.structures.api.gen.IChunkValidator;
import crazypants.structures.api.gen.ISiteValidator;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.api.gen.IWorldStructures;
import crazypants.structures.api.util.BoundingCircle;
import crazypants.structures.api.util.Vector2d;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class SpacingValidator implements IChunkValidator, ISiteValidator {

  private static final double CHUNK_RADIUS = new Vector2d().distance(new Vector2d(8, 8));

  private int minSpacing;
  private final List<String> templateFilter = new ArrayList<String>();
  private boolean validateChunk = false;
  private boolean validateLocation = true;

  
  //TODO: Look at MapGenStructure to include vanilla structures
  
  
  public SpacingValidator() {
    this(20, (String[]) null);
  }

  public SpacingValidator(int minSpacing, String... templateType) {
    this(minSpacing, minSpacing >= 32, minSpacing < 32, templateType);
  }

  public SpacingValidator(int minSpacing, boolean checkChunkDistance, boolean checkPointDistance, String... matchTypes) {
    this.minSpacing = minSpacing;
    this.validateChunk = checkChunkDistance;
    this.validateLocation = checkPointDistance;
    if(matchTypes != null) {
      for (String tmp : matchTypes) {
        if(tmp != null) {
          templateFilter.add(tmp);
        }
      }
    }
  }
  
  public void setValidateChunk(boolean validateChunk) {
    this.validateChunk = validateChunk;
  }
  
  public void setValidateLocation(boolean validateLocation) {
    this.validateLocation = validateLocation;
  }

  @Override
  public boolean isValidChunk(IStructureGenerator template, IWorldStructures structures, World world, Random random, int chunkX, int chunkZ) {

    if(!validateChunk) {
      return true;
    }
    ChunkCoordIntPair cc = new ChunkCoordIntPair(chunkX, chunkZ);
    BoundingCircle bc = new BoundingCircle(cc.getCenterXPos(), cc.getCenterZPosition(), minSpacing + CHUNK_RADIUS);
    return areMatchingStructuresInBounds(structures, bc);
  }

  @Override
  public boolean isValidBuildSite(IStructure structure, IWorldStructures existingStructures, World world, Random random, StructureBoundingBox bounds) {
    if(!validateLocation) {
      return true;
    }
    BoundingCircle bc = new BoundingCircle(structure.getOrigin().x + structure.getSize().x / 2, structure.getOrigin().z + structure.getSize().z / 2,
        (int) (structure.getBoundingRadius() + minSpacing));
    return areMatchingStructuresInBounds(existingStructures, bc);
  }

  private boolean areMatchingStructuresInBounds(IWorldStructures existingStructures, BoundingCircle bc) {
    List<IStructure> res = new ArrayList<IStructure>();    
    for (ChunkCoordIntPair chunk : bc.getChunks()) {
      if(bc.intersects(new BoundingCircle(chunk.getCenterXPos(), chunk.getCenterZPosition(), CHUNK_RADIUS))) {
        getStructuresIntersectingChunk(chunk, existingStructures, res);
        if(!res.isEmpty()) {
          for (IStructure s : res) {
            if(s.getBoundingCircle().intersects(bc)) {
              return false;
            }
          }
          res.clear();
        }
      }
    }
    return true;
  }

  private void getStructuresIntersectingChunk(ChunkCoordIntPair chunk, IWorldStructures structures, List<IStructure> res) {
    structures.getStructuresIntersectingChunk(chunk, null, res);
    if(!templateFilter.isEmpty() && !res.isEmpty()) {
      ListIterator<IStructure> iter = res.listIterator();
      while (iter.hasNext()) {
        IStructure match = iter.next();
        if(!templateFilter.contains(match.getUid())) {
          iter.remove();
        }
      }
    }
  }

  public int getMinSpacing() {
    return minSpacing;
  }

  public void setMinSpacing(int minSpacing) {
    this.minSpacing = minSpacing;
  }

  public List<String> getTemplateFilter() {
    return templateFilter;
  }

  public void setTemplateFilter(Collection<String> filter) {
    templateFilter.clear();
    templateFilter.addAll(filter);
  }

}
