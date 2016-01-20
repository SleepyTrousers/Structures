package crazypants.structures.runtime.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.google.gson.annotations.Expose;
import com.mojang.authlib.GameProfile;

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
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

public class ExecuteCommandAction extends PositionedType implements IAction {

  @ListElementType(elementType=String.class)
  @Expose
  private List<String> commands = new ArrayList<String>();
  
  @Expose
  private String chat = "What is this for?";
  
  public ExecuteCommandAction() { 
    super("ExecuteCommand");
  }

  @Override
  public IAction createInstance(World world, IStructure structure, NBTTagCompound state) {
    return this;
  }

  @Override
  public void doAction(World world, IStructure structure, Point3i worldPos) {
    if(commands.isEmpty()) {
      return;
    }
    MinecraftServer minecraftserver = MinecraftServer.getServer();
    if(minecraftserver != null) {           
      Point3i wp = getWorldPosition(structure, worldPos);            
      InnerSender sender = new InnerSender(world, structure, wp);      
      ICommandManager icommandmanager = minecraftserver.getCommandManager();
      
      boolean origValue = minecraftserver.worldServers[0].getGameRules().getBoolean("commandBlockOutput");      
      minecraftserver.worldServers[0].getGameRules().setOrCreateGameRule("commandBlockOutput", "false");
      
      for(String cmd : commands) {
        icommandmanager.executeCommand(sender, cmd);
      }     
      
      minecraftserver.worldServers[0].getGameRules().setOrCreateGameRule("commandBlockOutput", origValue + "");
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
    if(newCommands == null) {
      return;
    }
    commands.addAll(newCommands);
  }
  
  public void addCommands(String... commandsToAdd) {
    if(commandsToAdd == null) {
      return;
    }
    for(String command : commandsToAdd) {
      commands.add(command);
    }    
  }

  public String getChat() {
    return chat;
  }

  public void setChat(String chat) {
    this.chat = chat;
  }

  //RConConsoleSource
 // private class InnerSender extends RConConsoleSource {//implements ICommandSender {
  private class InnerSender extends CommandBlockLogic {//implements ICommandSender {

    private final BlockPos coords;
    private final World world;
    private final FakePlayer fp;  

    public InnerSender(World world, IStructure structure, Point3i worldPos) {
      if(world instanceof WorldServer) {        
        fp = new FakePlayer((WorldServer)world, new GameProfile(UUID.fromString("41C82C87-7AfB-4024-BA57-13D000000E77"), "EnderStructures"));
      } else {
        fp = null;
      }
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
    public int func_145751_f() {
      return 0;
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
      return new Vec3(0,0,0);
    }

    @Override
    public Entity getCommandSenderEntity() {
      return fp;
    }

    @Override
    public void updateCommand() {            
    }

  }

 

}
