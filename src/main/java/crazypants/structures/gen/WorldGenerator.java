package crazypants.structures.gen;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import crazypants.structures.gen.structure.Structure;
import crazypants.structures.gen.structure.StructureGenerator;
import crazypants.vec.Point3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class WorldGenerator implements IWorldGenerator {

  public static WorldGenerator create() {
    WorldGenerator sm = new WorldGenerator();
    sm.init();
    return sm;
  }

  public static boolean GEN_ENABLED_DEBUG = true;

  private final Map<Integer, WorldStructures> worldManagers = new HashMap<Integer, WorldStructures>();

  private final Set<Point3i> generating = new HashSet<Point3i>();
  private final Set<Point3i> deffered = new HashSet<Point3i>();

  private WorldGenerator() {
  }

  private void init() {
    MinecraftForge.EVENT_BUS.register(this);
    GameRegistry.registerWorldGenerator(this, 50000);
  }

  @Override
  public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

    if(!GEN_ENABLED_DEBUG) {
      return;
    }

    if(!world.getWorldInfo().isMapFeaturesEnabled()) {
      return;
    }

    Point3i p = new Point3i(world.provider.getDimensionId(), chunkX, chunkZ);
    if(generating.contains(p)) {
      //guard against recurse gen
      return;
    }
    if(!generating.isEmpty()) {
      //Only allow one chunk to have structures added at a time. 
      //If building a structure forces a new chunk to be generated, catch that here and defer
      //structure gen on them until we are done
      deffered.add(p);
      return;
    }
    generating.add(p);
    try {
      long worldSeed = world.getSeed();
      Random fmlRandom = new Random(worldSeed);
      long xSeed = fmlRandom.nextLong() >> 2 + 1L;
      long zSeed = fmlRandom.nextLong() >> 2 + 1L;
      long chunkSeed = (xSeed * chunkX + zSeed * chunkZ) ^ worldSeed;

      WorldStructures structures = getWorldManOrCreate(world);

      for (StructureGenerator template : StructureRegister.instance.getGenerators()) {
        Random r = new Random(chunkSeed ^ template.getUid().hashCode());
        Collection<Structure> s = template.generate(structures, r, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        if(s != null) {
          structures.addAll(s);
        }
      }
    } finally {
      generating.remove(p);
    }
    if(!deffered.isEmpty()) {
      Point3i chk = deffered.iterator().next();
      deffered.remove(chk);
      generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
    }
  }

  public void serverStopped(FMLServerStoppedEvent event) {
    worldManagers.clear();    
    generating.clear();
  }

  @SubscribeEvent
  public void eventWorldSave(WorldEvent.Save evt) {
    WorldStructures wm = getWorldManOrCreate(evt.world);
    if(wm != null) {
      wm.save();
    }
  }

  public WorldStructures getWorldMan(World world) {
    if(world == null) {
      return null;
    }
    return worldManagers.get(world.provider.getDimensionId());
  }

  public WorldStructures getWorldManOrCreate(World world) {
    WorldStructures res = getWorldMan(world);
    if(res == null) {
      res = new WorldStructures(world);
      res.load();
      worldManagers.put(world.provider.getDimensionId(), res);
    }
    return res;
  }

}
