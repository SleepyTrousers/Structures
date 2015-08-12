package crazypants.structures.gen;

import java.util.ArrayList;
import java.util.List;

import crazypants.structures.item.ExportManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

public class ReloadConfigCommand extends CommandBase {

  public static final String NAME = "reloadStructGen";

    private final List<String> names = new ArrayList<String>();

//  @Override
//  public String getCommandName() {
//    return NAME;
//  }
//  
////  public String getName() {
////    return NAME;
////  }
//
//  @Override
//  public List getCommandAliases() {
//    return Lists.newArrayList(NAME, "rSG");
//  }
//
//  @Override
//  public String getCommandUsage(ICommandSender sender) {
//    return NAME + " will reload the config for structure generation";
//  }
//
//  @Override
//  public int getRequiredPermissionLevel() {
//    return 2;
//  }
//
//  @Override
//  public void processCommand(ICommandSender sender, String[] args) throws CommandException {
//    StructureRegister.instance.reload();
//    ExportManager.instance.loadExportFolder();
//    sender.addChatMessage(new ChatComponentText("Reloaded Structure Generation Configs"));
//  }

    public ReloadConfigCommand() {
      names.add(NAME);
      names.add("rSG");
    }
    
    @Override
    public int compareTo(Object o) {
      return 0;
    }
  
    @Override
    public String getCommandName() {  
      return NAME;
    }
  
    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
      return NAME + " will reload the config for structure generation";
    }
  
    @SuppressWarnings("rawtypes")
    @Override
    public List getCommandAliases() {
      return names;
    }
  
    @Override
    public void processCommand(ICommandSender sender, String[] args) {    
      StructureRegister.instance.reload();    
      ExportManager.instance.loadExportFolder();
      sender.addChatMessage(new ChatComponentText("Reloaded Structure Generation Configs"));
    }
  
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
      return true;
    }
  
    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
      return false;
    }
  
    @Override
    public List<?> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {   
      return null;
    }

}
