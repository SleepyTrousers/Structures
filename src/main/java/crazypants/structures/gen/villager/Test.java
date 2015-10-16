package crazypants.structures.gen.villager;

public class Test {

  public Test() {

  }

  public void registerVillagers() {

//    int id = 27637;
//
//    VillagerRegistry.instance().registerVillagerId(id);
//
//    ResourceLocation texture = new ResourceLocation(ResourceModContainer.MODID + ":testVillager.png");
//    VillagerRegistry.instance().registerVillagerSkin(id, texture);
//    VillagerRegistry.instance().registerVillageTradeHandler(id, new TradeHandler());
//
//    
//    //
//    List<String> temps = new ArrayList<String>();
//    temps.add("villageHouse1");
//    temps.add("villageHouse2");
//    List<String> desertTemps = Collections.singletonList("villageHouseDesert");
//    VillagerRegistry.instance().registerVillageCreationHandler(new CreationHandler(id, temps, desertTemps));
//    MapGenStructureIO.func_143031_a(VillageHouse.class, "EnderStructures:TestStructure");
  }

//  public static class TradeHandler implements IVillageTradeHandler {
//
//    @Override
//    public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random) {
//      recipeList.addToListWithCheck(new MerchantRecipe(new ItemStack(Items.apple, 20, 0), new ItemStack(Items.emerald, 1, 0)));
//    }
//
//  }
//
//  public static class CreationHandler implements IVillageCreationHandler {
//
//    private final int villagerId;
//    private final List<String> templates = new ArrayList<String>();
//    private final List<String> desertTemplates = new ArrayList<String>();
//
//    public CreationHandler(int villagerId, List<String> templates, List<String> desertTemplates) {
//      this.villagerId = villagerId;
//      this.templates.addAll(templates);
//      this.desertTemplates.addAll(desertTemplates);
//    }
//
//    @Override
//    public PieceWeight getVillagePieceWeight(Random random, int i) {
////      return new PieceWeight(VillageComponent.class, 9, 1);
//            return new PieceWeight(VillageComponent.class, 100, 20);
//    }
//
//    @Override
//    public Class<?> getComponentClass() {
//      return VillageComponent.class;
//    }
//
//    @SuppressWarnings("rawtypes")
//    @Override
//    public Object buildComponent(PieceWeight villagePiece, Start startPiece, List pieces, Random random, int x, int y, int z, int coordBaseMode, int p5) {
//      String templateUid;
//      if(startPiece.inDesert && !desertTemplates.isEmpty()) {
//        templateUid = desertTemplates.get(random.nextInt(desertTemplates.size()));
//      } else {
//        templateUid = templates.get(random.nextInt(templates.size()));
//      }
//      VillageComponent comp = new VillageComponent(templateUid, villagerId, x, y, z, coordBaseMode);
//
//      return canVillageGoDeeper(comp.getBoundingBox()) && StructureComponent.findIntersecting(pieces, comp.getBoundingBox()) == null
//          ? comp : null;
//    }
//
//    protected boolean canVillageGoDeeper(StructureBoundingBox p_74895_0_) {
//      return p_74895_0_ != null && p_74895_0_.minY > 10;
//    }
//
//  }
//
//  //public class VillageComponent extends Village {
//  public static class VillageComponent extends StructureVillagePieces.House1 {
//
//    private int villagerId;
//
//    private int averageGroundLevel = -1;
//
//    private IStructure structure;
//    private IStructureTemplate template;   
//
//    public VillageComponent() {
//
//    }
//
//    public VillageComponent(String templateUid, int villagerId, int x, int y, int z, int coordBaseMode) {
//      this.villagerId = villagerId;
//      this.coordBaseMode = coordBaseMode;
//
//      template = StructureRegister.instance.getStructureTemplate(templateUid, true);
//      structure = template.createInstance(getRotation());
//
//      AxisAlignedBB bb = structure.getBounds();
//      bb = bb.getOffsetBoundingBox(x, y, z);
//      this.boundingBox = new StructureBoundingBox((int) bb.minX, (int) bb.minY, (int) bb.minZ, (int) bb.maxX, (int) bb.maxY, (int) bb.maxZ);
//
//      if(coordBaseMode == 1) {
//        boundingBox.offset((int) -(bb.maxX - bb.minX), 0, 0);
//      } else if(coordBaseMode == 2) {
//        boundingBox.offset(0, 0, (int) -(bb.maxZ - bb.minZ));
//      }
//
//    }
//
//    @Override
//    public boolean addComponentParts(World world, Random random, StructureBoundingBox bb) {
//      if(averageGroundLevel < 0) {
//        averageGroundLevel = getAverageGroundLevel(world, bb);
//
//        if(averageGroundLevel < 0) {
//          return true;
//        }
//
//        boundingBox.offset(0, averageGroundLevel - boundingBox.minY - 1 - structure.getSurfaceOffset(), 0);
//      }
//
//      Point3i origin = new Point3i(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
//      if(coordBaseMode == 1) {
//        origin.x++;
//      } else if(coordBaseMode == 2) {
//        origin.z++;
//      }
//      structure.setOrigin(origin);
//
//      
//      template.build(structure, world, random, bb);
//
//      spawnVillagers(world, bb, 3, 1, 3, 1);
//
//      return true;
//    }
//
//    private Rotation getRotation() {      
//      return Rotation.values()[coordBaseMode];
//    }
//
//    @Override
//    protected int getVillagerType(int par1) {
//      return villagerId;
//    }
//
//  }

}
