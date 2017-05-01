package com.zixiken.dimdoors.shared.commands;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.*;
import com.zixiken.dimdoors.shared.tileentities.TileEntityDimDoor;
import com.zixiken.dimdoors.shared.util.Location;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PocketCommand extends CommandBase {

    private final List<String> aliases;

    public PocketCommand() {
        aliases = new ArrayList<>();

        aliases.add("dimpocket");
    }

    @Override
    public String getName() {
        return "dimpocket";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "dimpocket <group> <name>";
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if (sender instanceof EntityPlayerMP) {
            EntityPlayerMP player = getCommandSenderAsPlayer(sender);
            if (areArgumentsValid(args, player)) {
                DimDoors.log(this.getClass(), "Executing command");

                BlockPos pos = player.getPosition();
                World world = player.world;
                Location playerLoc = new Location(world, pos);

                PocketTemplate template = SchematicHandler.INSTANCE.getDungeonTemplate(args[0], args[1]);
                Pocket pocket = PocketRegistry.INSTANCE.generatePocketAt(EnumPocketType.DUNGEON, 1, playerLoc, template);
                int entranceDoorID = pocket.getEntranceDoorID();
                RiftRegistry.INSTANCE.setLastGeneratedEntranceDoorID(entranceDoorID);
            }
        } else {
            DimDoors.log(this.getClass(), "Not executing command, because it wasn't sent by a player.");
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        if (args == null || args.length < 2) { //counts an empty ("") argument as an argument as well...
            return SchematicHandler.INSTANCE.getDungeonTemplateGroups();
        } else if (args.length == 2) {
            return SchematicHandler.INSTANCE.getDungeonTemplateNames(args[0]);
        } else if (args.length == 3) {
            List<String> list = new ArrayList();
            list.add("Remove_this");
            return list;
        } else { 
            List<String> list = new ArrayList();
            list.add("No_seriously");
            return list;
        }
    }

    private boolean areArgumentsValid(String[] args, EntityPlayerMP player) {
        if (args.length < 2) {
            DimDoors.chat(player, "Too few arguments.");
            return false;
        } else if (args.length > 2) {
            DimDoors.chat(player, "Too many arguments.");
            return false;
        } else { //exactly 2 arguments
            if (!SchematicHandler.INSTANCE.getDungeonTemplateGroups().contains(args[0])) {
                DimDoors.chat(player, "Group not found.");
                return false;
            } else if (!SchematicHandler.INSTANCE.getDungeonTemplateNames(args[0]).contains(args[1])) {
                DimDoors.chat(player, "Schematic not found.");
                return false;
            } else {
                DimDoors.chat(player, "Generating schematic " + args[1]);
                return true;
            }
        }
    }
}
