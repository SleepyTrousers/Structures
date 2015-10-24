package crazypants.structures.api.runtime;

import net.minecraft.nbt.NBTTagCompound;

public interface IStateful {

  NBTTagCompound getState();
    
}
