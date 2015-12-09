package crazypants.structures.runtime;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.gen.IWorldStructures;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.gen.WorldStructures;
import crazypants.structures.gen.io.WorldData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

public class StructureRegister {

  public static StructureRegister create() {
    StructureRegister sm = new StructureRegister();
    sm.init();
    return sm;
  }

  private final Map<Integer, WorldStructures> worldManagers = new HashMap<Integer, WorldStructures>();

  private final Set<IStructure> loadedStructures = new HashSet<IStructure>();

  public StructureRegister() {
  }

  private void init() {
    MinecraftForge.EVENT_BUS.register(new EventListener());
  }

  public IWorldStructures getStructuresForWorld(World world) {
    return getStructuresForWorldImpl(world);
  }

  private WorldStructures getStructuresForWorldImpl(World world) {
    if(world == null) {
      return null;
    }
    WorldStructures res = worldManagers.get(world.provider.dimensionId);
    if(res == null) {
      WorldStructures s = new WorldStructures(world);
      s.load();
      worldManagers.put(world.provider.dimensionId, s);
      res = s;
    }
    return res;
  }

  public void serverStopped(FMLServerStoppedEvent event) {
    worldManagers.clear();
    loadedStructures.clear();
  }

  public class EventListener {

    private EventListener() {
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load evt) {
      if(evt.world.isRemote) {
        return;
      }
      Collection<IStructure> structs = getStructuresForWorld(evt.world).getStructuresWithOriginInChunk(evt.getChunk().getChunkCoordIntPair());
      if(!structs.isEmpty()) {
        loadedStructures.addAll(structs);        
        for(IStructure s : structs) {          
          s.onLoaded(evt.world, WorldData.INSTANCE.loadNBT(evt.world, getStateKey(s)));          
        }
      }
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload evt) {
      if(evt.world.isRemote) {
        return;
      }
      Collection<IStructure> structs = getStructuresForWorld(evt.world).getStructuresWithOriginInChunk(evt.getChunk().getChunkCoordIntPair());
      if(!structs.isEmpty()) {
        loadedStructures.removeAll(structs);
        for(IStructure s : structs) {
          NBTTagCompound state = s.onUnloaded(evt.world);          
          WorldData.INSTANCE.saveNBT(evt.world, getStateKey(s), state);
        }
      }
    }

    @SubscribeEvent
    public void eventWorldSave(WorldEvent.Save evt) {
      if(evt.world.isRemote) {
        return;
      }
      WorldStructures wm = getStructuresForWorldImpl(evt.world);
      if(wm != null) {
        wm.save();
      }      
      for(IStructure s : loadedStructures) {
        if(wm.contains(s)) {
          WorldData.INSTANCE.saveNBT(evt.world, getStateKey(s), s.getState());
        }
      }
      
    }
    
    private String getStateKey(IStructure structure) {
      Point3i origin = structure.getOrigin();
      return origin.x + "_" + origin.y + "_" + origin.z + "_" + structure.getTemplate().getUid(); 
    }

  }

}
