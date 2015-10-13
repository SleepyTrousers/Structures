package crazypants.structures.village;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.gen.IStructureTemplate;
import crazypants.structures.api.util.ChunkBounds;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.api.util.Rotation;
import crazypants.structures.gen.StructureRegister;
import crazypants.structures.gen.io.resource.ResourceModContainer;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;

public class Test {

  public Test() {

  }

  public void registerVillagers() {

    int id = 27637;

    VillagerRegistry.instance().registerVillagerId(id);

    ResourceLocation texture = new ResourceLocation(ResourceModContainer.MODID + ":test/testVillager.png");
    VillagerRegistry.instance().registerVillagerSkin(id, texture);
    VillagerRegistry.instance().registerVillageTradeHandler(id, new TradeHandler());
    VillagerRegistry.instance().registerVillageCreationHandler(new CreationHandler("test", id));
    MapGenStructureIO.func_143031_a(VillageComponent.class, "EnderStructures:TestStructure");
  }

  public class TradeHandler implements IVillageTradeHandler {

    @Override
    public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random) {
      recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(Items.apple, 20, 0), new ItemStack(Items.emerald, 1, 0)));
    }

  }

  public class CreationHandler implements IVillageCreationHandler {

    private final int villagerId;
    private final String templateUid;

    public CreationHandler(String templateUid, int villagerId) {
      this.templateUid = templateUid;
      this.villagerId = villagerId;
    }

    @Override
    public PieceWeight getVillagePieceWeight(Random random, int i) {
      //      return new PieceWeight(VillageComponent.class, 9, 1);
      return new PieceWeight(VillageComponent.class, 100, 20);
    }

    @Override
    public Class<?> getComponentClass() {
      return VillageComponent.class;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object buildComponent(PieceWeight villagePiece, Start startPiece, List pieces, Random random, int x, int y, int z, int coordBaseMode, int p5) {
      VillageComponent comp = new VillageComponent(templateUid, villagerId, x, y, z, coordBaseMode);

      return canVillageGoDeeper(comp.getBoundingBox()) && StructureComponent.findIntersecting(pieces, comp.getBoundingBox()) == null
          ? comp : null;
    }

    protected boolean canVillageGoDeeper(StructureBoundingBox p_74895_0_) {
      return p_74895_0_ != null && p_74895_0_.minY > 10;
    }

  }

  //public class VillageComponent extends Village {
  public class VillageComponent extends StructureVillagePieces.House1 {

    private int villagerId;    

    private int averageGroundLevel = -1;

    private IStructure structure;
    private IStructureTemplate template;

    public VillageComponent(String templateUid, int villagerId, int x, int y, int z, int coordBaseMode) {
      this.villagerId = villagerId;
      this.coordBaseMode = coordBaseMode;

      template = StructureRegister.instance.getStructureTemplate(templateUid, true);
      structure = template.createInstance(getRotation());

      AxisAlignedBB bb = structure.getBounds();
      bb = bb.getOffsetBoundingBox(x, y, z);
      this.boundingBox = new StructureBoundingBox((int) bb.minX, (int) bb.minY, (int) bb.minZ, (int) bb.maxX, (int) bb.maxY, (int) bb.maxZ);

      if(coordBaseMode == 1) {
        boundingBox.offset((int) -(bb.maxX - bb.minX), 0, 0);
      } else if(coordBaseMode == 2) {
        boundingBox.offset(0, 0, (int) -(bb.maxZ - bb.minZ));
      }

    }

    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox bb) {
      if(averageGroundLevel < 0) {
        averageGroundLevel = getAverageGroundLevel(world, bb);

        if(averageGroundLevel < 0) {
          return true;
        }

        boundingBox.offset(0, averageGroundLevel - boundingBox.minY - 1, 0);
      }
      Point3i origin = new Point3i(boundingBox.minX, boundingBox.minY, boundingBox.minZ);

      if(coordBaseMode == 1) {
        origin.x++;
      } else if(coordBaseMode == 2) {
        origin.z++;
      }
      structure.setOrigin(origin);

      ChunkBounds cb = new ChunkBounds(bb.minX >> 4, bb.minZ >> 4);
      cb = null; //TODO:
      template.build(structure, world, random, cb);

      spawnVillagers(world, bb, 3, 1, 3, 1);

      return true;
    }

    private Rotation getRotation() {

      //TODO: I think my test model is the wrong way around
      switch (coordBaseMode) {
      case 0:
        return Rotation.DEG_180;
      case 1:
        return Rotation.DEG_270;
      case 2:
        return Rotation.DEG_0;
      case 3:
        return Rotation.DEG_90;
      default:
        return Rotation.DEG_0;
      }

    }

    @Override
    protected int getVillagerType(int par1) {
      return villagerId;
    }

  }

}
