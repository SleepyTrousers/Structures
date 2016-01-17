package crazypants.structures.gen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import crazypants.structures.EnderStructures;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.gen.IStructureGenerator;
import crazypants.structures.api.gen.IWorldStructures;
import crazypants.structures.api.util.Point3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class WorldGenerator implements IWorldGenerator {

  public static WorldGenerator create() {
    WorldGenerator sm = new WorldGenerator();
    sm.init();
    return sm;
  }

  public static boolean GEN_ENABLED_DEBUG = true;

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

      //TODO: Round robin?
      List<IStructureGenerator> shuffledGens = new ArrayList<IStructureGenerator>(StructureGenRegister.instance.getGenerators());
      Collections.shuffle(shuffledGens);
      IWorldStructures worldStructs = EnderStructures.structureRegister.getStructuresForWorld(world);
      for (IStructureGenerator generator : shuffledGens) {
        Random r = new Random(chunkSeed ^ generator.getUid().hashCode());
        Collection<IStructure> generatedStructs = generator.generate(worldStructs, r, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        if(generatedStructs != null) {          
          for (IStructure s : generatedStructs) {
            if(s != null && s.isValid()) {
              worldStructs.add(s);
              s.onGenerated(world);              
            }
          }
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
    generating.clear();
  }

}
