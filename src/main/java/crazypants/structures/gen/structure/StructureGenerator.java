package crazypants.structures.gen.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;

import com.google.gson.annotations.Expose;

import crazypants.structures.Log;
import crazypants.structures.api.ListElementType;
import crazypants.structures.api.gen.IChunkValidator;
import crazypants.structures.api.gen.ILocationSampler;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.api.gen.IWorldStructures;
import crazypants.structures.api.gen.WeightedTemplate;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.VecUtil;
import crazypants.structures.gen.structure.sampler.SurfaceLocationSampler;
import crazypants.structures.gen.structure.validator.CompositeValidator;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class StructureGenerator implements IStructureGenerator {

  private String uid;

  @ListElementType(elementType = WeightedTemplate.class)
  @Expose
  private final List<WeightedTemplate> templates = new ArrayList<WeightedTemplate>();

  @Expose
  private ILocationSampler locationSampler;

  @Expose
  private CompositeValidator chunkValidator;

  @Expose
  private boolean canSpanChunks;

  @Expose
  private int maxAttemptsPerChunk;

  //Max number of structures of this type that be generated in a single chunk
  @Expose
  private int maxGeneratedPerChunk;

  private final List<DeferredGenTask> deferredGenTasks = new ArrayList<DeferredGenTask>();

  public StructureGenerator() {
    canSpanChunks = true;
    maxAttemptsPerChunk = 2;
    maxGeneratedPerChunk = 1;
    chunkValidator = new CompositeValidator();
    locationSampler = new SurfaceLocationSampler();
  }

  public IStructure createStructure() {
    if(templates.isEmpty()) {
      return null;
    }
    return WeightedTemplate.getTemplate(templates).createInstance();
    //return templates.get(RND.nextInt(templates.size())).createInstance();
  }
  
  public void addChunkValidator(IChunkValidator validator) {
    chunkValidator.add(validator);    
  }
  
  public List<WeightedTemplate> getTemplates() {    
    return templates;
  }

  @Override
  public Collection<IStructure> generate(IWorldStructures structures, Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
      IChunkProvider chunkProvider) {

    if(canSpanChunks) {
      //Continue generating any structures that where started in a different chunk
      continueBuildingExistingStructrures(structures, random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
    }

    List<IStructure> res = new ArrayList<IStructure>();

    //Check to see if any deferred tasks can now be completed with the creation of this chunk
    ListIterator<DeferredGenTask> liter = deferredGenTasks.listIterator();
    while (liter.hasNext()) {
      DeferredGenTask task = liter.next();
      if(task.canComplete(chunkX, chunkZ)) {
        if(task.build(structures, world, random)) {
          res.add(task.getStructure());
        }
        liter.remove();
      }
    }

    if(!chunkValidator.isValidChunk(this, structures, world, random, chunkX, chunkZ)) {
      return Collections.emptyList();
    }

    IStructure struct = createStructure();
    if(struct == null) {
      return Collections.emptyList();
    }

    for (int i = 0; i < maxAttemptsPerChunk && res.size() < maxGeneratedPerChunk; i++) {
      Point3i origin = locationSampler.generateCandidateLocation(struct, structures, random, chunkX, chunkZ);
      if(origin != null) {
        struct.setOrigin(origin);
        if(struct.getGenerationRequiresLoadedChunks()) {
          DeferredGenTask task = new DeferredGenTask(chunkProvider, struct);
          if(task.canComplete(chunkX, chunkZ)) {
            if(task.build(structures, world, random)) {
              res.add(struct);
            }
          } else {
            deferredGenTasks.add(task);
          }

        } else {
          if(buildStructureInChunk(struct, structures, random, chunkX, chunkZ, world, chunkGenerator, chunkProvider)) {
            res.add(struct);
            Log.debug("StructureGenerator.generate: Added " + struct);
          }
        }
      }

    }
    return res;
  }

  public boolean buildStructureInChunk(IStructure s, IWorldStructures structures, Random random, int chunkX, int chunkZ, World world,
      IChunkProvider chunkGenerator,
      IChunkProvider chunkProvider) {

    //ChunkBounds bounds = new ChunkBounds(chunkX, chunkZ);
    StructureBoundingBox bounds = VecUtil.createForChunk(chunkX, chunkZ);
    if(!s.isValidSite(structures, world, random, bounds)) {
      return false;
    }

    s.build(world, random, bounds);

    //it is possible, when not deferring generation, that when this structure is build we will need to build it onto 
    //already loaded chunks
    if(s.isChunkBoundaryCrossed() && !s.getGenerationRequiresLoadedChunks()) {
      Collection<ChunkCoordIntPair> chunks = s.getChunkBounds().getChunks();
      for (ChunkCoordIntPair c : chunks) {
        if(!(c.chunkXPos == chunkX && c.chunkZPos == chunkZ) && chunkGenerator.chunkExists(c.chunkXPos, c.chunkZPos)) {
          s.build(world, random, VecUtil.createForChunk(c.chunkXPos, c.chunkZPos));
        }
      }
    }
    return true;
  }

  protected boolean continueBuildingExistingStructrures(IWorldStructures structures, Random random, int chunkX, int chunkZ, World world,
      IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

    Collection<IStructure> existing = new ArrayList<IStructure>();
    structures.getStructuresIntersectingChunk(new ChunkCoordIntPair(chunkX, chunkZ), uid, existing);

    for (IStructure s : existing) {
      if(s.canSpanChunks() && !s.getGenerationRequiresLoadedChunks()) {
        s.build(world, random, VecUtil.createForChunk(chunkX, chunkZ));
      }
    }
    return !existing.isEmpty();

  }

  public boolean isValid() {
    return uid != null && !uid.trim().isEmpty() &&  hasValidTemplate() && locationSampler != null;
  }

  private boolean hasValidTemplate() {
    if(templates == null || templates.isEmpty()) {
      return false;
    }
    for( WeightedTemplate tmpl : templates) {
      if(tmpl.getTemplate() == null) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String getUid() {
    return uid;
  }

  public void setUid(String newUid) {
    uid = newUid;
  }

  public int getMaxAttemptsPerChunk() {
    return maxAttemptsPerChunk;
  }

  public ILocationSampler getLocationSampler() {
    return locationSampler;
  }

  public void setLocationSampler(ILocationSampler locSampler) {
    this.locationSampler = locSampler;
  }

  public int getAttemptsPerChunk() {
    return maxAttemptsPerChunk;
  }

  public void setAttemptsPerChunk(int attemptsPerChunk) {
    this.maxAttemptsPerChunk = attemptsPerChunk;
  }

  public int getMaxInChunk() {
    return maxGeneratedPerChunk;
  }

  public void setMaxInChunk(int maxInChunk) {
    this.maxGeneratedPerChunk = maxInChunk;
  }
 
  @Override
  public String toString() {
    return "StructureGenerator [uid=" + uid + "]";
  }

  private static class DeferredGenTask {

    private final IStructure structure;
    private final Set<ChunkCoordIntPair> requiredChunks = new HashSet<ChunkCoordIntPair>();

    public DeferredGenTask(IChunkProvider cp, IStructure structure) {
      this.structure = structure;
      for (ChunkCoordIntPair cc : structure.getChunkBounds().getChunks()) {
        if(!cp.chunkExists(cc.chunkXPos, cc.chunkZPos)) {
          requiredChunks.add(cc);
        }
      }
    }

    public boolean canComplete(int creatingChunkX, int creatingChunkZ) {
      requiredChunks.remove(new ChunkCoordIntPair(creatingChunkX, creatingChunkZ));
      return requiredChunks.isEmpty();
    }

    public boolean isGenerated(IWorldStructures existingStructures) {
      //TODO: This is a bit dodgy as it is relying on Structure not having a proper equals method and essentially relying on an ==
      //this is easily broken
      Collection<IStructure> structures = existingStructures.getStructuresWithOriginInChunk(structure.getChunkCoord(), structure.getTemplate().getUid());
      return structures.contains(structure);
    }

    public boolean build(IWorldStructures existingStructures, World world, Random random) {
      if(isGenerated(existingStructures) || getStructure().getTemplate() == null) {
        return false;
      }

      if(getStructure().getTemplate().getSiteValidator() != null
          && !getStructure().getTemplate().getSiteValidator().isValidBuildSite(getStructure(), existingStructures, world, random, null)) {
        return false;
      }
      getStructure().build(world, random, null);
      return true;
    }

    public IStructure getStructure() {
      return structure;
    }

  }

}
