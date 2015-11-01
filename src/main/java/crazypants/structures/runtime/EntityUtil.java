package crazypants.structures.runtime;

import crazypants.structures.api.util.Point3i;
import crazypants.structures.gen.io.JsonUtil;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityUtil {

  public static boolean canSpawnEntity(World world, EntityLiving entityliving, boolean doEntityChecks) {
    boolean spaceClear = world.checkNoEntityCollision(entityliving.boundingBox)
        && world.getCollidingBoundingBoxes(entityliving, entityliving.boundingBox).isEmpty()
        && (!world.isAnyLiquid(entityliving.boundingBox) || entityliving.isCreatureType(EnumCreatureType.waterCreature, false));
    if(spaceClear && doEntityChecks) {
      //Full checks for lighting, dimension etc 
      spaceClear = entityliving.getCanSpawnHere();
    }
    return spaceClear;
  }

  public static boolean spawnEntity(World world, EntityLiving entityliving, Point3i worldPos, int spawnRange, int numAttempts, boolean doEntityChecks) {

    while (numAttempts-- > 0) {
      double x = worldPos.x + (world.rand.nextDouble() - world.rand.nextDouble()) * spawnRange;
      double y = worldPos.y + world.rand.nextInt(3) - 1;
      double z = worldPos.z + (world.rand.nextDouble() - world.rand.nextDouble()) * spawnRange;
      entityliving.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360.0F, 0.0F);

      if(EntityUtil.canSpawnEntity(world, entityliving, doEntityChecks)) {
        entityliving.onSpawnWithEgg(null);
        world.spawnEntityInWorld(entityliving);
        return true;
      }
    }

    return false;
  }

  public static NBTTagCompound createEntityNBT(String entityName, String entityNbtText) {
    if(entityName == null) {
      return null;
    }
    NBTTagCompound entityNBT = JsonUtil.parseNBT(entityNbtText);
    if(entityNBT == null) {
      entityNBT = new NBTTagCompound();
    }
    entityNBT.setString("id", entityName);
    return entityNBT;
  }

  

}
