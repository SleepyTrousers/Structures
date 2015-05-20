package crazypants.structures.item;

import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.structures.EnderStructures;
import crazypants.structures.EnderStructuresTab;
import crazypants.structures.gen.StructureRegister;
import crazypants.structures.gen.structure.Structure.Rotation;
import crazypants.structures.gen.structure.StructureTemplate;
import crazypants.vec.Point3i;

public class ItemStructureTool extends Item {

  private static final String NAME = "itemStructureTool";

  public static ItemStructureTool create() {
    ItemStructureTool res = new ItemStructureTool();
    res.init();
    return res;
  }

  private ItemStructureTool() {
    setUnlocalizedName(NAME);
    setCreativeTab(EnderStructuresTab.tabEnderStructures);
    setTextureName(EnderStructures.MODID.toLowerCase() + ":" + NAME);
    setHasSubtypes(false);
  }

  private void init() {
    GameRegistry.registerItem(this, NAME);
  }

  @Override
  public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {

    if (!world.isRemote) {
      if (player.isSneaking()) {
        String uid = setNextUid(stack);
        player.addChatComponentMessage(new ChatComponentText("Structure Generator set to " + uid));
      }
    }

    return super.onItemRightClick(stack, world, player);
  }

  @Override
  public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

    if (world.getBlock(x, y, z) == EnderStructures.blockStructureMarker) {
      return true;
    }
    if (world.isRemote) {
      return true;
    }

    String uid = getGenUid(stack, true);    
    if (uid != null) {
      StructureTemplate st = StructureRegister.instance.getStructureTemplate(uid);
      if(st != null) {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        Point3i origin = new Point3i(x + dir.offsetX, y + dir.offsetY - 1, z + dir.offsetZ);
        origin.y -= st.getSurfaceOffset();
        st.build(world, origin.x,origin.y,origin.z, Rotation.DEG_0, null);
        addMarkers(world, st, origin);        
      }
    }
    return true;
  }

  private void addMarkers(World world, StructureTemplate st, Point3i origin) {
    Point3i sz = st.getSize();
    world.setBlock(origin.x - 1, origin.y - 1, origin.z - 1, EnderStructures.blockStructureMarker);
    world.setBlock(origin.x - 1, origin.y  + sz.y, origin.z - 1, EnderStructures.blockStructureMarker);    
    world.setBlock(origin.x + sz.x, origin.y - 1, origin.z - 1, EnderStructures.blockStructureMarker);
    world.setBlock(origin.x - 1, origin.y - 1, origin.z + sz.z, EnderStructures.blockStructureMarker);
    
    if(st.getSurfaceOffset() > 0) {
      world.setBlock(origin.x - 1, origin.y + st.getSurfaceOffset(), origin.z - 1, EnderStructures.blockGroundLevelMarker);
    }
  }

  private String setNextUid(ItemStack stack) {
    String curUid = getGenUid(stack, false);
    if(curUid == null) {
      return setDefaultUid(stack);
    }
    Iterator<StructureTemplate> it = StructureRegister.instance.getStructureTemplates().iterator();
    while (it.hasNext()) {
      StructureTemplate template = it.next();
      if (curUid.equals(template.getUid())) {
        if (it.hasNext()) {
          String uid = it.next().getUid();
          setGenUid(stack, uid);
          return uid;
        }
      }
    }        
    return setDefaultUid(stack);
  }

  private String setDefaultUid(ItemStack stack) {
    String uid =  getFirstTemplateUid();
    setGenUid(stack, uid);
    return uid;
  }

  private String getGenUid(ItemStack stack, boolean setDefaultIfNull) {   
    String result = null;
    if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey("genUid")) {      
      result = stack.stackTagCompound.getString("genUid");
    }
    if(setDefaultIfNull && result == null) {
      result = setDefaultUid(stack);
    }    
    return result;     
  }
  
  private void setGenUid(ItemStack stack, String uid) {
    if (stack.stackTagCompound == null) {      
      stack.stackTagCompound = new NBTTagCompound();
    }
    if(uid == null) {
      stack.stackTagCompound.removeTag("genUid");
    } else {
      stack.stackTagCompound.setString("genUid", uid);
    }
  }
  
  private String getFirstTemplateUid() {
    Iterator<StructureTemplate> it = StructureRegister.instance.getStructureTemplates().iterator();
    if(it.hasNext()) {
      return it.next().getUid();
    }
    return null;
  }

}
