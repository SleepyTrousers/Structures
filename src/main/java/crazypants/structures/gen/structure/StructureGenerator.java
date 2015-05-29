package crazypants.structures.gen.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import crazypants.structures.Log;
import crazypants.structures.gen.ChunkBounds;
import crazypants.structures.gen.WorldStructures;
import crazypants.structures.gen.structure.sampler.ILocationSampler;
import crazypants.structures.gen.structure.sampler.SurfaceLocationSampler;
import crazypants.structures.gen.structure.validator.CompositeValidator;
import crazypants.structures.gen.structure.validator.IChunkValidator;
import crazypants.vec.Point3i;

public class StructureGenerator {

  private static final Random RND = new Random();

  private final String uid;

  private final CompositeValidator chunkValidators = new CompositeValidator();

  private ILocationSampler locSampler;
  private boolean canSpanChunks = false;
  private int attemptsPerChunk = 2;
  //Max number of structures of this type that be generated in a single chunk
  private int maxInChunk = 1;

  private final List<StructureTemplate> structureTemplates = new ArrayList<StructureTemplate>();
  
  private final List<DeferredGenTask> deferredGenTasks = new ArrayList<DeferredGenTask>();

  public StructureGenerator(String uid) {
    this(uid, (StructureTemplate) null);
  }

  public StructureGenerator(String uid, StructureTemplate... gens) {
    this(uid, gens == null ? (Collection<StructureTemplate>) null : Arrays.asList(gens));
  }

  public StructureGenerator(String uid, Collection<StructureTemplate> gens) {
    this.uid = uid;
    if (gens != null) {
      for (StructureTemplate gen : gens) {
        if (gen != null) {
          structureTemplates.add(gen);
        }
      }
    }
    locSampler = new SurfaceLocationSampler();
  }

  public void addStructureTemaplate(StructureTemplate structureTemplate) {
    if (structureTemplate != null) {
      structureTemplates.add(structureTemplate);
    }
    canSpanChunks = false;
    for(StructureTemplate tp : structureTemplates) {      
      if(tp.getCanSpanChunks()) {
        canSpanChunks = true;
        return;
      }
    }
  }

  public Structure createStructure() {
    if (structureTemplates.isEmpty()) {
      return null;
    }
    return structureTemplates.get(RND.nextInt(structureTemplates.size())).createInstance(this);
  }

  public Collection<Structure> generate(WorldStructures structures, Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
      IChunkProvider chunkProvider) {

    if (canSpanChunks) { 
      //Continue generating any structures that where started in a different chunk
      continueBuildingExistingStructrures(structures, random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);            
    }
    
    List<Structure> res = new ArrayList<Structure>();
        
    //Check to see if any deferred tasks can now be completed with the creation of this chunk
    ListIterator<DeferredGenTask> liter = deferredGenTasks.listIterator();
    while(liter.hasNext()) {
      DeferredGenTask task = liter.next();
      if(task.canComplete(chunkX, chunkZ)) {
        if(task.build(structures, world, random)) {
          res.add(task.getStructure());
        }
        liter.remove();
      }
    }
    
    if (!chunkValidators.isValidChunk(this, structures, world, random, chunkX, chunkZ)) {
      return Collections.emptyList();
    }

    Structure struct = createStructure();
    if (struct == null) {
      return Collections.emptyList();
    }
    
    for (int i = 0; i < attemptsPerChunk && res.size() < maxInChunk; i++) {
      Point3i origin = locSampler.generateCandidateLocation(struct, structures, world, random, chunkX, chunkZ);
      if (origin != null) {
        struct.setOrigin(origin);
        
        if(struct.getGenerationRequiresLoadedChunks()) {
          //TODO: deal with extra tries: eg if tries per chunk is 2, and max in chunk is 1, we could actually
          //call build twice
          DeferredGenTask task = new DeferredGenTask(chunkProvider, struct);
          if(task.canComplete(chunkX, chunkZ)) {
            if(task.build(structures, world, random)) {
              res.add(struct);
            }
          } else {
            deferredGenTasks.add(task);
          }
          
        } else {
          if (buildStructureInChunk(struct, structures, random, chunkX, chunkZ, world, chunkGenerator, chunkProvider)) {
            res.add(struct);
            Log.debug("StructureGenerator.generate: Added " + struct);
          }  
        }
      }

    }
    return res;
  }

