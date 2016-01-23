package crazypants.structures.runtime.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gson.annotations.Expose;

import crazypants.structures.api.ListElementType;
import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.IAction;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.runtime.PositionedType;
import io.netty.buffer.ByteBuf;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ExecuteCommandAction extends PositionedType implements IAction {

  @ListElementType(elementType = String.class)
  @Expose
  private List<String> commands = new ArrayList<String>();

  public ExecuteCommandAction() {
    super("ExecuteCommand");
  }

  @Override
  public IAction createInstance(World world, IStructure structure, NBTTagCompound state) {
    return this;
  }

  @Override
  public void doAction(World world, IStructure structure, Point3i worldPos) {
    if (commands.isEmpty() || world.isRemote) {
      return;
    }
    MinecraftServer minecraftserver = MinecraftServer.getServer();
    if (minecraftserver != null) {
      Point3i wp = getWorldPosition(structure, worldPos);
      InnerSender sender = new InnerSender(world, structure, wp);
      ICommandManager icommandmanager = minecraftserver.getCommandManager();
      for (String cmd : commands) {        
        icommandmanager.executeCommand(sender, cmd);        
      }
    }
  }

  @Override
  public NBTTagCompound getState() {
    return null;
  }

  public List<String> getCommands() {
    return commands;
  }

  public void setCommands(Collection<String> newCommands) {
    commands = new ArrayList<String>();
    if (newCommands == null) {
      return;
    }
    commands.addAll(newCommands);
  }

  public void addCommands(String... commandsToAdd) {
    if (commandsToAdd == null) {
      return;
    }
    for (String command : commandsToAdd) {
      commands.add(command);
    }
  }

  private class InnerSender extends CommandBlockLogic {

    private final BlockPos coords;
    private final World world;    

    public InnerSender(World world, IStructure structure, Point3i worldPos) {      
      this.world = world;
      this.coords = new BlockPos(worldPos.x, worldPos.y, worldPos.z);

    }

    @Override
    public World getEntityWorld() {
      return world;
    }

    @Override
    public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_) {
      return true;
    }


    @Override
    public void func_145757_a(ByteBuf p_145757_1_) {
    }

    @Override
    public BlockPos getPosition() {
      return coords;
    }

    @Override
    public Vec3 getPositionVector() {
      return new Vec3(coords.getX() + 0.5, coords.getY() + 0.5, coords.getZ() + 0.5);
    }

    @Override
    public Entity getCommandSenderEntity() {
      return null;
    }
  
    public void addChatMessage(IChatComponent component) {      
    }
    
    public boolean sendCommandFeedback() {
      return false;
    }

    @Override
    public void updateCommand() {      
    }

    @Override
    public int func_145751_f() {      
      return 0;
    }

  }

}
