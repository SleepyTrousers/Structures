package crazypants.structures.runtime.behaviour.vspawner;

import java.lang.reflect.Field;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import crazypants.structures.Log;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class DespawnController {

  private static final String KEY_DESPAWN_TIME = "enabledDespawnAt";
  private static DespawnController INSTANCE = null;

  public static DespawnController getInstance() {
    if(INSTANCE != null) {
      return INSTANCE;
    }
    DespawnController res = new DespawnController();
    MinecraftForge.EVENT_BUS.register(res);
    INSTANCE = res;
    return INSTANCE;
  }

  private Field fieldpersistenceRequired;

  private DespawnController() {
    try {
      fieldpersistenceRequired = ReflectionHelper.findField(EntityLiving.class, "field_82179_bU", "persistenceRequired");
    } catch (Exception e) {
      Log.error("DespawnController: Could not find field: persistenceRequired");
    }
  }
    
  
  @SubscribeEvent
  public void onLivingUpdate(LivingUpdateEvent livingUpdate) {

    Entity ent = livingUpdate.entityLiving;
    if(!ent.getEntityData().hasKey(KEY_DESPAWN_TIME)) {
      return;
    }
    if(fieldpersistenceRequired == null) {
      ent.getEntityData().removeTag(KEY_DESPAWN_TIME);
      return;
    }

    long despawnTime = ent.getEntityData().getLong(KEY_DESPAWN_TIME);    
    if(despawnTime <= livingUpdate.entity.worldObj.getTotalWorldTime() ) {
      try {
        fieldpersistenceRequired.setBoolean(livingUpdate.entityLiving, false);
        ent.getEntityData().removeTag(KEY_DESPAWN_TIME);
      } catch (Exception e) {
        Log.warn("BlockPoweredSpawner.onLivingUpdate: Error occured allowing entity to despawn: " + e);
        ent.getEntityData().removeTag(KEY_DESPAWN_TIME);
      }
    }
  }

}
