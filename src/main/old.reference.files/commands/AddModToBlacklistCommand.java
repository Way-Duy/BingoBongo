package com.snagtype.modbingo.commands;

import com.snagtype.modbingo.AdvancementConfig;
import com.snagtype.modbingo.BingoAdvancementConfig;
import com.snagtype.modbingo.BingoModRandomizor;
import com.snagtype.modbingo.ModBingoLog;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.*;
import org.apache.commons.io.FileUtils;

public class AddModToBlacklistCommand implements ICommand {

    private final List aliases;
    public static File configDirectory = BingoModRandomizor.configDirectory;
    private static final String FILE_NAME = "/ModBlacklist.json";
    private static final String NAME = "blacklistaddmod";
    private static File ModBlacklistDirectory;
    public AddModToBlacklistCommand(File ModBlacklistDirectory, AdvancementConfig configDirectory){
        aliases = new ArrayList();
        this.ModBlacklistDirectory = ModBlacklistDirectory;
        aliases.add("bingo blacklist add mod");
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "Adds mod to bingo blacklist. It will not generate any items on the Bingo Card";
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }


    JsonObject convertModToJSON (String modName)
    {
        JsonObject modObj = new JsonObject();
        modObj.addProperty("Blacklist", modName);
        return modObj;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        boolean match = false;
        String modName = args[0];
        // get mod names list
        List<String> modList = new ArrayList<>();
        try {
            Object obj = new JsonParser().parse(new FileReader(configDirectory + "/items.json"));
            JsonObject itemsJson = (JsonObject) obj;
            JsonArray modNamesArray = (JsonArray) itemsJson.get("modnames");
            //Iterating the contents of the array
            Iterator<JsonElement> iterator = modNamesArray.iterator();

            while (iterator.hasNext()) {
                String currMod = iterator.next().toString();
                currMod = currMod.substring(1,currMod.length()-1);

                ModBingoLog.info("currmod: /" + currMod + "/");
                ModBingoLog.info("modName: /" + modName +"/");
                if (modName.equals(currMod)) {
                    match = true;
                }

                ModBingoLog.info("match: " + match);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (match) {
            try {

                // Open given file in append mode.
                BufferedWriter out = new BufferedWriter(
                        new FileWriter(configDirectory +FILE_NAME, true));
                out.write(modName + "\n");
                out.close();
                sender.sendMessage(new TextComponentString(TextFormatting.WHITE + (modName + " added to Bingo Blacklist")));
            } catch (IOException e) {
                ModBingoLog.warn("Adding  " + modName + " to blacklist unsuccessful");
                ModBingoLog.warn(e);
                return;
            }
        }
        else {
            sender.sendMessage(new TextComponentString(TextFormatting.WHITE + (modName + " not found on Bingo Blacklist")));
            return;
        }

        ModBingoLog.info("Adding  " + modName + " to blacklist successful", ModBlacklistDirectory);
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
