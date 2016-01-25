package crazypants.structures.gen.villager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageCreationHandler;

public class CompositeCreationHandler implements IVillageCreationHandler {

  private List<CreationHandler> handlers = new ArrayList<CreationHandler>();

  private List<WeightedCreationHandler> pieceWeights;
  
  public CompositeCreationHandler() {    
  }

  @Override
  public PieceWeight getVillagePieceWeight(Random random, int i) {

    int maxToSpawn = 0;
    int totalWeight = 0;
    pieceWeights = getWeightedSpawnList(random, 0);
    for (WeightedCreationHandler wch : pieceWeights) {
      maxToSpawn += wch.weight.villagePiecesLimit;
      totalWeight += wch.weight.villagePieceWeight;
    }
    return new PieceWeight(StructuresVillageHouse.class, totalWeight, maxToSpawn);
  }

  public void addCreationHandler(CreationHandler handler) {
    handlers.add(handler);
  }
  
  public void removeCreationHandler(String uid) {
    if(uid == null) {
      return;
    }
    ListIterator<CreationHandler> itr = handlers.listIterator();
    while(itr.hasNext()) {
      CreationHandler val = itr.next();
      if(val != null && uid.equals(val.getUid())) {
        itr.remove();
      }
    }    
  }

  @Override
  public Class<?> getComponentClass() {
    return StructuresVillageHouse.class;
  }

  
  @Override
  public Village buildComponent(PieceWeight villagePiece, Start startPiece, List<StructureComponent> pieces, Random random, int x, int y, int z,
      EnumFacing facing, int p5) {
       
    int totalWeight = getTotalWieght(pieceWeights);
    if(totalWeight <= 0) {
      return null;
    }

    for (int i = 0; i < 5; ++i) {

      int randomWeight = random.nextInt(totalWeight);
      Iterator<WeightedCreationHandler> iterator = pieceWeights.iterator();

      while (iterator.hasNext()) {
        WeightedCreationHandler el = iterator.next();
        PieceWeight pieceweight = el.weight;
        randomWeight -= pieceweight.villagePieceWeight;

        if(randomWeight < 0) {
          int notUsed = 0;
          if(!pieceweight.canSpawnMoreVillagePiecesOfType(notUsed)
              || pieceweight == startPiece.structVillagePieceWeight && pieceWeights.size() > 1) {
            break;
          }
          StructureVillagePieces.Village village = (Village) el.handler.buildComponent(pieceweight, startPiece, pieces, random, x, y, z, facing, p5);

          if(village != null) {
            ++pieceweight.villagePiecesSpawned;
            startPiece.structVillagePieceWeight = pieceweight;
            if(!pieceweight.canSpawnMoreVillagePieces()) {
              pieceWeights.remove(el);
            }
            return village;
          }
        }
      }
    }
    return null;
  }

  private int getTotalWieght(List<WeightedCreationHandler> handlers) {
    boolean result = false;
    int totalWeight = 0;
    StructureVillagePieces.PieceWeight pieceweight;

    for (Iterator<WeightedCreationHandler> iterator = handlers.iterator(); iterator.hasNext(); totalWeight += pieceweight.villagePieceWeight) {
      pieceweight = iterator.next().weight;

      if(pieceweight.villagePiecesLimit > 0 && pieceweight.villagePiecesSpawned < pieceweight.villagePiecesLimit) {
        result = true;
      }
    }

    return result ? totalWeight : -1;
  }

  private List<WeightedCreationHandler> getWeightedSpawnList(Random random, int i) {
    List<WeightedCreationHandler> res = new ArrayList<WeightedCreationHandler>();
    for (CreationHandler ch : handlers) {
      res.add(new WeightedCreationHandler(ch.getVillagePieceWeight(random, i), ch));            
    }
    return res;
  }

  private static class WeightedCreationHandler {

    final PieceWeight weight;
    final CreationHandler handler;

    public WeightedCreationHandler(PieceWeight weight, CreationHandler handler) {
      this.weight = weight;
      this.handler = handler;
    }

  }

  

}