  public boolean buildStructureInChunk(Structure s, WorldStructures structures, Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
      IChunkProvider chunkProvider) {

    ChunkBounds bounds = new ChunkBounds(chunkX, chunkZ);
    if(!s.isValidSite(structures, world, random, bounds)) {
      return false;
    }
    
    s.build(world, bounds);

    //it is possible, when not deferring generation, that when this structure is build we will need to build it onto 
    //already loaded chunks
    if (s.isChunkBoundaryCrossed() && !s.getGenerationRequiresLoadedChunks()) {      
      Collection<ChunkCoordIntPair> chunks = s.getChunkBounds().getChunks();
      for (ChunkCoordIntPair c : chunks) {
        if (!(c.chunkXPos == chunkX && c.chunkZPos == chunkZ) && chunkGenerator.chunkExists(c.chunkXPos, c.chunkZPos)) {
          s.build(world, new ChunkBounds(c.chunkXPos, c.chunkZPos));
        }
      }
    }
    return true;
  }

  protected boolean continueBuildingExistingStructrures(WorldStructures structures, Random random, int chunkX, int chunkZ, World world,
      IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

    Collection<Structure> existing = new ArrayList<Structure>();
    structures.getStructuresIntersectingChunk(new ChunkCoordIntPair(chunkX, chunkZ), uid, existing);

    for (Structure s : existing) {
      if (s.canSpanChunks() && !s.getGenerationRequiresLoadedChunks()) {
        s.build(world, new ChunkBounds(chunkX, chunkZ));
      }
    }
    return !existing.isEmpty();

  }

  public boolean isValid() {
    return uid != null && !uid.trim().isEmpty() && !structureTemplates.isEmpty() && locSampler != null;
  }

  public void addChunkValidator(IChunkValidator val) {
    if (val != null) {
      chunkValidators.add(val);
    }
  }

  public String getUid() {
    return uid;
  }

  public int getMaxAttemptsPerChunk() {
    return attemptsPerChunk;
  }

  public ILocationSampler getLocationSampler() {
    return locSampler;
  }

  public void setLocationSampler(ILocationSampler locSampler) {
    this.locSampler = locSampler;
  }

  public int getAttemptsPerChunk() {
    return attemptsPerChunk;
  }

  public void setAttemptsPerChunk(int attemptsPerChunk) {
    this.attemptsPerChunk = attemptsPerChunk;
  }

  public int getMaxInChunk() {
    return maxInChunk;
  }

  public void setMaxInChunk(int maxInChunk) {
    this.maxInChunk = maxInChunk;
  }

  public List<StructureTemplate> getTemplates() {
    return structureTemplates;
  }

  @Override
  public String toString() {
    return "StructureGenerator [uid=" + uid + "]";
  }
  
  private static class DeferredGenTask {
    
    private final Structure structure;
    private final Set<ChunkCoordIntPair> requiredChunks = new HashSet<ChunkCoordIntPair>();
    
    public DeferredGenTask(IChunkProvider cp, Structure structure) {    
      this.structure = structure;      
      for(ChunkCoordIntPair cc : structure.getChunkBounds().getChunks()) {
        if(!cp.chunkExists(cc.chunkXPos, cc.chunkZPos)) {
          requiredChunks.add(cc);
        }
      }
    }

    public boolean canComplete(int creatingChunkX, int creatingChunkZ) {
      requiredChunks.remove(new ChunkCoordIntPair(creatingChunkX, creatingChunkZ));
      return requiredChunks.isEmpty();
    }
    
    public boolean build(WorldStructures existingStructures, World world, Random random) {
      if(getStructure().getTemplate() == null || !getStructure().getTemplate().getSiteValiditor().isValidBuildSite(getStructure(), existingStructures, world, random, null)) {
        return false;
      }
      getStructure().build(world, null);
      return true;
    }

    public Structure getStructure() {
      return structure;
    }
    
  }

}
