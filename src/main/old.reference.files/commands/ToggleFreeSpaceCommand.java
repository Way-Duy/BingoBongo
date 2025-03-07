package com.snagtype.modbingo.commands;

import com.google.common.collect.Lists;
import com.snagtype.modbingo.AdvancementConfig;
import com.snagtype.modbingo.BingoAdvancementConfig;
import com.snagtype.modbingo.ModBingoLog;
import com.snagtype.utils.RandomItems;
import com.sun.security.auth.login.ConfigFile;
import export.json.JsonExportProcess;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ToggleFreeSpaceCommand implements ICommand {

    private final List aliases;
    private static AdvancementConfig configDirectory;
    private static final String NAME = "togglefreespace";
    private BingoAdvancementConfig config;
    public ToggleFreeSpaceCommand(BingoAdvancementConfig config){
        aliases = new ArrayList();
        aliases.add("togglefree");
        this.config = config;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "switches the free space on or off";
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }


    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        config.toggleFreeSpace();
        if (config.isFreeSpaceEnabled())
        {

            sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Free Space Now Enabled"));
        }
        else {
            sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Free Space Now Disabled"));
        }
    }
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
