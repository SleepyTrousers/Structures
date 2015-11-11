package crazypants.structures.runtime.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gson.annotations.Expose;

import crazypants.structures.api.gen.IStructure;
import crazypants.structures.api.runtime.IAction;
import crazypants.structures.api.util.Point3i;
import crazypants.structures.runtime.behaviour.Positioned;
import io.netty.buffer.ByteBuf;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class ExecuteCommandAction extends Positioned implements IAction {

  @Expose
  private List<String> commands = new ArrayList<String>();
  
  @Expose
  private String chat = "What is this for?";
  
  public ExecuteCommandAction() {   
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
      
      boolean origValue = minecraftserver.worldServers[0].getGameRules().getGameRuleBooleanValue("commandBlockOutput");      
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

    private final ChunkCoordinates coords;
    private final World world;
    private IChatComponent cp;    

    public InnerSender(World world, IStructure structure, Point3i worldPos) {
      this.world = world;      
      this.coords = new ChunkCoordinates(worldPos.x, worldPos.y, worldPos.z);      
      cp = new ChatComponentText(chat);
    }

    @Override
    public String getCommandSenderName() {
      return "EnderStructures";      
    }

    @Override
    public ChunkCoordinates getPlayerCoordinates() {
      return coords;
    }

    @Override
    public World getEntityWorld() {
      return world;
    }

    @Override
    public IChatComponent func_145748_c_() {
      return cp;
    }

    @Override
    public void addChatMessage(IChatComponent parent) {
      if(chat != null && world != null && !world.isRemote) {
        cp = new ChatComponentText(chat).appendSibling(parent);
      }
    }

    @Override
    public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_) {
      return true;
    }

    @Override
    public void func_145756_e() {
      
    }

    @Override
    public int func_145751_f() {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public void func_145757_a(ByteBuf p_145757_1_) {
      // TODO Auto-generated method stub
      
    }

  }

 

}
