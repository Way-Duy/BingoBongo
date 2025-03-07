package com.snagtype.modbingo.commands;

import com.google.common.collect.Lists;
import com.snagtype.modbingo.BingoAdvancementConfig;
import com.snagtype.modbingo.BingoAdvancementPage;
import com.snagtype.modbingo.ModBingoLog;
import com.snagtype.utils.RandomItems;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CreateBingoCommand implements ICommand {

    private final List aliases;
    private static final String NAME = "createbingocard";
    private List<Item> itemList = null;
    private static final String ADVANCEMENT_DIRECTORY_SUFFIX = "/data/advancements/bingo";
    private static final int DEFAULT_BINGO_ITEMS = 25;
    private File advancementDirectory;
    private BingoAdvancementConfig config;
    public CreateBingoCommand(BingoAdvancementConfig config){
        aliases = new ArrayList();
        aliases.add("create");
        this.config = config;
        this.advancementDirectory = new File(DimensionManager.getCurrentSaveRootDirectory()+ ADVANCEMENT_DIRECTORY_SUFFIX);
        ModBingoLog.info(advancementDirectory.toString());
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "generates a new bingo board in your advancements tab";
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
        itemList = RandomItems.getRandomItemList(DEFAULT_BINGO_ITEMS);
        final BingoAdvancementPage process = new BingoAdvancementPage(this.advancementDirectory,itemList ,config.isFreeSpaceEnabled() ); // need itemforge list of 25 items
        final Thread BingoAdvancementPageThread = new Thread(process);

        FMLCommonHandler.instance().getMinecraftServerInstance().setMOTD("BINGO SERVER");
        this.startService("BingoMod Creating Advancements Page",BingoAdvancementPageThread);

        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Bingo Card generated in Advancements"));
    }
    private void startService( final String serviceName, final Thread thread )
    {
        thread.setName( serviceName );
        thread.setPriority( Thread.MIN_PRIORITY );
        ModBingoLog.info( "Starting " + serviceName );
        thread.start();
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
