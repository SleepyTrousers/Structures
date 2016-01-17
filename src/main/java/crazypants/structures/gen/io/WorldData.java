package crazypants.structures.gen.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import crazypants.structures.EnderStructures;
import crazypants.structures.Log;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class WorldData {
  
  public static final WorldData INSTANCE = new WorldData();
  
  private WorldData() {   
  }
  
  public File getWorldSaveDir(World world) {
    File dir = new File(world.getSaveHandler().getWorldDirectory(), EnderStructures.MODID);
    dir = new File(dir, "DIM" + world.provider.getDimensionId());
    if(!dir.exists()) {
      if(!dir.mkdirs()) {
        Log.error("WorldData: Could not create directory: " + dir.getAbsolutePath());
      }
    }
    return dir;
  }

  public File getNbtSaveFile(World world, String id) {
    File res = new File(getWorldSaveDir(world), id + ".nbt");
    return res;
  }
  
  public NBTTagCompound loadNBT(World world, String id) {
    return loadNBT(getNbtSaveFile(world, id));
  }
  
  public void saveNBT(World world, String id, NBTTagCompound nbt) {
    saveNBT(getNbtSaveFile(world, id), nbt);
  }
  
  
  public NBTTagCompound loadNBT(File fromFile) {
    if(fromFile == null) {
      return null;
    }
    if(!fromFile.exists()) {
      return null;
    }
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(fromFile);
    } catch (Exception e) {
      Log.error("WorldData: Could not open nbt file for reading: " + fromFile.getAbsolutePath() + " Exception: " + e);
      return null;
    }

    try {
      return CompressedStreamTools.read(new DataInputStream(fis));      
    } catch (IOException e) {
      Log.error("WorldData: Error reading nbt file: " + fromFile.getAbsolutePath() + " Exception: " + e);
      return null;
    } finally {
      IOUtils.closeQuietly(fis);
    }
  }

  public void saveNBT(File toFile, NBTTagCompound nbt) {
    if(toFile == null || nbt == null) {
      return;
    }
    if(nbt.hasNoTags()) {
      if(toFile.exists()) {
        toFile.delete();
      }
      return;
    }
    
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(toFile, false);
    } catch (FileNotFoundException e) {
      Log.error("WorldData: could not open nbt file for writing: " + toFile.getAbsolutePath());
      return;
    }

    try {
      CompressedStreamTools.write(nbt, new DataOutputStream(fos));
    } catch (IOException e) {
      Log.error("WorldData: error writing nbt to: " + toFile.getAbsolutePath() + " Exception: " + e);
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(fos);
    }

  }
  
}
