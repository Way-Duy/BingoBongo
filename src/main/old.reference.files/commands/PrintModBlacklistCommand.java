package com.snagtype.modbingo.commands;

import com.snagtype.modbingo.AdvancementConfig;
import com.snagtype.modbingo.BingoAdvancementConfig;
import com.snagtype.modbingo.ModBingoLog;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.*;
public class PrintModBlacklistCommand implements ICommand {

    private final List aliases;
    private static AdvancementConfig configDirectory;
    private static final String NAME = "printmodblacklist";
    private static File ModBlacklistDirectory;
    public PrintModBlacklistCommand(File ModBlacklistDirectory){
        aliases = new ArrayList();
        this.ModBlacklistDirectory = ModBlacklistDirectory;
        aliases.add("bingo blacklist mod print");
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Prints the mods on the Blacklist that are not allowed to generate items on the Bingo Card";
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }


    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        //prints the list of blacklisted mods aqui
        try {
            BufferedReader in = new BufferedReader(
                    new FileReader(ModBlacklistDirectory +"/ModBlacklist.json" ));

            String line;
            while ((line = in.readLine()) != null) {
                sender.sendMessage(new TextComponentString(TextFormatting.WHITE + (line)));
            }
        }
        catch (IOException e) {e.printStackTrace();
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
