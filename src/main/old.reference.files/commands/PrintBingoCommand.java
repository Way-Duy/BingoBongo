package com.snagtype.modbingo.commands;

import com.google.common.collect.Lists;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PrintBingoCommand implements ICommand {

    private final List aliases;
    private static final String NAME = "printbingo";
    private List<Item> itemList = null;

    public PrintBingoCommand(){
        aliases = new ArrayList();
        aliases.add("bingo printbingo");

        IForgeRegistry<Item> itemRegistry = ForgeRegistries.ITEMS;
        Iterable<Item> items = itemRegistry;
        itemList = Lists.newArrayList( items );

    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "prints randomitems";
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }

    public Item getRandomItem(){
        Random rand = new Random();
        return itemList.get(rand.nextInt(itemList.size()));
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        World world = sender.getEntityWorld();
        Item input = getRandomItem();
        String itemName = ForgeRegistries.ITEMS.getKey( input ).toString();
        sender.sendMessage(new TextComponentString(TextFormatting.WHITE + itemName));
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
